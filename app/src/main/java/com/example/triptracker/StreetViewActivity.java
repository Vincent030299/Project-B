package com.example.triptracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewSource;

import java.util.Objects;

public class StreetViewActivity extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {
    private SupportStreetViewPanoramaFragment streetViewFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streetviewlayout);

        streetViewFrag = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetViewFragment);
        streetViewFrag.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama streetViewPanorama) {
        final LatLng viewLocation = getIntent().getParcelableExtra("memory location");
        streetViewPanorama.setPosition(viewLocation,StreetViewSource.OUTDOOR);
        streetViewPanorama.setUserNavigationEnabled(true);

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if(streetViewPanoramaLocation==null){
                    finish();
                    Toast.makeText(getApplicationContext(), getString(R.string.feature_not_available), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
