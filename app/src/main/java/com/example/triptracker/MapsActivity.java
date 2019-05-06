package com.example.triptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Button createMemoryButton;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private final int CREATE_MARKER = 2;
    private LocationManager locationManager;
    private LatLng userLocation;
    private EditText mSearchText;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = (EditText) findViewById(R.id.input_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Check if user has given permissions for FINE_LOCATION and COARSE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; }

        // get initial user location
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        userLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            userLocation = new LatLng(latitude, longitude);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        createMemoryButton = findViewById(R.id.createMemoryButton);
    }

    private void init(){
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    geoLocate();
                }

                return false;
            }
        });
    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 16);
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public void openMemoryActivity(LatLng point) {
        Intent intent = new Intent(this, CreateMemoryActivity.class);
        intent.putExtra("location", point);
        startActivityForResult(intent, CREATE_MARKER);
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestLocationPermission();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                openCreateMemoryActivity(point);
            }
        });
        createMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userLocation == null) {
                    Toast.makeText(getApplicationContext(), "No known location, try turning on GPS", Toast.LENGTH_SHORT).show();
                } else {
                    openMemoryActivity(userLocation);
                    Toast.makeText(getApplicationContext(), "Placed marker on current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor markers = databaseHelper.getData();
        while(markers.moveToNext()){
            Double lat = markers.getDouble(4);
            Double lng = markers.getDouble(5);
            String title = markers.getString(1);
            LatLng point = new LatLng(lat,lng);
            createMarker(point,title);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View infoWindow = getLayoutInflater().inflate(R.layout.info_window_layout, null);
                TextView markerTitle = (TextView) infoWindow.findViewById(R.id.markerTitle);
                TextView markerDesc = (TextView) infoWindow.findViewById(R.id.markerDesc);
                ImageView markerImage = (ImageView) infoWindow.findViewById(R.id.markerImage);

                final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                LatLng markerPos = marker.getPosition();

                Cursor dbMarker = databaseHelper.getItem(markerPos.latitude,markerPos.longitude);
                while(dbMarker.moveToNext()){
                    String dbMarkerTitle = dbMarker.getString(1);
                    String dbMarkerDesc = dbMarker.getString(2);
                    Integer dbMemoryId = dbMarker.getInt(0);
                    Cursor dbImage = databaseHelper.getImage(dbMemoryId);
                    while (dbImage.moveToNext()){
                        String dbImageUriString = dbImage.getString(1);
                        Uri dbImageUri = Uri.parse(dbImageUriString);
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setData(dbImageUri);
                        markerImage.setImageURI(intent.getData());
                    }

                    if(dbMarkerTitle.length() > 21) {
                        markerTitle.setText(dbMarkerTitle.substring(0, 21) + "..");
                    } else {
                        markerTitle.setText(dbMarkerTitle);
                    }
                    if(dbMarkerDesc.length() > 82) {
                        markerDesc.setText(dbMarkerDesc.substring(0, 82) + "..");
                    } else {
                        markerDesc.setText(dbMarkerDesc);
                    }
                }

                return infoWindow;
            }

        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                openMemory(marker);
            }
        });
    }

    public void openMemory(Marker marker) {

    }

    public void openCreateMemoryActivity(LatLng point) {
        Intent intent = new Intent(this, CreateMemoryActivity.class);
        intent.putExtra("location", point);
        startActivityForResult(intent, CREATE_MARKER);
    }
    public void createMarker(LatLng point, String title) {
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(title));
    }

    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(this, perms)) {
            mMap.setMyLocationEnabled(true);
            init();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_MARKER && resultCode == RESULT_OK) {
            LatLng point = data.getParcelableExtra("location");
            String title = data.getStringExtra("title");
            createMarker(point, title);
        }
    }
}