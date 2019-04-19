package com.example.triptracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.Objects;

public class ViewMemoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private ViewPager viewMemoryMediaSlider;
    private TextView viewMemoryTitle,viewMemoryDate,viewMemoryDescription;
    private ImageButton viewMemoryShareButton;
    private Fragment viewmemoryMapFragment;
    private CirclePageIndicator viewMemoryDotsIndicator;
    private Switch viewMemoryMediaSwitch;
    private FragmentManager viewMemoryFragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewMemoryDate=findViewById(R.id.viewMemoryDate);
        viewMemoryDescription=findViewById(R.id.viewMemoryDescription);
        viewMemoryTitle=findViewById(R.id.viewMemoryTitle);
        viewMemoryMediaSlider=findViewById(R.id.viewMemorySlider);
        viewMemoryShareButton=findViewById(R.id.viewMemoryShareBtn);
        viewMemoryDotsIndicator=findViewById(R.id.viewMemoryDotsIndicator);
        viewMemoryMediaSwitch=findViewById(R.id.viewMemoryMediaSwitch);

        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewMemoryMap);
        Objects.requireNonNull(spmf).getMapAsync(this);

        viewMemoryDotsIndicator.setFillColor(Color.rgb(20,145,218));
        viewMemoryDotsIndicator.setRadius(8.0F);
        viewMemoryDotsIndicator.setStrokeColor(Color.rgb(20,145,218));
        viewMemoryDotsIndicator.setViewPager(viewMemoryMediaSlider);

        viewMemoryFragmentManager=getSupportFragmentManager();
        viewmemoryMapFragment=viewMemoryFragmentManager.findFragmentById(R.id.viewMemoryMap);
        viewMemoryShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewMemoryMediaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (viewMemoryMediaSwitch.isChecked()){
                    viewMemoryMapVisibility(false);
                }
                else{
                    viewMemoryMapVisibility(true);
                }
            }
        });
    }
    private void viewMemoryMapVisibility(boolean visible) {
        if (visible){
            viewMemoryDotsIndicator.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.show(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(false);
        }
        else {
            viewMemoryDotsIndicator.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.hide(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
