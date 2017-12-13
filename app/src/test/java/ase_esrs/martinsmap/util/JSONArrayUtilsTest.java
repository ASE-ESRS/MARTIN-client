package ase_esrs.martinsmap.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by loicverrall on 13/12/2017.
 */
public class JSONArrayUtilsTest {
    @Test
    public void getCrimeNameFromCategory() throws Exception {
        // Check that valid crime categories are correctly dealt with.
        assertEquals("Violence and sexual offences", JSONArrayUtils.getCrimeNameFromCategory("violent-crime"));
        // Check that invalid crime categories return the correct default result (empty String).
        assertEquals("", JSONArrayUtils.getCrimeNameFromCategory("NOT-A-CRIME"));
    }

}