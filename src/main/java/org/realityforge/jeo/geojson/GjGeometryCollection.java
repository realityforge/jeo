package org.realityforge.jeo.geojson;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonObject;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

public final class GjGeometryCollection
  extends GjElement
{
  private final List<GjGeometry> _collection;

  @SuppressWarnings( "ConstantConditions" )
  public GjGeometryCollection( @Nonnull final List<GjGeometry> collection,
                               @Nullable final CrsId crsId,
                               @Nullable final Envelope bbox,
                               @Nullable final JsonObject additionalProperties )
  {
    super( crsId, bbox, additionalProperties );
    if ( null == collection )
    {
      throw new IllegalArgumentException( "collection is null" );
    }
    _collection = Collections.unmodifiableList( collection );
  }

  @Nonnull
  public List<GjGeometry> getCollection()
  {
    return _collection;
  }

  @Override
  protected boolean isPropertyAllowed( final String name )
  {
    return super.isPropertyAllowed( name ) && !( "geometries".equals( name ) );
  }
}
