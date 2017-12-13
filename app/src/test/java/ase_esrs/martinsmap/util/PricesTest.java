package ase_esrs.martinsmap.util;

import org.junit.Test;

import ase_esrs.martinsmap.util.Prices;

import static org.junit.Assert.*;

/**
 * Testing class for Prices
 */
public class PricesTest {

    @Test
    public void priceIntensity() throws Exception {
        assertEquals(Prices.priceIntensity(500000), 1.0, 0.01);
        assertEquals(Prices.priceIntensity(287000), 0.5890909, 0.001);
        assertEquals(Prices.priceIntensity(125000), 0.0, 0.01);
        assertNotEquals(Prices.priceIntensity(90000), 1.0, 0.01);

        assertEquals(Prices.priceIntensity(134134), Prices.priceIntensity(134134, 400000, 125000), 0.0001);
    }

    @Test
    public void priceIntensity1() throws Exception {
        assertEquals(Prices.priceIntensity(300000, 350000, 150000), 0.75, 0.01);
        assertEquals(Prices.priceIntensity(199999, 200000, 180000), 0.99995, 0.001);
        assertEquals(Prices.priceIntensity(201000, 180000, 150000), 1.0, 0.001);
        assertEquals(Prices.priceIntensity(130000, 180000, 150000), 0.0, 0.001);
    }

}