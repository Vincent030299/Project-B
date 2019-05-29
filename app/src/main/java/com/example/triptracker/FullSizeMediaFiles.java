package com.example.triptracker;

import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

public class FullSizeMediaFiles extends FragmentActivity {
    private ArrayList<Uri> imagesUris;
    private ArrayList<Uri> videosUris;
    private ArrayList<Fragment> mediaFilesFragments = new ArrayList<>();
    private ViewPager allMediaFilesSlider;
    private SwipeAdapter fullSizeMediaFilesAdapter;
    private DisplayMetrics phoneDisplay;
    private CirclePageIndicator fullSizeDotsIndicator;
    private ImageButton closePopupBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biggermediafileslayout);
        //initializing all needed variables
        imagesUris = getIntent().getParcelableArrayListExtra("images");
        videosUris = getIntent().getParcelableArrayListExtra("videos");
        phoneDisplay = new DisplayMetrics();
        fullSizeDotsIndicator = findViewById(R.id.fullSizeDotsIndicator);
        closePopupBtn = findViewById(R.id.fullScreenCloseBtn);

        //getting display dimensions and adding all the video and images
        getWindowManager().getDefaultDisplay().getMetrics(phoneDisplay);
        addImages(imagesUris);
        addVideos(videosUris);
        allMediaFilesSlider = findViewById(R.id.fullSizeViewPager);
        fullSizeMediaFilesAdapter = new SwipeAdapter(getSupportFragmentManager(), mediaFilesFragments);
        allMediaFilesSlider.setAdapter(fullSizeMediaFilesAdapter);

        //adjusting the dot indicator
        fullSizeDotsIndicator.setFillColor(Color.rgb(20,145,218));
        fullSizeDotsIndicator.setRadius(15.0f);
        fullSizeDotsIndicator.setStrokeColor(Color.rgb(20,145,218));
        fullSizeDotsIndicator.setViewPager(allMediaFilesSlider);
        resizeLayout();

        //click listener to close the popup
        closePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void resizeLayout() {
        getWindow().setLayout((int)(phoneDisplay.widthPixels*0.7),(int)(phoneDisplay.heightPixels*0.8));
    }

    private void addVideos(ArrayList<Uri> videosUris) {
        for (int i = 0; i<videosUris.size(); i++){
            Bundle fragArgs = new Bundle();
            fragArgs.putString("the video", videosUris.get(i).toString());
            VidFragment fullSizeVid = new VidFragment();
            fullSizeVid.setArguments(fragArgs);
            mediaFilesFragments.add(fullSizeVid);
        }
    }

    private void addImages(ArrayList<Uri> imagesUris) {
        for (int i = 0; i<imagesUris.size(); i++){
            Bundle fragArgs = new Bundle();
            fragArgs.putString("the image", imagesUris.get(i).toString());
            ImageFragment fullSizeImage = new ImageFragment();
            fullSizeImage.setArguments(fragArgs);
            mediaFilesFragments.add(fullSizeImage);
        }

    }
}
