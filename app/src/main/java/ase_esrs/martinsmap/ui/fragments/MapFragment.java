package ase_esrs.martinsmap.ui.fragments;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
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

import util.PersistentStorageManager;
import util.Prices;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MapFragment extends com.google.android.gms.maps.MapFragment
        implements OnMapReadyCallback,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                LocationListener {

    private final static String SERVER_URI = "https://4wmuzhlr5b.execute-api.eu-west-2.amazonaws.com/prod/martinServer";

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    RequestQueue queue;
    boolean foundLocation = false;

    @Override
    public void onResume() {
        super.onResume();
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
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
            } else {
                //Request Permissions
                checkPermission(LOCATION_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        else {
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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Martin's Maps", "Location Updated");
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(!foundLocation) {
            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            foundLocation = true;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        updateServer();
    }

    private void checkPermission(final int permissionConstant, final String manifestPermissionConstant) {
        if(ContextCompat.checkSelfPermission(getActivity(), manifestPermissionConstant)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    manifestPermissionConstant)) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Permission Needed")
                        .setMessage("This app needs the "+manifestPermissionConstant+" permission, please accept to use related functionality.")
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

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case INTERNET_PERMISSION:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateServer();
                }
                break;
        }
    }

    private void updateServer() {
        int radius = PersistentStorageManager.sharedInstance.getSearchRadiusInMeters();

        Toast.makeText(getActivity(), "Finding Price Paid Data...", Toast.LENGTH_LONG).show();
        String requestUrl = SERVER_URI+"?latitude="+mLastLocation.getLatitude()+"&longitude="+mLastLocation.getLongitude()+"&distance="+radius;
        Log.d("Martin's Map", requestUrl);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                addHeatMap(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Martin's Maps", "A network error occurred: "+error.toString());
                Toast.makeText(getActivity(), "Network error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequest);
    }

    private void addHeatMap(JSONArray array) {
        try {
            if(array.length() == 0) {
                Toast.makeText(getActivity(), "No price paid data available", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<WeightedLatLng> locations = new ArrayList<>();
                int max = array.getJSONObject(0).getInt("price");
                int min = array.getJSONObject(0).getInt("price");
                for (int i = 1; i < array.length(); i++) { //Retrieves max and min price values
                    int currprice = array.getJSONObject(i).getInt("price");
                    if (currprice > max) {
                        max = currprice;
                    } else if (currprice < min) min = currprice;
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    LatLng loc = new LatLng(obj.getDouble("latitude"), obj.getDouble("longitude"));
                    double weight = Prices.priceIntensity(obj.getInt("price")); //TODO: add if statement here whether to pass max and min to Prices.priceIntensity depending on scale slider setting
                    locations.add(new WeightedLatLng(loc, weight));
                }
                HeatmapTileProvider tileProvider = new Builder().weightedData(locations).build();
                mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
            }
        } catch(JSONException ex) {
            Log.e("Martin's Maps", ex.getMessage());
        }
    }

}