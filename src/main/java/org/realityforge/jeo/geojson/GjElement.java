package org.realityforge.jeo.geojson;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.crs.CrsId;

public abstract class GjElement
{
  private static final JsonGeneratorFactory FACTORY = Json.createGeneratorFactory( new HashMap<String, Object>() );

  @Nonnull
  private final Map<String, JsonValue> _additionalProperties;
  @Nullable
  private final CrsId _crsId;
  @Nullable
  private final Envelope _bbox;

  protected GjElement( @Nullable final CrsId crsId,
                       @Nullable final Envelope bbox,
                       @Nullable final JsonObject additionalProperties )
  {
    if ( null != additionalProperties )
    {
      for ( final String key : additionalProperties.keySet() )
      {
        if ( !isPropertyAllowed( key ) )
        {
          throw new IllegalArgumentException( "Property named '" + key + "' is not allowed." );
        }
      }
    }
    _crsId = crsId;
    _bbox = bbox;
    _additionalProperties =
      null == additionalProperties ?
      Collections.<String, JsonValue>emptyMap() :
      additionalProperties;
  }

  @Nullable
  public final CrsId getCrsId()
  {
    return _crsId;
  }

  @Nullable
  public final Envelope getBBox()
  {
    return _bbox;
  }

  @Nonnull
  public final Map<String, JsonValue> getAdditionalProperties()
  {
    return _additionalProperties;
  }

  protected boolean isPropertyAllowed( final String name )
  {
    return !( "type".equals( name ) ||
              "crs".equals( name ) ||
              "bbox".equals( name ) );
  }

  @Override
  public final String toString()
  {
    /*
     * We use a cached factory here, otherwise creating the generator
     * becomes the bottleneck. Here are the timings for different
     * implementations of this method.
     *
     * 35167.862 ns - creating factory every time
     * 2483.863 ns - reusing static factory
     */

    final StringWriter writer = new StringWriter();
    final JsonGenerator g = FACTORY.createGenerator( writer );
    write( g );
    return writer.toString();
  }

  public final void write( final JsonGenerator g )
  {
    new GeoJsonWriter().write( g, this );
    g.close();
  }
}
