package util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by loicverrall on 12/12/2017.
 * Adapted from the Google Maps API docs: https://developers.google.com/maps/documentation/android-api/utility/marker-clustering
 * @author Loic Verrall
 */

public class CrimeClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private final String mTitle;

    public CrimeClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = "Crime";
    }

    public CrimeClusterItem(double lat, double lng, String title) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
