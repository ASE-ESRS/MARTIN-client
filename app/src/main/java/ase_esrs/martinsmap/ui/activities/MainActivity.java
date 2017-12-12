package ase_esrs.martinsmap.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ase_esrs.martinsmap.R;
import ase_esrs.martinsmap.ui.fragments.MapFragment;
import ase_esrs.martinsmap.util.DefaultsManager;
import ase_esrs.martinsmap.util.PostcodeUtils;
import ase_esrs.martinsmap.util.Prices;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapsFragment;
    private Toolbar toolbar;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.rgb(103,58,183));
        setSupportActionBar(toolbar);

        queue = Volley.newRequestQueue(this);

        // Set up default values in persistent storage.
        DefaultsManager.getInstance(getApplicationContext()).setDefaults();

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

    public void setHeatmapKey(int max, int avg, int min) {
        ((TextView) findViewById(R.id.heatmap_key_red)).setText(Prices.priceToString(max));
        ((TextView) findViewById(R.id.heatmap_key_yellow)).setText(Prices.priceToString(avg));
        ((TextView) findViewById(R.id.heatmap_key_green)).setText(Prices.priceToString(min));
        findViewById(R.id.heatmap_key).setVisibility(View.VISIBLE);
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

    /**
     * Presents the in-app Preferences (called when the Settings button (top right) is pressed).
     * @author Loic Verrall
     */
    protected void presentSettingsActivity() {
        MainActivity.this.startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
}
