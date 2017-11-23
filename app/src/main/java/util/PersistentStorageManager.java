package util;

/**
 * Handles the saving and loading of preferences (such as the search radius).
 *
 * Created by loicverrall on 23/11/2017.
 */

public class PersistentStorageManager {

    public static PersistentStorageManager sharedInstance = new PersistentStorageManager();

    private int sliderValue = 1;

    /**
     * Saves the supplied slider value (0 - 5) to persistent storage.
     */
    public void saveSliderValue(int sliderValue) {
        this.sliderValue = sliderValue;
    }

    /**
     * Loads the search radius value (0 - 5) from persistent storage.
     */
    public int getSliderValue() {
        return sliderValue;
    }

}
