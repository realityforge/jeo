package org.realityforge.jeo.geojson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonObject;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.crs.CrsId;

public final class GjGeometry
  extends GjElement
{
  @Nonnull
  private final Geometry _geometry;

  @SuppressWarnings( "ConstantConditions" )
  public GjGeometry( @Nonnull final Geometry geometry,
                     @Nullable final CrsId crsId,
                     @Nullable final Envelope bbox,
                     @Nullable final JsonObject additionalProperties )
  {
    super( crsId, bbox, additionalProperties );
    if ( null == geometry )
    {
      throw new IllegalArgumentException( "geometry is null" );
    }
    if ( geometry.getClass() == GeometryCollection.class )
    {
      throw new IllegalArgumentException( "geometry is a GeometryCollection" );
    }
    _geometry = geometry;
  }

  @Nonnull
  public Geometry getGeometry()
  {
    return _geometry;
  }

  @Override
  protected boolean isPropertyAllowed( final String name )
  {
    return super.isPropertyAllowed( name ) && !( "coordinates".equals( name ) );
  }
}
