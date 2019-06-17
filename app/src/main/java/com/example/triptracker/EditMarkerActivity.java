package com.example.triptracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EditMarkerActivity extends FragmentActivity {

    private Integer markerId;
    private Integer markerColor;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

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
            }
            return false;
        }
    };

    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppThemeNight);
        }

        setContentView(R.layout.activity_edit_marker);

        Intent intent = getIntent();
        markerId = intent.getIntExtra("markerId", 0);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final TextInputLayout markerName = findViewById(R.id.custom_marker_name);

        SeekBar hueBar = findViewById(R.id.hueBar);
        final ImageView hueValue = findViewById(R.id.hueValue);
        hueBar.setMax(360);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor markers = databaseHelper.getMarker(markerId);

        while(markers.moveToNext()){
            markerName.getEditText().setText(markers.getString(1));
            Integer color = markers.getInt(2);
            markerColor = color;
            hueBar.setProgress(color);
            float hsv[] = new float[] {color,100,100};
            int hsvToColor = Color.HSVToColor(hsv);
            hueValue.setColorFilter(hsvToColor);

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
                            hueValue.setColorFilter(color);
                            markerColor = progress;
                        }
                    }
            );
        }

        ImageButton saveMarkerBtn = findViewById(R.id.saveMarkerBtn);
        saveMarkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper customMarkerDatabase = new DatabaseHelper(getApplicationContext());
                if(markerName.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.give_marker_name), Toast.LENGTH_SHORT).show();
                } else {
                    if (customMarkerDatabase.updateMarker(markerName.getEditText().getText().toString(),markerId ,markerColor)) {
                        Intent openSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                        openSettings.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        openSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(openSettings);
                        Toast.makeText(getApplicationContext(), getString(R.string.marker_changed), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.marker_not_changed), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

}
