package com.example.triptracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    Switch nightMode;
    View view;
    TextView text;
    private ListView markerList;
    private ArrayList<String> markerNames;
    private ArrayList<Integer> markerColors;
    private ArrayList<Integer> markerIds;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    protected MarkerListViewAdapter markerListViewAdapter;


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
            }
            return false;
        }
    };
    private void toggleTheme(boolean DarkTheme) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, DarkTheme);
        editor.apply();

        openActivity(SettingsActivity.class);
    }

    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppThemeNight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        view = this.getWindow().getDecorView();
        text = findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        markerList = findViewById(R.id.markersList);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor markers = databaseHelper.getCustomMarkers();
        markerNames = new ArrayList<>();
        markerColors = new ArrayList<>();
        markerIds = new ArrayList<>();


        while(markers.moveToNext()){
            Integer markerId = markers.getInt(0);
            String markerName = markers.getString(1);
            Integer markerColor = markers.getInt(2);
            markerNames.add(markerName);
            markerColors.add(markerColor);
            markerIds.add(markerId);
        }
        markerListViewAdapter = new MarkerListViewAdapter(getApplicationContext(),markerNames,markerColors,markerIds,getSupportFragmentManager());
        markerList.setAdapter(markerListViewAdapter);
        markerListViewAdapter.notifyDataSetChanged();

        ViewGroup.LayoutParams params = markerList.getLayoutParams();

        nightMode = findViewById(R.id.nightModeSwitch);
        nightMode.setChecked(useDarkTheme);
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleTheme(isChecked);
            }
        });

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
                DatabaseHelper customMarkerDatabase = new DatabaseHelper(getApplicationContext());
                if(markerName.getEditText().getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please give the marker a name", Toast.LENGTH_SHORT).show();
                } else {
                    if (customMarkerDatabase.addCustomMarker(markerName.getEditText().getText().toString(), markerColor)) {
                        Toast.makeText(getApplicationContext(), "Marker succesfully saved", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(getApplicationContext(), "Marker unsuccesfully saved", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

  }


