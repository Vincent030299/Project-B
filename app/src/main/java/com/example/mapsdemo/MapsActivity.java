package com.example.mapsdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {

                openCardActivity(marker);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                openMemoryActivity(point);
            }
        });

    }

    public void openCardActivity(Marker marker) {
        String markerTitle = marker.getTitle();
        String markerDesc = marker.getSnippet();
        LatLng markerPos = marker.getPosition();
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("markerTitle", markerTitle);
        intent.putExtra("markerDesc", markerDesc);
        intent.putExtra("markerPos", markerPos);
        startActivity(intent);
    }

    public void openMemoryActivity(LatLng point) {

        Intent intent = new Intent(this, MemoryActivity.class);
        intent.putExtra("location", point);
        startActivityForResult(intent, 1);

    }

    public void createMarker(LatLng point, String title, String desc) {
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(desc));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                LatLng point = data.getParcelableExtra("location");
                String title = data.getStringExtra("title");
                String desc = data.getStringExtra("description");
                createMarker(point, title, desc);
            }
        }
    }

}
