package org.realityforge.jeo.geojson;

import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.LineString;
import org.geolatte.geom.LinearRing;
import org.geolatte.geom.MultiLineString;
import org.geolatte.geom.MultiPoint;
import org.geolatte.geom.MultiPolygon;
import org.geolatte.geom.Point;
import org.geolatte.geom.PointCollection;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CrsId;

public class GeoJsonWriter
{
  public void write( @Nonnull final JsonGenerator g,
                     @Nonnull final GjElement element )
  {
    if ( element instanceof GjGeometry )
    {
      write( g, (GjGeometry) element );
    }
    else if ( element instanceof GjGeometryCollection )
    {
      write( g, (GjGeometryCollection) element );
    }
    else if ( element instanceof GjFeature )
    {
      write( g, (GjFeature) element );
    }
    else if ( element instanceof GjFeatureCollection )
    {
      write( g, (GjFeatureCollection) element );
    }
    else
    {
      throw new IllegalStateException( "Unknown element: " + element );
    }
  }

  public void write( @Nonnull final JsonGenerator g,
                     @Nonnull final GjGeometry element )
  {
    g.writeStartObject();
    writeGeometryBody( g, element );
    g.writeEnd();
  }

  private void writeGeometryBody( final JsonGenerator g, final GjGeometry element )
  {
    final Geometry geometry = element.getGeometry();
    if ( geometry instanceof Point )
    {
      writeHeader( g, "Point", element, true );

      final Point p = (Point) geometry;
      g.writeStartArray( "coordinates" );
      writePositionBody( g, p.getX(), p.getY(), p.is3D() ? p.getZ() : null, p.isMeasured() ? p.getM() : null );
      g.writeEnd();

    }
    else if ( geometry instanceof LineString )
    {
      writeHeader( g, "LineString", element, true );

      g.writeStartArray( "coordinates" );
      writeLineStringBody( g, (LineString) geometry );
      g.writeEnd();
    }
    else if ( geometry instanceof Polygon )
    {
      writeHeader( g, "Polygon", element, true );

      final Polygon p = (Polygon) geometry;

      g.writeStartArray( "coordinates" );
      writePolygonBody( g, p );
      g.writeEnd();
    }
    else if ( geometry instanceof MultiPoint )
    {
      writeHeader( g, "MultiPoint", element, true );
      final MultiPoint mp = (MultiPoint) geometry;
      g.writeStartArray( "coordinates" );
      final int count = mp.getNumGeometries();
      for ( int i = 0; i < count; i++ )
      {
        final Point p = mp.getGeometryN( i );
        writePosition( g,
                       p.getX(),
                       p.getY(),
                       p.is3D() ? p.getZ() : null,
                       p.isMeasured() ? p.getM() : null );
      }
      g.writeEnd();
    }
    else if ( geometry instanceof MultiLineString )
    {
      writeHeader( g, "MultiLineString", element, true );
      g.writeStartArray( "coordinates" );

      final MultiLineString multiLineString = (MultiLineString) geometry;
      final int count = multiLineString.getNumGeometries();
      for ( int i = 0; i < count; i++ )
      {
        final LineString lineString = multiLineString.getGeometryN( i );
        g.writeStartArray();
        writeLineStringBody( g, lineString );
        g.writeEnd();
      }

      g.writeEnd();
    }
    else if ( geometry instanceof MultiPolygon )
    {
      writeHeader( g, "MultiPolygon", element, true );
      final MultiPolygon mp = (MultiPolygon) geometry;

      g.writeStartArray( "coordinates" );
      final int size = mp.getNumGeometries();
      for ( int i = 0; i < size; i++ )
      {
        g.writeStartArray();
        writePolygonBody( g, mp.getGeometryN( i ) );
        g.writeEnd();
      }
      g.writeEnd();
    }
    else
    {
      throw new IllegalArgumentException( "Unknown geometry type: " + geometry.getClass().getName() );
    }
  }

  private void writePolygonBody( final JsonGenerator g, final Polygon p )
  {
    final LinearRing exteriorRing = p.getExteriorRing();
    g.writeStartArray();
    writeLinearRingBody( g, exteriorRing );
    g.writeEnd();
    final int count = p.getNumInteriorRing();
    for ( int i = 0; i < count; i++ )
    {
      final LinearRing inner = p.getInteriorRingN( i );
      g.writeStartArray();
      writeLinearRingBody( g, inner );
      g.writeEnd();
    }
  }

  private void writeLineStringBody( final JsonGenerator g, final LineString lineString )
  {
    writePointCollection( g, lineString.getPoints() );
  }

  private void writeLinearRingBody( final JsonGenerator g, final LinearRing linearRing )
  {
    writePointCollection( g, linearRing.getPoints() );
  }

  private void writePointCollection( final JsonGenerator g, final PointCollection p )
  {
    final int size = p.size();
    for ( int i = 0; i < size; i++ )
    {
      writePosition( g, p.getX( i ),
                     p.getY( i ),
                     p.is3D() ? p.getZ( i ) : null,
                     p.isMeasured() ? p.getM( i ) : null );
    }
  }

  private void writePosition( @Nonnull final JsonGenerator g,
                              final double x,
                              final double y,
                              @Nullable final Double z,
                              @Nullable final Double m )
  {
    g.writeStartArray();
    writePositionBody( g, x, y, z, m );
    g.writeEnd();
  }

  private void writePositionBody( final JsonGenerator g,
                                  final double x,
                                  final double y,
                                  final Double z,
                                  final Double m )
  {
    g.write( x );
    g.write( y );
    if ( null != z )
    {
      g.write( z );
    }
    if ( null != m )
    {
      g.write( m );
    }
  }

  public void write( @Nonnull final JsonGenerator g,
                     @Nonnull final GjGeometryCollection element )
  {
    g.writeStartObject();

    writeHeader( g, "GeometryCollection", element, true );

    g.writeStartArray( "geometries" );
    for ( final GjGeometry geometry : element.getCollection() )
    {
      write( g, geometry );
    }
    g.writeEnd();

    g.writeEnd();
  }

  public void write( @Nonnull final JsonGenerator g,
                     @Nonnull final GjFeature element )
  {
    g.writeStartObject();
    writeHeader( g, "Feature", element, false );
    if ( JsonValue.NULL != element.getId() )
    {
      g.write( "id", element.getId() );
    }
    final GjGeometry geometry = element.getGeometry();
    if ( null != geometry )
    {
      g.writeStartObject( "geometry" );
      writeGeometryBody( g, geometry );
      g.writeEnd();
    }
    else
    {
      final GjGeometryCollection geometryCollection = element.getGeometryCollection();
      if ( null != geometryCollection )
      {
        g.writeStartObject( "geometry" );
        write( g, geometryCollection );
        g.writeEnd();
      }
      else
      {
        g.write( "geometry", JsonValue.NULL );
      }
    }

    g.writeStartObject( "properties" );
    writeProperties( g, element.getAdditionalProperties() );
    g.writeEnd();

    g.writeEnd();
  }

  public void write( @Nonnull final JsonGenerator g,
                     @Nonnull final GjFeatureCollection element )
  {
    g.writeStartObject();
    writeHeader( g, "FeatureCollection", element, true );
    g.writeStartArray( "features" );
    for ( final GjFeature feature : element.getCollection() )
    {
      write( g, feature );
    }
    g.writeEnd();

    g.writeEnd();
  }

  private void writeHeader( final JsonGenerator g,
                            final String type,
                            @Nonnull final GjElement element,
                            final boolean writeProperties )
  {
    g.write( "type", type );
    writeBbox( g, element.getBBox() );
    writeCrsId( g, element.getCrsId() );
    if ( writeProperties )
    {
      writeProperties( g, element.getAdditionalProperties() );
    }
  }

  private void writeCrsId( final JsonGenerator g, final CrsId crsId )
  {
    if ( null != crsId && CrsId.UNDEFINED != crsId )
    {
      g.writeStartObject( "crs" );
      if ( CrsId.DEFAULT_AUTHORITY.equals( crsId.getAuthority() ) )
      {
        g.write( "type", crsId.getAuthority() );
        g.writeStartObject( "properties" );
        g.write( "code", String.valueOf( crsId.getCode() ) );
        g.writeEnd();
      }
      else
      {
        g.write( "type", "name" );
        g.writeStartObject( "properties" );
        g.write( "name", crsId.getAuthority() + ":" + crsId.getCode() );
        g.writeEnd();
      }

      g.writeEnd();
    }
  }

  private void writeBbox( final JsonGenerator g, final Envelope bbox )
  {
    if ( null != bbox )
    {
      g.writeStartArray( "bbox" );
      writePositionBody( g, bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY() );
      g.writeEnd();
    }
  }

  private void writeProperties( final JsonGenerator g, final Map<String, JsonValue> properties )
  {
    for ( final Entry<String, JsonValue> entry : properties.entrySet() )
    {
      g.write( entry.getKey(), entry.getValue() );
    }
  }
}
