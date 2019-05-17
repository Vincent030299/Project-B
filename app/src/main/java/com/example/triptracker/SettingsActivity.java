package com.example.triptracker;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private Integer markerColor;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openActivity(MapsActivity.class);
                    return true;
                case R.id.navigation_dashboard:
                    openActivity(DashboardActivity.class);
                    return true;
                case R.id.navigation_settings:
                    openActivity(SettingsActivity.class);
                    return true;
            }
            return false;
        }
    };
    
    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);
    }


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        final TextInputLayout markerName = findViewById(R.id.custom_marker_name);

        SeekBar hueBar = findViewById(R.id.hueBar);
        final ImageView hueValue = findViewById(R.id.hueValue);
        hueBar.setMax(360);

        hueBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onStopTrackingTouch(SeekBar hueBar) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar hueBar) {}

                    @Override
                    public void onProgressChanged(SeekBar huekBar, int progress,
                                                  boolean fromUser)
                    {
                        float hsv[] = new float[] {progress,100,100};
                        int color = Color.HSVToColor(hsv);
                        Log.d("test", Integer.toString(progress));
                        hueValue.setColorFilter(color);
                        markerColor = progress;
                    }
                }
        );

        ImageButton saveMarkerBtn = findViewById(R.id.saveMarkerBtn);
        saveMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper customMarkerDatabase=new DatabaseHelper(getApplicationContext());
                if(customMarkerDatabase.addCustomMarker(markerName.getEditText().getText().toString(), markerColor)) {
                    Toast.makeText(getApplicationContext(), "Marker succesfully saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Marker unsuccesfully saved", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
