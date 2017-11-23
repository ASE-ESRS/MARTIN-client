package ase_esrs.martinsmap.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

/**
 * Handles the saving and loading of preferences (such as the search radius).
 *
 * Created by loicverrall on 23/11/2017.
 */

public class PersistantStorageManager {

    public static PersistantStorageManager sharedInstance = new PersistantStorageManager();

    private int searchRadius = 10;

    /**
     * Saves the supplied radius to persistent storage.
     *
     * @param searchRadius the updated radius of the search (in meters).
     */
    public void saveSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    /**
     * Loads the search radius value from persistent storage.
     *
     * @return an `int` representing the number of meters.
     */
    public int getSearchRadius() {
        return searchRadius;
    }
}
