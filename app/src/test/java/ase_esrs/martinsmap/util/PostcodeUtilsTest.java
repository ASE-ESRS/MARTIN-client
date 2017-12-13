package ase_esrs.martinsmap.util;

import org.junit.Test;

import ase_esrs.martinsmap.util.PostcodeUtils;

import static org.junit.Assert.*;

/**
 * Created by Dan on 11/12/2017.
 */
public class PostcodeUtilsTest {

    @Test
    public void testEmptyString() {
        assertFalse(PostcodeUtils.isValidPostcode(""));
    }

    @Test
    public void testRandomStrings() {
        assertFalse(PostcodeUtils.isValidPostcode("Hello World!"));
        assertFalse(PostcodeUtils.isValidPostcode("Another test"));
        assertFalse(PostcodeUtils.isValidPostcode("Hello Goodbye"));
        assertFalse(PostcodeUtils.isValidPostcode("this is another test"));
    }

    @Test
    public void testRegularPostcodesUpperNoSpaces() {
        assertTrue(PostcodeUtils.isValidPostcode("SS55HJ"));
        assertTrue(PostcodeUtils.isValidPostcode("SS43BP"));
        assertTrue(PostcodeUtils.isValidPostcode("BN14GE"));
        assertTrue(PostcodeUtils.isValidPostcode("BN17HL"));
        assertTrue(PostcodeUtils.isValidPostcode("BN19QU"));
    }

    @Test
    public void testRegularPostcodesUpperSpaces() {
        assertTrue(PostcodeUtils.isValidPostcode("SS5 5HJ"));
        assertTrue(PostcodeUtils.isValidPostcode("SS4 3BP"));
        assertTrue(PostcodeUtils.isValidPostcode("BN1 4GE"));
        assertTrue(PostcodeUtils.isValidPostcode("BN1 7HL"));
        assertTrue(PostcodeUtils.isValidPostcode("BN1 9QU"));
    }

    @Test
    public void testRegularPostcodesLowerNoSpaces() {
        assertTrue(PostcodeUtils.isValidPostcode("ss55hj"));
        assertTrue(PostcodeUtils.isValidPostcode("ss43bp"));
        assertTrue(PostcodeUtils.isValidPostcode("bn14ge"));
        assertTrue(PostcodeUtils.isValidPostcode("bn17hl"));
        assertTrue(PostcodeUtils.isValidPostcode("bn19qu"));
    }

    @Test
    public void testRegularPostcodesLowerSpaces() {
        assertTrue(PostcodeUtils.isValidPostcode("ss5 5hj"));
        assertTrue(PostcodeUtils.isValidPostcode("ss4 3bp"));
        assertTrue(PostcodeUtils.isValidPostcode("bn1 4ge"));
        assertTrue(PostcodeUtils.isValidPostcode("bn1 7hl"));
        assertTrue(PostcodeUtils.isValidPostcode("bn1 9qu"));
    }

    @Test
    public void testLondonPostcodes() {
        assertTrue(PostcodeUtils.isValidPostcode("W8 5XD"));
        assertTrue(PostcodeUtils.isValidPostcode("W85XD"));
        assertTrue(PostcodeUtils.isValidPostcode("w85xd"));
        assertTrue(PostcodeUtils.isValidPostcode("w8 5xd"));
        assertTrue(PostcodeUtils.isValidPostcode("W8 5xD"));
        assertTrue(PostcodeUtils.isValidPostcode("w8 5xD"));
    }

}