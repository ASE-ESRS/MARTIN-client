package util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by loicverrall on 23/11/2017.
 */
public class PersistentStorageManagerTest {

    PersistentStorageManager manager;

    @Before
    public void setUp() throws Exception {
        manager = PersistentStorageManager.sharedInstance;
    }

    @Test
    public void testPersistentStorageManager() {
        assertEquals(manager.getSliderValue(), 1);
    }

    @Test
    public void setSliderValue() throws Exception {
        // Checks that the value can only be assigned to values between 0 and 4.
        manager.setSliderValue(6);
        assertEquals(manager.getSliderValue(), 1);

        manager.setSliderValue(-1);
        assertEquals(manager.getSliderValue(), 1);
    }

    @Test
    public void getSliderValue() throws Exception {
        manager.setSliderValue(3);
        assertEquals(manager.getSliderValue(), 3);
    }

}