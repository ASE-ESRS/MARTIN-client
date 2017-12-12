package ase_esrs.martinsmap.ui.fragments;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider.Builder;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ase_esrs.martinsmap.ui.activities.MainActivity;
import util.CrimeClusterItem;
import util.JSONArrayUtils;
import util.LatLonBoundary;
import util.Prices;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String SERVER_URI = "https://4wmuzhlr5b.execute-api.eu-west-2.amazonaws.com/prod/martinServer";
    private final static String POLICE_URI = "https://data.police.uk/api/crimes-street/all-crime";

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LatLng lastLocation;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    Marker mCurrLocationMarker;
    ClusterManager<CrimeClusterItem> mClusterManager;
    Snackbar loadingBar;

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleMap != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            mGoogleMap.setMapType(sharedPreferences.getBoolean("satelliteDisplayMode", false) ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);
        }
        setUpMapIfNeeded();
    }

    public void setUpMapIfNeeded() {
        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        mGoogleMap.setMapType(sharedPreferences.getBoolean("satelliteDisplayMode", false) ? GoogleMap.MAP_TYPE_HYBRID : GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(49.82380908513249, -10.8544921875), new LatLng(59.478568831926395, 2.021484375)));
        mGoogleMap.setOnMapLongClickListener((point) -> {
            lastLocation = new LatLng(point.latitude, point.longitude);
            updateMap();
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                checkPermission(LOCATION_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else {
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        loadingBar = Snackbar.make(getView(), "Loading House Prices Paid Data...", Snackbar.LENGTH_INDEFINITE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        queue = Volley.newRequestQueue(getActivity());
        checkPermission(INTERNET_PERMISSION, Manifest.permission.INTERNET);

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        // If location not enabled default to Brighton
        lastLocation = new LatLng(50.8375054,-0.1762299);
        updateMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Martin's Maps", "Location Updated");
        if (mLastLocation == null) {
            mLastLocation = location;
            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
            updateMap();
        }
    }

    public void updateMap(double latitude, double longitude) {
        lastLocation = new LatLng(latitude, longitude);
        updateMap();
    }

    private void updateMap() {
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 15));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(lastLocation);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        requestHousePricesPaidData();
        requestCrimeData();
    }

    private void checkPermission(final int permissionConstant, final String manifestPermissionConstant) {
        if (ContextCompat.checkSelfPermission(getActivity(), manifestPermissionConstant)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    manifestPermissionConstant)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Permission Needed")
                        .setMessage("This app needs the " + manifestPermissionConstant + " permission, please accept to use related functionality.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{manifestPermissionConstant},
                                        permissionConstant);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{manifestPermissionConstant},
                        permissionConstant);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                }
                break;
            case INTERNET_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestHousePricesPaidData();
                }
                break;
        }
    }

    private void requestHousePricesPaidData() {
        loadingBar.show();
        int radius = Integer.parseInt(sharedPreferences.getString("radius", "50"));
        LatLonBoundary boundary = new LatLonBoundary(lastLocation.latitude, lastLocation.longitude, radius);
        String requestUrl = SERVER_URI + "?start_latitude=" + boundary.getLatFrom()
                + "&start_longitude=" + boundary.getLonFrom()
                + "&end_latitude=" + boundary.getLatTo()
                + "&end_longitude=" + boundary.getLonTo()
                + "&distance=" + radius;
        Log.d("Martin's Map", requestUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                addHeatMap(response);
                loadingBar.dismiss();
            }
        }, (error) -> {
            Log.e("Martin's Maps", "A network error occurred: " + error.toString());
            loadingBar.dismiss();
            Snackbar.make(getView(), "A network error occurred.", Snackbar.LENGTH_SHORT).show();
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }

    private void addHeatMap(JSONArray array) {
        try {
            if (array.length() == 0) {
                Snackbar.make(getView(), "There is no price paid data available for this location.", Snackbar.LENGTH_SHORT).show();
            } else {
                ArrayList<WeightedLatLng> locations = new ArrayList<>();
                int min = JSONArrayUtils.getMinPrice(array);
                int max = JSONArrayUtils.getMaxPrice(array);
                int average = JSONArrayUtils.getAveragePrice(array);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    LatLng loc = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
                    double weight = sharedPreferences.getBoolean("relativePricing", false) ? Prices.priceIntensity(obj.getInt("price"), max, min) : Prices.priceIntensity(obj.getInt("price"));
                    locations.add(new WeightedLatLng(loc, weight));
                }
                Log.d("Martin's Maps", array.toString());
                HeatmapTileProvider heatmapTileProvider = new Builder().weightedData(locations).build();
                mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(heatmapTileProvider));
                if(sharedPreferences.getBoolean("relativePricing", false)) {
                    ((MainActivity) getActivity()).setHeatmapKey(max, average, min);
                } else {
                    ((MainActivity) getActivity()).setHeatmapKey(400000, 262500,125000);
                }
            }
        } catch (JSONException ex) {
            Log.e("Martin's Maps", ex.getMessage());
        }
    }

    /**
     * Makes an API request to Police UK to obtain nearby crime data.
     * Uses the current location and search radius.
     * @author Loic Verrall
     */
    private void requestCrimeData() {
        int radius = Integer.parseInt(sharedPreferences.getString("radius", "50"));
        LatLonBoundary boundary = new LatLonBoundary(lastLocation.latitude, lastLocation.longitude, radius);
        String requestUrl = POLICE_URI + "?poly="
                + boundary.getLatFrom() + "," + boundary.getLonFrom() + ":"
                + boundary.getLatTo() + "," + boundary.getLonFrom() + ":"
                + boundary.getLatTo() + "," + boundary.getLonTo() + ":"
                + boundary.getLatFrom() + "," + boundary.getLonTo() + ":";
        Log.d("Martin's Map", "Crime URI: " + requestUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                addCrimeData(response);
            }
        }, (error) -> {
            Log.e("Martin's Maps", "A network error occurred: " + error.toString());
            Toast.makeText(getActivity(), "Network error occurred", Toast.LENGTH_SHORT).show();
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }

    /**
     * Responsible for plotting the crime data (received as a JSON array) on the map.
     * @param array the JSON array of crime data to display.
     * @author Loic Verrall
     */
    private void addCrimeData(JSONArray array) {
        try {
            if (array.length() == 0) {
                Toast.makeText(getActivity(), "No crime data available", Toast.LENGTH_SHORT).show();
            } else {
                mClusterManager = new ClusterManager<CrimeClusterItem>(getActivity(), mGoogleMap);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    // Extract the location
                    double latitude = obj.getJSONObject("location").getDouble("latitude");
                    double longitude = obj.getJSONObject("location").getDouble("latitude");

                    // Retrieve the crime category.
                    String category = obj.getString("category");

                    CrimeClusterItem item = new CrimeClusterItem(latitude, longitude, category);
                    mClusterManager.addItem(item);
                }

                mGoogleMap.setOnCameraIdleListener(mClusterManager);
            }
        } catch (JSONException ex) {
            Log.e("Martin's Maps", ex.getMessage());
        }
    }

}