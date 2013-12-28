package org.realityforge.jeo.geojson;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import org.geolatte.geom.Envelope;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.crs.CrsId;
import org.geolatte.geom.crs.CrsRegistry;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class GjElementTest
  extends AbstractGjElementTest
{
  @Test
  public void basicInteraction()
    throws Exception
  {
    final Geometry geometry = fromWkT( "POINT (1 1)" );
    final CrsId crsId = CrsRegistry.getCrsIdForEPSG( 1234 );
    final Envelope bbox = new Envelope( 0, 0, 2, 2, crsId );
    final JsonObject additionalProperties =
      Json.createObjectBuilder().
        add( "foo", false ).
        build();
    final GjGeometry e = new GjGeometry( geometry, crsId, bbox, additionalProperties );
    assertEquals( e.getGeometry(), geometry );
    assertEquals( e.getBBox(), bbox );
    assertEquals( e.getCrsId(), crsId );
    assertEquals( e.getAdditionalProperties(), additionalProperties );
    assertPropertyAllowed( e, "X" );
    assertPropertyNotAllowed( e, "crs" );
    assertPropertyNotAllowed( e, "bbox" );
    assertPropertyNotAllowed( e, "type" );
    final JsonStructure actual = Json.createReader( new StringReader( e.toString() ) ).read();

    final String expectedJson =
      "{\"type\":\"Point\",\"bbox\":[0.0,0.0,2.0,2.0],\"foo\":false,\"coordinates\":[1.0,1.0]}";
    final JsonStructure expected = Json.createReader( new StringReader( expectedJson ) ).read();
    assertEquals( actual, expected );
  }

  @Test
  public void nullMetadata()
    throws Exception
  {
    final Geometry geometry = fromWkT( "POINT (1 1)" );
    final GjGeometry e = new GjGeometry( geometry, null, null, null );
    assertEquals( e.getGeometry(), geometry );
    assertNull( e.getBBox() );
    assertNull( e.getCrsId() );
    assertEquals( e.getAdditionalProperties().size(), 0 );
  }

  @Test
  public void invalidAdditionalData()
    throws Exception
  {
    verifyAdditionalDataRestricted( "type" );
    verifyAdditionalDataRestricted( "crs" );
    verifyAdditionalDataRestricted( "bbox" );
  }

  private void verifyAdditionalDataRestricted( final String key )
  {
    final JsonObject additionalProperties =
      Json.createObjectBuilder().
        add( key, false ).
        build();
    try
    {
      new GjGeometry( fromWkT( "POINT (1 1)" ), null, null, additionalProperties );
    }
    catch ( final IllegalArgumentException e )
    {
      return;
    }
    fail( "Expected to receive an exception for addition data: " + additionalProperties );
  }
}
