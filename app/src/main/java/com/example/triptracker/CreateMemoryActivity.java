package com.example.triptracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class CreateMemoryActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private LatLng point;
    ViewPager creatememoryslider;
    private ArrayList<Fragment> chosenviews= new ArrayList<>();
    private swipeadapter myviewsadapter;
    private Switch maptoggle;
    private final int  pick_image_code=10;
    private final int pick_video_code=11;
    private final int take_pic_code=12;
    private final int record_vid_code=13;
    private ImageButton choose_pic_gallery,choose_vid_gallery,taken_pic,record_vid;
    private Uri imageuri;
    private Uri viduri;

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

        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragview);
        Objects.requireNonNull(spmf).getMapAsync(this);
        //initialize the adapter for the chosen media files
        myviewsadapter= new swipeadapter(getSupportFragmentManager(),chosenviews);
        creatememoryslider=findViewById(R.id.creatememoryslider);
        maptoggle=findViewById(R.id.mediaswitch);
        choose_pic_gallery=findViewById(R.id.gallery_image);
        taken_pic=findViewById(R.id.take_pic);
        choose_vid_gallery=findViewById(R.id.upload_video);
        record_vid=findViewById(R.id.record_vid);
        
        creatememoryslider.setAdapter(myviewsadapter);
        maptoggle.setTextOn("Map");
        
        
        
        choose_pic_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadpic();
            }
        });
        taken_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takepic();
            }
        });
        choose_vid_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadvid();
            }
        });
        record_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordvid();
            }
        });

        //toggling on and off between the map and the media files
        maptoggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            FragmentManager fragmentManager = getSupportFragmentManager();

            android.support.v4.app.Fragment themapview = fragmentManager.findFragmentById(R.id.mapfragview);
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (maptoggle.isChecked()){
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
    private void uploadpic() {
        Intent upload= new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("image/*");
        startActivityForResult(upload,pick_image_code);
    }
//starts the activity of taking a pic
    private void takepic() {
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takepic,take_pic_code);
    }
//starts the activity of choosing a video from the gallery
    private void uploadvid() {
        Intent upload_vid= new Intent(Intent.ACTION_GET_CONTENT);
        upload_vid.setType("video/*");
        startActivityForResult(upload_vid,pick_video_code);
    }

    //starts the activity of recording a video
    private void recordvid() {
        Intent recordvid= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(recordvid,record_vid_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==pick_image_code && resultCode== Activity.RESULT_OK){
            imageuri=data.getData();
            if (imageuri==null){

                Toast.makeText(getApplicationContext(),"please choose an image",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the image",imageuri.toString());
                imagefragment thefrag= new imagefragment();
                thefrag.setArguments(args);
                chosenviews.add(thefrag);
                myviewsadapter=new swipeadapter(getSupportFragmentManager(),chosenviews);
                creatememoryslider.setAdapter(myviewsadapter);

            }


        }
        else if (requestCode==pick_video_code && resultCode==Activity.RESULT_OK){

            viduri=data.getData();
            if (viduri==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the video",viduri.toString());
                vidfragment thefrag= new vidfragment();
                thefrag.setArguments(args);
                chosenviews.add(thefrag);
                myviewsadapter=new swipeadapter(getSupportFragmentManager(),chosenviews);
                creatememoryslider.setAdapter(myviewsadapter);




            }
        }
        else if (requestCode==take_pic_code && resultCode==Activity.RESULT_OK){
            Bitmap themap= (Bitmap) data.getExtras().get("data");
            if(themap==null){
                Toast.makeText(getApplicationContext(),"Please take a picture",Toast.LENGTH_SHORT).show();
            }
            else{
                Bundle args=new Bundle();
                Bitmap mymap= (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream mystream= new ByteArrayOutputStream();
                mymap.compress(Bitmap.CompressFormat.JPEG,100,mystream);
                byte[] mybytes= mystream.toByteArray();
                args.putString("the cam", Base64.encodeToString(mybytes,Base64.DEFAULT));
                capimagefragment thefrag= new capimagefragment();
                thefrag.setArguments(args);
                chosenviews.add(thefrag);
                myviewsadapter=new swipeadapter(getSupportFragmentManager(),chosenviews);
                creatememoryslider.setAdapter(myviewsadapter);


            }

        }
        else if (requestCode==record_vid_code && resultCode== Activity.RESULT_OK){

            viduri=data.getData();
            if (viduri==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the video",viduri.toString());
                vidfragment thefrag= new vidfragment();
                thefrag.setArguments(args);
                chosenviews.add(thefrag);
                myviewsadapter=new swipeadapter(getSupportFragmentManager(),chosenviews);
                creatememoryslider.setAdapter(myviewsadapter);



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

//


}








//    public void onMapReady(GoogleMap googleMap) {
//
//
//    }