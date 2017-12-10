package ase_esrs.martinsmap.ui.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import ase_esrs.martinsmap.R;
import ase_esrs.martinsmap.ui.fragments.MapFragment;
import util.DefaultsManager;
import util.PostcodeUtils;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    // Used as the shared context for the preferences.
    public static Context applicationContext;

    private MapFragment mapsFragment;
    private Toolbar toolbar;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = getApplicationContext();
        setContentView(R.layout.activity_main);

        // Set up default values in persistent storage.
        DefaultsManager.getInstance(getApplicationContext()).setDefaults();

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.rgb(103,58,183));
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        EditText postcodeSearchField = (EditText) findViewById(R.id.postcode_field);
        postcodeSearchField.setOnEditorActionListener((view, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                String postcode = view.getText().toString();
                if(PostcodeUtils.isValidPostcode(postcode)) {
                    postcode = postcode.replaceAll("\\s", "");
                    String requestUrl = "https://api.postcodes.io/postcodes/"+postcode;

                    JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("status") == 200) {
                                    JSONObject result = response.getJSONObject("result");
                                    double latitude = result.getDouble("latitude");
                                    double longitude = result.getDouble("longitude");
                                    mapFragment.updateMap(latitude, longitude);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Postcode could not be searched at this time",  Toast.LENGTH_SHORT);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Postcode could not be searched at this time",  Toast.LENGTH_SHORT);
                            }
                        }
                    }, (error) -> {
                        Toast.makeText(getApplicationContext(), "Postcode could not be searched at this time",  Toast.LENGTH_SHORT);
                    });
                    queue.add(jsonArrayRequest);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                mapsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            case INTERNET_PERMISSION:
                mapsFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            presentSettingsActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void presentSettingsActivity() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
}
