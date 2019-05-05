package com.example.triptracker;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.viewpagerindicator.CirclePageIndicator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMemoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private ViewPager viewMemoryMediaSlider;
    private TextView viewMemoryTitle,viewMemoryDate,viewMemoryDescription;
    private ImageButton viewMemoryShareButton;
    private Fragment viewmemoryMapFragment;
    private CirclePageIndicator viewMemoryDotsIndicator;
    private Switch viewMemoryMediaSwitch;
    private FragmentManager viewMemoryFragmentManager;
    private GoogleMap mMap;
    private SwipeAdapter viewMemorySwipeAdapter;
    private ArrayList<Fragment> memoryViewMediaFiles = new ArrayList<>();
    private String memoryTitle, memoryDescription,memoryDate;
    private ArrayList<String> memoryImages,memoryBitmaps,memoryVideos;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memoryviewlayout);
        viewMemoryDate=findViewById(R.id.viewMemoryDate);
        viewMemoryDescription=findViewById(R.id.viewMemoryDescription);
        viewMemoryTitle=findViewById(R.id.viewMemoryTitle);
        viewMemoryMediaSlider=findViewById(R.id.viewMemorySlider);
        viewMemoryShareButton=findViewById(R.id.viewMemoryShareBtn);
        viewMemoryDotsIndicator=findViewById(R.id.viewMemoryDotsIndicator);
        viewMemoryMediaSwitch=findViewById(R.id.viewMemoryMediaSwitch);
        memoryTitle = getIntent().getStringExtra("title");
        memoryDescription = getIntent().getStringExtra("description");
        memoryDate = getIntent().getStringExtra("date");

        viewMemoryDate.setText(memoryDate);
        viewMemoryTitle.setText(memoryTitle);
        viewMemoryDescription.setText(memoryDescription);
        memoryImages = getIntent().getStringArrayListExtra("images");
        memoryBitmaps = getIntent().getStringArrayListExtra("bitmaps");
        memoryVideos = getIntent().getStringArrayListExtra("videos");
//        Toast.makeText(getApplicationContext(), String.valueOf(memoryVideos.size()), Toast.LENGTH_SHORT).show();

        for(int i = 0; i<memoryImages.size(); i++){
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("the image", memoryImages.get(i));
            ImageFragment singleImageFragment = new ImageFragment();
            singleImageFragment.setArguments(fragmentArgs);
            memoryViewMediaFiles.add(singleImageFragment);
        }
        for (int i = 0; i<memoryVideos.size();i++){
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("the video", memoryVideos.get(i));
            VidFragment singleVideoFragment = new VidFragment();
            singleVideoFragment.setArguments(fragmentArgs);
            memoryViewMediaFiles.add(singleVideoFragment);
        }

        if (!memoryBitmaps.isEmpty()){
            for(int i = 0; i<memoryBitmaps.size(); i++){
                Bundle fragmentArgs = new Bundle();
                Log.d("the bitmap", memoryBitmaps.get(i));
                byte[] bitmapInBytes = memoryBitmaps.get(i).getBytes();
                fragmentArgs.putString("the cam",Base64.encodeToString(bitmapInBytes, Base64.DEFAULT) );
                CapImageFragment singleImageBitmap = new CapImageFragment();
                singleImageBitmap.setArguments(fragmentArgs);
                memoryViewMediaFiles.add(singleImageBitmap);
            }
        }

        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewMemoryMap);
        Objects.requireNonNull(spmf).getMapAsync(this);

        viewMemorySwipeAdapter = new SwipeAdapter(getSupportFragmentManager(), memoryViewMediaFiles);
        viewMemoryMediaSlider.setAdapter(viewMemorySwipeAdapter);
        viewMemoryDotsIndicator.setFillColor(Color.rgb(20,145,218));
        viewMemoryDotsIndicator.setRadius(8.0F);
        viewMemoryDotsIndicator.setStrokeColor(Color.rgb(20,145,218));
        viewMemoryDotsIndicator.setVisibility(View.INVISIBLE);
        viewMemoryMediaSlider.setVisibility(View.INVISIBLE);
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
            viewMemoryMediaSlider.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.show(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(false);
        }
        else {
            viewMemoryDotsIndicator.setVisibility(View.VISIBLE);
            viewMemoryMediaSlider.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.hide(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
