package com.example.triptracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CreateMemoryActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private LatLng point;
    ViewPager createMemorySlider;
    private ArrayList<Fragment> chosenViewsArrayList = new ArrayList<>();
    private SwipeAdapter chosenViewsAdapter;
    private Switch mapMediaToggle;
    private final int  PICK_IMAGE_CODE=10;
    private final int PICK_VIDEO_CODE=11;
    private final int TAKE_PIC_CODE=12;
    private final int RECORD_VIDEO_CODE=13;
    private ImageButton choosePicGallery, chooseVidGallery, takePic, recordVid;
    private Uri imageUri;
    private Uri recordedVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memory);

        //Get the screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Get the height and width of the phone display
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //Set the screen size of current activity to *8 and *7 to make it appear smaller
        getWindow().setLayout((int) (screenWidth*.8), (int) (screenHeight*.8));

        //Move the popup up
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.y = 0;
        getWindow().setAttributes(params);

        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragView);
        Objects.requireNonNull(spmf).getMapAsync(this);

        //initialize the adapter for the chosen media files
        chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
        createMemorySlider =findViewById(R.id.createMemorySlider);
        mapMediaToggle =findViewById(R.id.mediaSwitch);
        choosePicGallery =findViewById(R.id.galleryImage);
        takePic =findViewById(R.id.takePic);
        chooseVidGallery =findViewById(R.id.uploadVideo);
        recordVid =findViewById(R.id.recordVid);

        createMemorySlider.setAdapter(chosenViewsAdapter);
        mapMediaToggle.setTextOn("Map");
        CirclePageIndicator myindicator= findViewById(R.id.tabDots);
        myindicator.setFillColor(Color.rgb(20,145,218));
        myindicator.setRadius(12.0F);
        myindicator.setStrokeColor(Color.rgb(20,145,218));
        myindicator.setViewPager(createMemorySlider);

        choosePicGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPic();
            }
        });
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        chooseVidGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVid();
            }
        });
        recordVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVid();
            }
        });

        //toggling on and off between the map and the media files
        mapMediaToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.Fragment themapview = fragmentManager.findFragmentById(R.id.mapFragView);
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mapMediaToggle.isChecked()){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(themapview);
                    fragmentTransaction.commit();

                }
                else{
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(themapview);
                    fragmentTransaction.commit();
                }
            }
        });


    }

//starts the activity of choosing a pic from the gallery
    private void uploadPic() {
        Intent upload= new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("image/*");
        startActivityForResult(upload,PICK_IMAGE_CODE);
    }

//starts the activity of taking a pic
    private void takePic() {
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takepic,TAKE_PIC_CODE);
    }

//starts the activity of choosing a video from the gallery
    private void uploadVid() {
        Intent upload_vid= new Intent(Intent.ACTION_GET_CONTENT);
        upload_vid.setType("video/*");
        startActivityForResult(upload_vid,PICK_VIDEO_CODE);
    }

    //starts the activity of recording a video
    private void recordVid() {
        Intent recordvid= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(recordvid,RECORD_VIDEO_CODE);
    }

    //handels all the data from the called intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==PICK_IMAGE_CODE && resultCode== Activity.RESULT_OK){
            imageUri =data.getData();
            if (imageUri ==null){

                Toast.makeText(getApplicationContext(),"please choose an image",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the image", imageUri.toString());
                ImageFragment chosenImageFragment= new ImageFragment();
                chosenImageFragment.setArguments(args);
                chosenViewsArrayList.add(chosenImageFragment);
                chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }
        }

        else if (requestCode==PICK_VIDEO_CODE && resultCode==Activity.RESULT_OK) {
            recordedVideoUri = data.getData();
            if (recordedVideoUri == null) {
                Toast.makeText(getApplicationContext(), "please choose a video", Toast.LENGTH_LONG).show();
            } else {
                Bundle args = new Bundle();
                args.putString("the video", recordedVideoUri.toString());
                VidFragment chosenVideoFragment = new VidFragment();
                chosenVideoFragment.setArguments(args);
                chosenViewsArrayList.add(chosenVideoFragment);
                chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }
        }

        else if (requestCode==TAKE_PIC_CODE && resultCode==Activity.RESULT_OK){
            Bitmap takenImageBitMap= (Bitmap) data.getExtras().get("data");
            if(takenImageBitMap==null){
                Toast.makeText(getApplicationContext(),"Please take a picture",Toast.LENGTH_SHORT).show();
            }
            else{
                Bundle args=new Bundle();
                ByteArrayOutputStream takenImageOutputStream= new ByteArrayOutputStream();
                takenImageBitMap.compress(Bitmap.CompressFormat.JPEG,100,takenImageOutputStream);
                byte[] takenImageByteArray= takenImageOutputStream.toByteArray();
                args.putString("the cam", Base64.encodeToString(takenImageByteArray,Base64.DEFAULT));
                CapImageFragment capturedImageFragment= new CapImageFragment();
                capturedImageFragment.setArguments(args);
                chosenViewsArrayList.add(capturedImageFragment);
                chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }

        }

        else if (requestCode==RECORD_VIDEO_CODE && resultCode== Activity.RESULT_OK){
            recordedVideoUri =data.getData();
            if (recordedVideoUri ==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the video", recordedVideoUri.toString());
                VidFragment recordedVideoFragment= new VidFragment();
                recordedVideoFragment.setArguments(args);
                chosenViewsArrayList.add(recordedVideoFragment);
                chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }

        }

        else {
            Toast.makeText(getApplicationContext(),"please choose a video or an image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

        mMap.addMarker(new MarkerOptions()
                //placeholder latlng until intent is added
                .position(new LatLng(51.9225, 4.47917))
                .draggable(true));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                point = marker.getPosition();
                Log.d("New location", String.valueOf(point));
            }

        });
    }
}