package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ase_esrs.martinsmap.ui.activities.SettingsActivity;

public class DefaultsManager {

    // Singleton instance.
    public static DefaultsManager sharedInstance = new DefaultsManager();

    public void setDefaults(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultRadius = prefs.getString("radius", null);
        if (defaultRadius == null) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("radius", "50");
            editor.apply();
        }
    }
}
