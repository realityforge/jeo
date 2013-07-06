package org.realityforge.jeo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonValue;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

public final class GjFeatureCollection
  extends GjElement
{
  private final List<GjFeature> _collection;

  public GjFeatureCollection( @Nonnull final List<GjFeature> collection,
                              @Nullable final CrsId crsId,
                              @Nullable final Envelope bbox,
                              @Nullable final Map<String, JsonValue> additionalProperties )
  {
    super( crsId, bbox, additionalProperties );
    _collection = Collections.unmodifiableList( collection );
  }

  @Nonnull
  public List<GjFeature> getCollection()
  {
    return _collection;
  }

  @Override
  protected boolean isPropertyAllowed( final String name )
  {
    return super.isPropertyAllowed( name ) && !( "features".equals( name ) );
  }
}
