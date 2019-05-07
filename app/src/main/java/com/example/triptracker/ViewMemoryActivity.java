package com.example.triptracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.BundleCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.CirclePageIndicator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMemoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private ViewPager viewMemoryMediaSlider;
    private TextView viewMemoryTitle,viewMemoryDate,viewMemoryDescription;
    private ImageButton viewMemoryShareButton,closeViewMemory;
    private Fragment viewmemoryMapFragment;
    private CirclePageIndicator viewMemoryDotsIndicator;
    private Switch viewMemoryMediaSwitch;
    private FragmentManager viewMemoryFragmentManager;
    private GoogleMap mMap;
    private SwipeAdapter viewMemorySwipeAdapter;
    private ArrayList<Fragment> memoryViewMediaFiles = new ArrayList<>();
    private String memoryTitle, memoryDescription,memoryDate;
    private ArrayList<String> memoryImages,memoryBitmaps,memoryVideos;
    private LinearLayout mediaFilesLayout;
    private LatLng markerLoc;
    private DatabaseHelper mDataBaseHelper;
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
        mediaFilesLayout = findViewById(R.id.viewMemoryMediaLayout);
        closeViewMemory = findViewById(R.id.closeViewMemory);
        mDataBaseHelper = new DatabaseHelper(getApplicationContext());

        memoryTitle = getIntent().getStringExtra("title");
        memoryDescription = getIntent().getStringExtra("description");
        memoryDate = getIntent().getStringExtra("date");
        markerLoc = new LatLng(getIntent().getExtras().getDouble("lat"), getIntent().getExtras().getDouble("lng"));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        closeViewMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                fragmentArgs.putString("the cam",memoryBitmaps.get(i) );
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
        viewMemoryDescription.setMovementMethod(new ScrollingMovementMethod());
        viewMemoryTitle.setMovementMethod(new ScrollingMovementMethod());

        viewMemoryFragmentManager=getSupportFragmentManager();
        viewmemoryMapFragment=viewMemoryFragmentManager.findFragmentById(R.id.viewMemoryMap);
        viewMemoryMapVisibility(false);

        viewMemoryShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu uploadBtnsMenu= new PopupMenu(ViewMemoryActivity.this, viewMemoryShareButton);
                uploadBtnsMenu.inflate(R.menu.choosesocialmediamenu);
                uploadBtnsMenu.show();
                uploadBtnsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.shareTwitter:
                                shareMemory("twitter");
                                return true;
                        }
                        return false;
                    }
                });
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

    private void shareMemory(String socialMedia) {
        switch (socialMedia){
            case "twitter":

                Intent twitter = new Intent();
                twitter.setPackage("com.twitter.android");
                twitter.setAction(Intent.ACTION_SEND);
                if(!memoryImages.isEmpty()) {
                    twitter.putExtra(Intent.EXTRA_STREAM, Uri.parse(memoryImages.get(0)));
                    twitter.setType("image/jpeg");
                }
                twitter.putExtra(Intent.EXTRA_TEXT, memoryTitle + " on " + memoryDate);
                twitter.setType("text/plain");

                startActivity(twitter);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Intent intent = getIntent();
        markerLoc = intent.getParcelableExtra("location");
        if (markerLoc==null){
            markerLoc = new LatLng(getIntent().getDoubleExtra("lat", 0.0), getIntent().getDoubleExtra("lng", 0.0));
        }

        mMap.addMarker(new MarkerOptions()
                .position(markerLoc)
                .draggable(true));

        //Create camera zoom to show marker close
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(markerLoc).
                zoom(15).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Please use the navigation bar to navigate", Toast.LENGTH_LONG).show();
    }

    //open a given activity
    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);

    }
}
