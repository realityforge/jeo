package org.realityforge.jeo.geojson;

import java.util.ArrayList;
import javax.json.Json;
import javax.json.JsonObject;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.codec.Wkt.Dialect;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;

/**
 * A simple class used to measure times for various operations.
 */
public final class Benchmark
{
  public static void main( final String[] args )
  {
    final CrsId crsId = CrsRegistry.getCrsIdForEPSG( 3111 );
    final Envelope bbox = new Envelope( 0, 0, 2, 2, crsId );
    final JsonObject additionalProperties =
      Json.createObjectBuilder().
        add( "foo", false ).
        build();
    final Geometry geometry = fromWkT( "POINT (1 1)" );
    final GjGeometry e = new GjGeometry( geometry, crsId, bbox, additionalProperties );
    final ArrayList<GjGeometry> geometries = new ArrayList<>();
    geometries.add( e );
    final GjElement element =
      new GjGeometryCollection( geometries, crsId, bbox, additionalProperties );
    measureWritePerformance( element, 1000, 10000, 10000 );
  }

  private static void measureWritePerformance( final GjElement element,
                                               final int innerCount,
                                               final int warmupIterationCount,
                                               final int testIterationCount )
  {
    final int stringSize = element.toString().length();
    final StringBuilder sb = new StringBuilder( (stringSize + 2) * innerCount );

    // Hash code is only required to make it difficult for the
    // compiler to optimize operations out
    int hash = 0;
    long time = 0L;
    for ( int i = 0; i < ( testIterationCount + warmupIterationCount ); i++ )
    {
      final long start = System.nanoTime();
      for ( int j = 0; j < innerCount; j++ )
      {
        sb.append( element.toString() );
      }
      final long stop = System.nanoTime();
      final long delay = stop - start;

      if ( i > warmupIterationCount )
      {
        time += ( delay / 1000L );
      }
      hash += sb.charAt( i );
      sb.setLength( 0 );
    }
    final double avg = ( (double) time ) / testIterationCount;
    System.out.println( "Average: " + avg + " Hash: " + hash );
  }

  @SuppressWarnings( "unchecked" )
  private static <T extends Geometry> T fromWkT( final String wkt )
  {
    return (T) Wkt.newDecoder( Dialect.POSTGIS_EWKT_1 ).decode( wkt );
  }
}
