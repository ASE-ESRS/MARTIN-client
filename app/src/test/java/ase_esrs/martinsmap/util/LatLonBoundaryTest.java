package ase_esrs.martinsmap.util;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by markp on 13/12/2017.
 */

public class LatLonBoundaryTest {

    double lat;
    double lon;
    int radius;
    LatLonBoundary lb;
    double posDouble;
    double negDouble;
    double oorLat;
    double oorLon;

    @Before
    public void setUp() {
        lat = 50.865561;
        lon = -0.086838;
        radius = 500;
        lb = new LatLonBoundary(lat, lon, radius);
        posDouble = 1.8;
        negDouble = -1.8;
        oorLat = -90.01;
        oorLon = -180.01;
    }

    @Test
    public void testConstructor() throws Exception {
        assertEquals(lb.getLatFrom(), lat - Math.abs(radius * (1 / (110.574 * 1000))));
        assertEquals(lb.getLatTo(), lat + Math.abs(radius * (1 / (110.574 * 1000))));
        assertEquals(lb.getLonFrom(), lon - Math.abs(radius * (1 / (111.320 * 1000))));
        assertEquals(lb.getLonTo(), lon + Math.abs(radius * (1 / (111.320 * 1000))));
    }

    @Test
    public void testLatFrom() throws Exception {
        assertTrue(lat > lb.getLatFrom());
    }

    @Test
    public void testLatTo() throws Exception {
        assertTrue(lat < lb.getLatTo());
    }

    @Test
    public void testLonFrom() throws Exception {
        assertTrue(lon > lb.getLonFrom());
    }

    @Test
    public void testLonTo() throws Exception {
        assertTrue(lon < lb.getLonTo());
    }

    @Test
    public void testSetLatFrom() {
        lb.setLatFrom(posDouble);
        assertEquals(lb.getLatFrom(), posDouble);
        lb.setLatFrom(negDouble);
        assertEquals(lb.getLatFrom(), negDouble);
        lb.setLatFrom(oorLat);
        assertFalse(lb.getLatFrom() == oorLat);
        assertEquals(lb.getLatFrom(), negDouble);
    }

    @Test
    public void testSetLatTo() {
        lb.setLatTo(posDouble);
        assertEquals(lb.getLatTo(), posDouble);
        lb.setLatTo(negDouble);
        assertEquals(lb.getLatTo(), negDouble);
        lb.setLatFrom(oorLat);
        assertFalse(lb.getLatTo() == oorLat);
        assertEquals(lb.getLatTo(), negDouble);
    }

    @Test
    public void testSetLonFrom() {
        lb.setLonFrom(posDouble);
        assertEquals(lb.getLonFrom(), posDouble);
        lb.setLonFrom(negDouble);
        assertEquals(lb.getLonFrom(), negDouble);
        lb.setLonFrom(oorLon);
        assertFalse(lb.getLonFrom() == oorLon);
        assertEquals(lb.getLonFrom(), negDouble);
    }

    @Test
    public void testSetLonTo() {
        lb.setLonTo(posDouble);
        assertEquals(lb.getLonTo(), posDouble);
        lb.setLonTo(negDouble);
        assertEquals(lb.getLonTo(), negDouble);
        lb.setLonTo(oorLon);
        assertFalse(lb.getLonTo() == oorLon);
        assertEquals(lb.getLonTo(), negDouble);
    }

}
