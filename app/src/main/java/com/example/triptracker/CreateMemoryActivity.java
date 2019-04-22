package com.example.triptracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;

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
    private ImageButton closePopup,saveMemoryButton;
    private TextInputLayout memoryTitle,memoryDescription;
    private DatePicker memoryDate;
    private int imageAmount=0, videoAmount=0, bitmapsAmount=0;
    private Uri[] imageUri = new Uri[imageAmount];
    private Uri[] recordedVideoUri= new Uri[videoAmount];
    private Bitmap[] imageBitmaps= new Bitmap[bitmapsAmount];
    private int currentDay,currentMonth,currentYear;
    private Fragment mapFragment;
    private LinearLayout pageIndicatorView,uploadMediaFilesMenu,mapLayout,mediaFilesLayout;
    private FragmentManager createMemoryFragmentManager;
    private android.support.v4.app.Fragment createMemoryMapView;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memory);

        //Get the screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Get the height and width of the phone display
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragView);
        Objects.requireNonNull(spmf).getMapAsync(this);

        //initialize the adapter for the chosen media files
        chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
        //initialize the used components in the layout file
        createMemorySlider =findViewById(R.id.createMemorySlider);
        mapMediaToggle =findViewById(R.id.mediaSwitch);
        uploadMediaFilesMenu=findViewById(R.id.uploadBtns);
        saveMemoryButton=findViewById(R.id.saveMemoryBtn);
        memoryDescription=findViewById(R.id.memoryDescription);
        memoryTitle=findViewById(R.id.memoryTitle);
        memoryDate=findViewById(R.id.memoryDate);
        closePopup =findViewById(R.id.closeCreateMemory);
        pageIndicatorView =findViewById(R.id.pageIndicator);
        mapFragment=getSupportFragmentManager().findFragmentById(R.id.mapFragView);
        mapLayout= findViewById(R.id.mapLayout);
        mediaFilesLayout= findViewById(R.id.mediaFilesLayout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);


        //setting the initial visibility state of pageIndicatorView
        pageIndicatorView.setVisibility(View.INVISIBLE);

        //adjusting the layout parameters according to the user's screen
//        ConstraintLayout.LayoutParams mapsLayoutParams=new ConstraintLayout.LayoutParams((screenWidth),(int)(screenHeight*0.8*0.35));
//        ConstraintLayout.LayoutParams mediafilesSliderParams= new ConstraintLayout.LayoutParams((screenWidth),(int)(screenHeight*0.8*0.35));
//        mediaFilesLayout.setLayoutParams(mediafilesSliderParams);
//        mediaFilesLayout.requestLayout();
//        mapLayout.setLayoutParams(mapsLayoutParams);
//        mapLayout.requestLayout();
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

        //all of the click listeners in the layout
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMemory();
            }
        });
        uploadMediaFilesMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu uploadBtnsMenu= new PopupMenu(CreateMemoryActivity.this, uploadMediaFilesMenu);
                uploadBtnsMenu.inflate(R.menu.choosemediafilemenu);
                uploadBtnsMenu.show();
                uploadBtnsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.uploadImageItem:
                                uploadPic();
                                return true;
                            case R.id.takePicItem:
                                takePic();
                                return true;
                            case R.id.uploadVideoItem:
                                uploadVid();
                                return true;
                            case R.id.recordVideoItem:
                                recordVid();
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

        //setting up the map fragment
        createMemoryFragmentManager = getSupportFragmentManager();
        createMemoryMapView = createMemoryFragmentManager.findFragmentById(R.id.mapFragView);

        //toggling on and off between the map and the media files
        mapMediaToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mapMediaToggle.isChecked()){

                    mapViewVisibility(false);

                }
                else{
                    mapViewVisibility(true);
                }
            }
        });
    }

    private void mapViewVisibility(boolean visible) {
        if (visible){
            pageIndicatorView.setVisibility(View.INVISIBLE);
            mediaFilesLayout.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = createMemoryFragmentManager.beginTransaction();
            fragmentTransaction.show(createMemoryMapView);
            fragmentTransaction.commit();
            mapMediaToggle.setChecked(false);
        }
        else {
            pageIndicatorView.setVisibility(View.VISIBLE);
            mediaFilesLayout.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = createMemoryFragmentManager.beginTransaction();
            fragmentTransaction.hide(createMemoryMapView);
            fragmentTransaction.commit();
            mapMediaToggle.setChecked(true);
        }
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
        else if ((chosenDay>currentDay & chosenMonth==currentMonth & currentYear==chosenYear)
                |(chosenDay==currentDay & chosenMonth>currentMonth & chosenYear==currentYear)
                |(chosenDay==currentDay & currentMonth==chosenMonth & chosenYear>currentYear)
                |(chosenDay==currentDay & chosenMonth>currentMonth & chosenYear>currentYear)
                |(chosenDay>currentDay & chosenMonth>currentMonth & chosenYear==currentYear)
                |(chosenDay>currentDay & chosenMonth<=currentMonth & chosenYear>currentYear)
                |(chosenDay>currentDay & chosenMonth>currentMonth & chosenYear>currentYear))
                {
            Toast.makeText(getApplicationContext(),"Please choose another date",Toast.LENGTH_SHORT).show();
        }
        else if(chosenViewsArrayList.size()==0){
            Toast.makeText(getApplicationContext(),"Please choose an image or a video",Toast.LENGTH_SHORT).show();
        }
        else {
            String currentMemoryTitle=memoryTitle.getEditText().getText().toString();
            String currentMemoryDescription= memoryDescription.getEditText().getText().toString();
            String currentMemoryDate= String.valueOf(chosenDay)+'-'+String.valueOf(chosenMonth)+'-'+String.valueOf(chosenYear);
            String chosenImages= imageUri.toString();
            String takenImages= imageBitmaps.toString();
            String chosenvideos=recordedVideoUri.toString();
            DatabaseHelper memoryDatabase=new DatabaseHelper(getApplicationContext());

            if(memoryDatabase.addData(currentMemoryTitle, currentMemoryDate, currentMemoryDescription, chosenImages, takenImages, chosenvideos, point.latitude, point.longitude)){
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
        upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        upload.setType("image/*");
        mapViewVisibility(false);
        startActivityForResult(upload,PICK_IMAGE_CODE);
    }

//starts the activity of taking a pic
    private void takePic() {
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mapViewVisibility(false);
        startActivityForResult(takepic,TAKE_PIC_CODE);
    }

//starts the activity of choosing a video from the gallery
    private void uploadVid() {
        Intent upload_vid= new Intent(Intent.ACTION_GET_CONTENT);
        upload_vid.setType("video/*");
        upload_vid.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        mapViewVisibility(false);
        startActivityForResult(upload_vid,PICK_VIDEO_CODE);
    }

    //starts the activity of recording a video
    private void recordVid() {
        Intent recordvid= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mapViewVisibility(false);
        startActivityForResult(recordvid,RECORD_VIDEO_CODE);
    }
    //open a given activity
    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);

    }

    //handels all the data from the called intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==PICK_IMAGE_CODE && resultCode== Activity.RESULT_OK){
            if(data.getClipData()!=null){
                for (int i=0; i<data.getClipData().getItemCount();i++){
                    imageAmount=imageAmount+1;
                    imageUri=new Uri[imageAmount];
                    imageUri[imageUri.length-1]=data.getClipData().getItemAt(i).getUri();
                    Bundle args=new Bundle();
                    args.putString("the image", imageUri[imageUri.length-1].toString());
                    ImageFragment chosenImageFragment= new ImageFragment();
                    chosenImageFragment.setArguments(args);
                    chosenViewsArrayList.add(chosenImageFragment);
                    chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                    createMemorySlider.setAdapter(chosenViewsAdapter);
                }
            }
            else if (data.getData() !=null){
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
            else{
                Toast.makeText(getApplicationContext(),"please choose an image",Toast.LENGTH_LONG).show();
            }
        }

        else if (requestCode==PICK_VIDEO_CODE && resultCode==Activity.RESULT_OK) {
            if(data.getClipData()!=null){
                for(int i=0;i<data.getClipData().getItemCount();i++){
                    videoAmount=videoAmount+1;
                    recordedVideoUri=new Uri[videoAmount];
                    recordedVideoUri[recordedVideoUri.length-1]=data.getClipData().getItemAt(i).getUri();
                    Bundle args = new Bundle();
                    args.putString("the video", recordedVideoUri[recordedVideoUri.length-1].toString());
                    VidFragment chosenVideoFragment = new VidFragment();
                    chosenVideoFragment.setArguments(args);
                    chosenViewsArrayList.add(chosenVideoFragment);
                    chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                    createMemorySlider.setAdapter(chosenViewsAdapter);
                }
            }
            else if (recordedVideoUri != null) {
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

            } else {
                Toast.makeText(getApplicationContext(), "please choose a video", Toast.LENGTH_LONG).show();
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

//        closePopup.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

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