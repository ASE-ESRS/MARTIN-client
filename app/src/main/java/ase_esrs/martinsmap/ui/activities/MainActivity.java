package ase_esrs.martinsmap.ui.activities;

import android.app.FragmentTransaction;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ase_esrs.martinsmap.R;
import ase_esrs.martinsmap.ui.fragments.MapFragment;

import static ase_esrs.martinsmap.ui.Permissions.INTERNET_PERMISSION;
import static ase_esrs.martinsmap.ui.Permissions.LOCATION_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapsFragment = new MapFragment();

        FragmentTransaction transaction = MainActivity.this.getFragmentManager().beginTransaction();
        transaction.add(R.id.linear_layout, (Fragment) mapsFragment);
        transaction.commit();
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
}
