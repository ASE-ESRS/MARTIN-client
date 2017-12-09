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
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.HeatmapTileProvider.Builder;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import util.JSONArrayUtils;
import util.Prices;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String SERVER_URI = "https://4wmuzhlr5b.execute-api.eu-west-2.amazonaws.com/prod/martinServer";

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    RequestQueue queue;
    SharedPreferences sharedPreferences;
    Marker mCurrLocationMarker;

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
            mLastLocation.setLatitude(point.latitude);
            mLastLocation.setLongitude(point.longitude);
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
            updateMap();
        }
    }

    public void updateMap(double latitude, double longitude) {
        mLastLocation.setLatitude(latitude);
        mLastLocation.setLongitude(longitude);
        updateMap();
    }

    private void updateMap() {
        mGoogleMap.clear();
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        requestHousePricesPaidData();
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
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
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
        int radius = Integer.parseInt(sharedPreferences.getString("radius", "50"));
        Toast.makeText(getActivity(), "Finding Price Paid Data...", Toast.LENGTH_LONG).show();
        String requestUrl = SERVER_URI + "?latitude=" + mLastLocation.getLatitude() + "&longitude=" + mLastLocation.getLongitude() + "&distance=" + radius;
        Log.d("Martin's Map", requestUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                addHeatMap(response);
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

    private void addHeatMap(JSONArray array) {
        try {
            if (array.length() == 0) {
                Toast.makeText(getActivity(), "No price paid data available", Toast.LENGTH_SHORT).show();
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
                addHeatMapMarker(average, max, min);
            }
        } catch (JSONException ex) {
            Log.e("Martin's Maps", ex.getMessage());
        }
    }

    private void addHeatMapMarker(int average, int max, int min) {
        LatLng latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mCurrLocationMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("HeatMap Figures")
                .snippet("Average House Price: " + average + "\n" + "Max House Price: " + max + "\n" + "Min House Price: " + min));
        mCurrLocationMarker.showInfoWindow();
    }

}