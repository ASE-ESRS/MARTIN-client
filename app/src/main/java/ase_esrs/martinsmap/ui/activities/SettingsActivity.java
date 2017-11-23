package ase_esrs.martinsmap.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

import ase_esrs.martinsmap.R;

public class SettingsActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView searchRadiusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        // Add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set the value of the seek bar (when it is created).
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(PersistentStorageManager.sharedInstance.getSliderValue());

        // Set the value of the search radius label.
        searchRadiusTextView = (TextView) findViewById(R.id.searchRadiusTextView);
        updateSearchRadiusTextView();

        setupSeekBarListener();
    }

    protected void updateSearchRadiusTextView() {
        int sliderValue = PersistentStorageManager.sharedInstance.getSliderValue();
        switch (sliderValue) {
            case 0:
                searchRadiusTextView.setText("50m");
            case 1:
                searchRadiusTextView.setText("5km");
            case 2:
                searchRadiusTextView.setText("10km");
            case 3:
                searchRadiusTextView.setText("15km");
            default:
                searchRadiusTextView.setText("20km");
        }
    }

    protected void setupSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update Search Radius button pressed.
                PersistentStorageManager.sharedInstance.saveSliderValue(seekBar.getProgress());
                updateSearchRadiusTextView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}
