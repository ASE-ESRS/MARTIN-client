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
     * Saves the supplied slider value (0 - 4) to persistent storage.
     */
    public void setSliderValue(int sliderValue) {
        // Ensure we've been passed a valid slider value.
        if (sliderValue >= 0 && sliderValue <= 4) {
            this.sliderValue = sliderValue;
        } else {
            // Otherwise, switch to the default.
            this.sliderValue = 1;
        }
    }

    /**
     * Loads the search radius value (0 - 4) from persistent storage.
     */
    public int getSliderValue() {
        return sliderValue;
    }

}
