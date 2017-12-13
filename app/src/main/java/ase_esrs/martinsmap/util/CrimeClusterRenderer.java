package ase_esrs.martinsmap.util;

import android.content.Context;
import android.graphics.BitmapRegionDecoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ase_esrs.martinsmap.R;

/**
 * Created by loicverrall on 12/12/2017.
 */

public class CrimeClusterRenderer extends DefaultClusterRenderer {

    public CrimeClusterRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.crime_marker);
        markerOptions.icon(icon);
        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
