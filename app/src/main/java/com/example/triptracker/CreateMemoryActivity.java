package com.example.triptracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
    private ImageButton choosePicGallery, chooseVidGallery, takePic, recordVid,saveMemoryButton;
    private Button closePopup;
    private TextInputLayout memoryTitle,memoryDescription;
    private DatePicker memoryDate;
    private int imageAmount=0, videoAmount=0, bitmapsAmount=0;
    private Uri[] imageUri = new Uri[imageAmount];
    private Uri[] recordedVideoUri= new Uri[videoAmount];
    private Bitmap[] imageBitmaps= new Bitmap[bitmapsAmount];
    private int currentDay,currentMonth,currentYear;
    private Fragment mapFragment;
    private LinearLayout pageIndicatorView;

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
        //initialize the used components in the layout file
        createMemorySlider =findViewById(R.id.createMemorySlider);
        mapMediaToggle =findViewById(R.id.mediaSwitch);
        choosePicGallery =findViewById(R.id.galleryImage);
        takePic =findViewById(R.id.takePic);
        chooseVidGallery =findViewById(R.id.uploadVideo);
        recordVid =findViewById(R.id.recordVid);
        saveMemoryButton=findViewById(R.id.saveMemoryBtn);
        memoryDescription=findViewById(R.id.memoryDescription);
        memoryTitle=findViewById(R.id.memoryTitle);
        memoryDate=findViewById(R.id.memoryDate);
        closePopup =findViewById(R.id.closePopup);
        pageIndicatorView =findViewById(R.id.pageIndicator);
        mapFragment=getSupportFragmentManager().findFragmentById(R.id.mapFragView);
        LinearLayout mapLayout= findViewById(R.id.mapLayout);
        LinearLayout mediaFilesLayout= findViewById(R.id.mediaFilesLayout);

        //setting the initial visibility state of pageIndicatorView
        pageIndicatorView.setVisibility(View.INVISIBLE);

        //adjusting the layout parameters according to the user's screen
        ConstraintLayout.LayoutParams mapsLayoutParams=new ConstraintLayout.LayoutParams((int)(screenWidth*0.8),(int)(screenHeight*.8*.35));
        ConstraintLayout.LayoutParams mediafilesSliderParams= new ConstraintLayout.LayoutParams((int)(screenWidth*0.8),(int)(screenHeight*.8*.35));
        mediaFilesLayout.setLayoutParams(mediafilesSliderParams);
        mediaFilesLayout.requestLayout();
        mapLayout.setLayoutParams(mapsLayoutParams);
        mapLayout.requestLayout();
        currentDay=memoryDate.getDayOfMonth();
        currentMonth=memoryDate.getMonth();
        currentYear=memoryDate.getYear();

        //setting the adapter for the slider view
        createMemorySlider.setAdapter(chosenViewsAdapter);
        mapMediaToggle.setTextOn("Map");
        final CirclePageIndicator pageIndicator= findViewById(R.id.tabDots);
        pageIndicator.setFillColor(Color.rgb(20,145,218));
        pageIndicator.setRadius(8.0F);
        pageIndicator.setStrokeColor(Color.rgb(20,145,218));
        pageIndicator.setViewPager(createMemorySlider);

        saveMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemory();
            }
        });
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
                    pageIndicatorView.setVisibility(View.VISIBLE);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.hide(themapview);
                    fragmentTransaction.commit();

                }
                else{
                    pageIndicatorView.setVisibility(View.INVISIBLE);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.show(themapview);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    private void saveMemory() {
        int chosenDay= memoryDate.getDayOfMonth();
        int chosenMonth= memoryDate.getMonth();
        int chosenYear=memoryDate.getYear();
        if (memoryTitle.getEditText().getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please enter a title",Toast.LENGTH_SHORT).show();
        }
        else if (memoryDescription.getEditText().getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please enter a description",Toast.LENGTH_SHORT).show();
        }
        else if ((chosenDay>currentDay & chosenMonth>currentMonth & chosenYear>currentYear)
                |(chosenDay>currentDay & chosenMonth==currentMonth & currentYear==chosenYear)
                |(chosenDay==currentDay & chosenMonth>currentMonth & chosenYear==currentYear)
                |(chosenDay==currentDay & currentMonth==chosenMonth & chosenYear>currentYear)
                |(chosenDay==currentDay & chosenMonth>currentMonth & chosenYear>currentYear)){
            Toast.makeText(getApplicationContext(),"Please choose another date",Toast.LENGTH_SHORT).show();
        }
        else if(chosenViewsArrayList.size()==0){
            Toast.makeText(getApplicationContext(),"Please choose an image or a video",Toast.LENGTH_SHORT).show();
        }
        else {
            String currentMemoryTitle=memoryTitle.getEditText().getText().toString();
            String currentMemoryDescription= memoryDescription.getEditText().getText().toString();
            String currentMemoryDate= String.valueOf(chosenDay)+'-'+String.valueOf(chosenMonth)+'-'+String.valueOf(chosenYear);
            DatabaseHelper memoryDatabase=new DatabaseHelper(getApplicationContext());

            if(memoryDatabase.addData(currentMemoryTitle, currentMemoryDate, currentMemoryDescription, imageUri, recordedVideoUri, imageBitmaps,point.latitude, point.longitude)){
                Toast.makeText(getApplicationContext(), "Memory saved successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location", point);
                resultIntent.putExtra("title", currentMemoryTitle);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
            else{
                Toast.makeText(getApplicationContext(), "Failed to save memory", Toast.LENGTH_SHORT).show();
            }
        }
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
            if (data.getData() ==null){

                Toast.makeText(getApplicationContext(),"please choose an image",Toast.LENGTH_LONG).show();
            }
            else{
                imageAmount=imageAmount+1;
                imageUri=new Uri[imageAmount];
                imageUri[imageUri.length-1]=data.getData();
                Bundle args=new Bundle();
                args.putString("the image", imageUri[imageUri.length-1].toString());
                ImageFragment chosenImageFragment= new ImageFragment();
                chosenImageFragment.setArguments(args);
                chosenViewsArrayList.add(chosenImageFragment);
                chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }
        }

        else if (requestCode==PICK_VIDEO_CODE && resultCode==Activity.RESULT_OK) {

            if (recordedVideoUri == null) {
                Toast.makeText(getApplicationContext(), "please choose a video", Toast.LENGTH_LONG).show();
            } else {
                videoAmount=videoAmount+1;
                recordedVideoUri=new Uri[videoAmount];
                recordedVideoUri[recordedVideoUri.length-1]=data.getData();
                Bundle args = new Bundle();
                args.putString("the video", recordedVideoUri[recordedVideoUri.length-1].toString());
                VidFragment chosenVideoFragment = new VidFragment();
                chosenVideoFragment.setArguments(args);
                chosenViewsArrayList.add(chosenVideoFragment);
                chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
            }
        }

        else if (requestCode==TAKE_PIC_CODE && resultCode==Activity.RESULT_OK){
            if(data.getExtras()==null){
                Toast.makeText(getApplicationContext(),"Please take a picture",Toast.LENGTH_SHORT).show();
            }
            else{
                bitmapsAmount=bitmapsAmount+1;
                imageBitmaps=new Bitmap[bitmapsAmount];
                imageBitmaps[imageBitmaps.length-1]=(Bitmap) data.getExtras().get("data");
                Bundle args=new Bundle();
                ByteArrayOutputStream takenImageOutputStream= new ByteArrayOutputStream();
                imageBitmaps[imageBitmaps.length-1].compress(Bitmap.CompressFormat.JPEG,100,takenImageOutputStream);
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
            if (data.getData() ==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                videoAmount=videoAmount+1;
                recordedVideoUri=new Uri[videoAmount];
                recordedVideoUri[recordedVideoUri.length-1]=data.getData();
                Bundle args=new Bundle();
                args.putString("the video", recordedVideoUri[recordedVideoUri.length-1].toString());
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
        Intent intent = getIntent();
        point = intent.getParcelableExtra("location");
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true));

        //Create camera zoom to show marker close
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(point).
                zoom(15).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        closePopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                Toast.makeText(getApplicationContext(), "Updated memory location", Toast.LENGTH_SHORT).show();
            }

        });
    }
}