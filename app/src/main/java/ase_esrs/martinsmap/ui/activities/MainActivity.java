package ase_esrs.martinsmap.ui.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ase_esrs.martinsmap.R;
import ase_esrs.martinsmap.ui.fragments.MapFragment;
import util.PersistentStorageManager;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapsFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Martin's Map");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundColor(Color.rgb(103,58,183));
        setSupportActionBar(toolbar);

        mapsFragment = new MapFragment();

        FragmentTransaction transaction = MainActivity.this.getFragmentManager().beginTransaction();
        transaction.add(R.id.linear_layout, (Fragment) mapsFragment);
        transaction.commit();
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
