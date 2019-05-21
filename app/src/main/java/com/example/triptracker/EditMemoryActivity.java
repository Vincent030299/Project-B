package com.example.triptracker;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Objects;
import java.util.Timer;
import java.util.TooManyListenersException;
import java.util.zip.Inflater;

/*
these links helped with learning to be able to write the code for this class
    https://www.youtube.com/watch?v=LpL9akTG4hI
*/
public class EditMemoryActivity extends FragmentActivity implements OnMapReadyCallback{

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
    private final int PERMISSION_REQUEST_CODE = 14;
    private ImageButton closePopup,saveMemoryButton,deleteMediaBtn,feelingEmojiBtn,chooseMarkerMenuBtn;
    private TextInputLayout memoryTitle,memoryDescription;
    private DatePicker memoryDate;
    private ArrayList<Uri> imageUri = new ArrayList<>();
    private ArrayList<Uri> recordedVideoUri= new ArrayList<>();
    private ArrayList<Bitmap> imageBitmaps= new ArrayList<>();
    private int currentDay,currentMonth,currentYear;
    private Fragment mapFragment;
    private LinearLayout pageIndicatorView,uploadMediaFilesMenu,mapLayout,mediaFilesLayout,optionsTab,switchAndMediaLayout;
    private FragmentManager createMemoryFragmentManager;
    private android.support.v4.app.Fragment createMemoryMapView;
    private Uri takenPictureUri;
    private String markerColor = "red";
    private Integer color;
    private int screenHeightInPx;
    private ConstraintLayout createMemoryLayout;
    private int feeling = 1000;
    private String feelingDescription;
    private String takenPicturePath;
    private ArrayList<String> memoryImages,memoryVideos;
    private int[] feelingsEmojis = {R.drawable.happy_emoji,R.drawable.relaxed_emoji,R.drawable.blessed_emoji
            ,R.drawable.loved_emoji,R.drawable.crazy_emoji,R.drawable.sad_emoji,R.drawable.tired_emoji,
            R.drawable.thankful_emoji,R.drawable.hopeful_emoji,R.drawable.fantastic_emoji,R.drawable.peaceful_emoji,
            R.drawable.disappointed_emoji,R.drawable.lost_emoji,R.drawable.inspired_emoji,R.drawable.optimistic_emoji};
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
        takenPictureUri = null;
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
        memoryTitle = findViewById(R.id.memoryTitle);
        memoryDate=findViewById(R.id.memoryDate);
        closePopup =findViewById(R.id.closeCreateMemory);
        pageIndicatorView =findViewById(R.id.pageIndicator);
        mapFragment=getSupportFragmentManager().findFragmentById(R.id.editMapFragView);
        mapLayout= findViewById(R.id.mapLayout);
        mediaFilesLayout= findViewById(R.id.mediaFilesLayout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        optionsTab = findViewById(R.id.optionsTab);
        deleteMediaBtn = findViewById(R.id.deleteMediaBtn);
        switchAndMediaLayout = findViewById(R.id.switchAndMediaLayout);
        createMemoryLayout = findViewById(R.id.createMemoryLayout);
        chooseMarkerMenuBtn=findViewById(R.id.customMarkerBtn);
        feelingEmojiBtn = findViewById(R.id.feelingEmojiBtn);

        // Set all the default
        Intent result = getIntent();
        String title = result.getStringExtra("title");
        String description = result.getStringExtra("description");
        String date = result.getStringExtra("date");
        String[] dates = date.split("-");
        Integer emoji = result.getIntExtra("emoji",1);

        int day = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]) - 1;
        int year = Integer.parseInt(dates[2]);

        memoryTitle.getEditText().setText(title);
        memoryDescription.getEditText().setText(description);
        memoryDate.updateDate(year,month,day);
        feelingEmojiBtn.setImageResource(feelingsEmojis[emoji]);
        memoryImages = getIntent().getStringArrayListExtra("images");
        memoryVideos = getIntent().getStringArrayListExtra("videos");
        Log.e("length", String.valueOf(memoryImages.size()));
        Log.e("length", String.valueOf(memoryVideos.size()));

        for(int i = 0; i<memoryImages.size(); i++){
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("the image", memoryImages.get(i));
            ImageFragment singleImageFragment = new ImageFragment();
            singleImageFragment.setArguments(fragmentArgs);
            chosenViewsArrayList.add(singleImageFragment);
        }
        for (int i = 0; i<memoryVideos.size();i++) {
            Bundle fragmentArgs = new Bundle();
            fragmentArgs.putString("the video", memoryVideos.get(i));
            VidFragment singleVideoFragment = new VidFragment();
            singleVideoFragment.setArguments(fragmentArgs);
            chosenViewsArrayList.add(singleVideoFragment);
        }
        chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
        createMemorySlider.setAdapter(chosenViewsAdapter);

        //setting the initial visibility state of pageIndicatorView
        pageIndicatorView.setVisibility(View.INVISIBLE);

        //Adjusting the layout according to the used phone.
        if(1920-getResources().getDisplayMetrics().heightPixels >= 100){
            screenHeight = getResources().getDisplayMetrics().heightPixels;
            LinearLayout.LayoutParams inputLayoutParams = new LinearLayout.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels*0.972),(int)(getResources().getDisplayMetrics().heightPixels*0.2));
            ConstraintLayout.LayoutParams mapFragmentLayoutParams = new ConstraintLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,(int)(getResources().getDisplayMetrics().heightPixels*0.26));
            ConstraintLayout.LayoutParams mediaFilesLayoutParams = new ConstraintLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels, (int)(getResources().getDisplayMetrics().heightPixels*0.26));
            ConstraintLayout.LayoutParams uploadBtnsLayoutParams = new ConstraintLayout.LayoutParams((int)(getResources().getDisplayMetrics().widthPixels*0.972),(int)(getResources().getDisplayMetrics().density*40));
            mapFragmentLayoutParams.topToBottom = R.id.TopBar;
            mapFragmentLayoutParams.endToEnd = R.id.createMemoryLayout;
            mapFragmentLayoutParams.startToStart = R.id.createMemoryLayout;

            mediaFilesLayoutParams.topToBottom = R.id.TopBar;
            mediaFilesLayoutParams.startToStart = R.id.createMemoryLayout;
            mediaFilesLayoutParams.endToEnd = R.id.createMemoryLayout;

            uploadBtnsLayoutParams.startToStart = R.id.createMemoryLayout;
            uploadBtnsLayoutParams.endToEnd = R.id.createMemoryLayout;
            uploadBtnsLayoutParams.topToBottom = R.id.pageIndicator;

            mapLayout.setLayoutParams(mapFragmentLayoutParams);
            memoryDescription.setLayoutParams(inputLayoutParams);
            mediaFilesLayout.setLayoutParams(mediaFilesLayoutParams);
            switchAndMediaLayout.setLayoutParams(uploadBtnsLayoutParams);
            memoryDescription.requestLayout();
            mapLayout.requestLayout();
            mediaFilesLayout.requestLayout();
            switchAndMediaLayout.requestLayout();
        }

        //getting the values of the current date
        currentDay=memoryDate.getDayOfMonth();
        currentMonth=memoryDate.getMonth();
        currentYear=memoryDate.getYear();
        optionsTab.getBackground().setAlpha(75);
        optionsTab.setVisibility(View.INVISIBLE);

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
                PopupMenu uploadBtnsMenu= new PopupMenu(EditMemoryActivity.this, uploadMediaFilesMenu);
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
                                askCameraRequest();
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


        //the functionality to delete a media file
        createMemorySlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                deleteMediaFile(i);
            }

            @Override
            public void onPageSelected(int i) {


            }

            @Override
            public void onPageScrollStateChanged(int i) {

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

        chooseMarkerMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu markerBtnsMenu= new PopupMenu(EditMemoryActivity.this, chooseMarkerMenuBtn);
                markerBtnsMenu.inflate(R.menu.choosemarkermenu);

                Menu menu = markerBtnsMenu.getMenu();

                final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                Cursor markers = databaseHelper.getCustomMarkers();
                while(markers.moveToNext()) {
                    String markerName = markers.getString(1);
                    Integer markerColor = markers.getInt(2);
                    menu.add(markerName);
                }

                markerBtnsMenu.show();

                markerBtnsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                        Cursor markers = databaseHelper.getCustomMarker(item.getTitle().toString());
                        while(markers.moveToNext()) {
                            Integer markerColor = markers.getInt(2);
                            changeMarkerColor(markerColor);
                        }
                        return true;
                    }
                });

            }
        });
        feelingEmojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "feelings");
                final PopupMenu feelingEmojisMenu = new PopupMenu(EditMemoryActivity.this, feelingEmojiBtn);
                feelingEmojisMenu.inflate(R.menu.feelingemojismenu);
                feelingEmojisMenu.show();
                feelingEmojisMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.happyEmoji:
                                feeling = 0;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.happy_emoji);
                                return true;
                            case R.id.relaxedEmoji:
                                feeling = 1;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.relaxed_emoji);
                                return true;
                            case R.id.blessedEmoji:
                                feeling = 2;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.blessed_emoji);
                                return true;
                            case R.id.lovedEmoji:
                                feeling = 3;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.loved_emoji);
                                return true;
                            case R.id.crazyEmoji:
                                feeling = 4;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.crazy_emoji);
                                return true;
                            case R.id.sadEmoji:
                                feeling = 5;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.sad_emoji);
                                return true;
                            case R.id.tiredEmoji:
                                feeling = 6;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.tired_emoji);
                                return true;
                            case R.id.thankfulEmoji:
                                feeling = 7;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.thankful_emoji);
                                return true;
                            case R.id.hopefulEmoji:
                                feeling = 8;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.hopeful_emoji);
                                return true;
                            case R.id.fantasticEmoji:
                                feeling = 9;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.fantastic_emoji);
                                return true;
                            case R.id.peacefulEmoji:
                                feeling = 10;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.peaceful_emoji);
                                return true;
                            case R.id.disappointedEmoji:
                                feeling = 11;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.disappointed_emoji);
                                return true;
                            case R.id.lostEmoji:
                                feeling = 12;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.lost_emoji);
                                return true;
                            case R.id.inspiredEmoji:
                                feeling = 13;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.inspired_emoji);
                                return true;
                            case R.id.optimisticEmoji:
                                feeling = 14;
                                feelingDescription = item.getTitle().toString();
                                feelingEmojiBtn.setImageResource(R.drawable.optimistic_emoji);
                                return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private File createTakenPictureFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        takenPicturePath = image.getAbsolutePath();
        return image;
    }

    //the function for deleting a certain media file
    private void deleteMediaFile(final int position){
        deleteMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenViewsArrayList.remove(position);
                chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                createMemorySlider.setAdapter(chosenViewsAdapter);
                Toast.makeText(getApplicationContext(), "Chosen media file deleted successfully", Toast.LENGTH_SHORT).show();
                if (chosenViewsArrayList.isEmpty()){
                    mapViewVisibility(true);
                }
            }
        });
    }

    //functionality for changing the visibility of map fragment, view pager and page indicator
    private void mapViewVisibility(boolean visible) {
        if (visible){
            pageIndicatorView.setVisibility(View.INVISIBLE);
            mediaFilesLayout.setVisibility(View.INVISIBLE);
            optionsTab.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = createMemoryFragmentManager.beginTransaction();
            fragmentTransaction.show(createMemoryMapView);
            fragmentTransaction.commit();
            mapMediaToggle.setChecked(false);
        }
        else {
            if (chosenViewsArrayList.isEmpty()){
                optionsTab.setVisibility(View.INVISIBLE);
                pageIndicatorView.setVisibility(View.VISIBLE);
                mediaFilesLayout.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction = createMemoryFragmentManager.beginTransaction();
                fragmentTransaction.hide(createMemoryMapView);
                fragmentTransaction.commit();
                mapMediaToggle.setChecked(true);
            }
            else{
                pageIndicatorView.setVisibility(View.VISIBLE);
                mediaFilesLayout.setVisibility(View.VISIBLE);
                optionsTab.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction = createMemoryFragmentManager.beginTransaction();
                fragmentTransaction.hide(createMemoryMapView);
                fragmentTransaction.commit();
                mapMediaToggle.setChecked(true);
            }

        }
    }

    //the function for saving a memory.
    private void saveMemory() {
        int chosenDay= memoryDate.getDayOfMonth();
        int chosenMonth= memoryDate.getMonth();
        int chosenYear=memoryDate.getYear();
        if (memoryTitle.getEditText().getText().length()==0 ){
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
        else if (memoryTitle.getEditText().getText().length()>20){
            Toast.makeText(getApplicationContext(), "The title is too long, try again", Toast.LENGTH_SHORT).show();
        }
        else if (feeling == 1000){
            Toast.makeText(getApplicationContext(), "Please choose a feeling, click on the smile icon", Toast.LENGTH_LONG).show();
        }
        else {
            String currentMemoryTitle=memoryTitle.getEditText().getText().toString();
            String currentMemoryDescription= memoryDescription.getEditText().getText().toString();
            String currentMemoryDate= String.valueOf(chosenDay)+'-'+String.valueOf(chosenMonth)+'-'+String.valueOf(chosenYear);
            DatabaseHelper memoryDatabase=new DatabaseHelper(getApplicationContext());

            if(memoryDatabase.addData(currentMemoryTitle, currentMemoryDate, currentMemoryDescription, imageUri, recordedVideoUri, imageBitmaps,point.latitude, point.longitude, color,feeling,feelingDescription)){
                Toast.makeText(getApplicationContext(), "Memory saved successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location", point);
                resultIntent.putExtra("title", currentMemoryTitle);
                resultIntent.putExtra("color", color);
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
        Intent upload= new Intent(Intent.ACTION_OPEN_DOCUMENT);
        upload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        upload.setType("image/*");
        mapViewVisibility(false);
        startActivityForResult(upload,PICK_IMAGE_CODE);
    }

    //asks for permission to use camera and storage
    private void askCameraRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
                String[] permissionrequest = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissionrequest, PERMISSION_REQUEST_CODE);
            }
            else{
                takePic();
            }
        }
        else{
            takePic();
        }

    }
    //starts the activity of taking a pic
    private void takePic(){
//        ContentValues takenPicInfo = new ContentValues();
//        takenPicInfo.put(MediaStore.Images.Media.TITLE, "New picture");
//        takenPicInfo.put(MediaStore.Images.Media.DESCRIPTION, "From camera");
//        takenPictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,takenPicInfo);
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        takenPictureUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "TripTracker" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCamera.resolveActivity(getPackageManager()) != null){
            File takenPictureFile = null;
            try {
                takenPictureFile = createTakenPictureFile();
            }
            catch (IOException ex){
                Toast.makeText(getApplicationContext(), "Failed creating an image file", Toast.LENGTH_SHORT).show();
            }
            if (takenPictureFile != null){
                takenPictureUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", takenPictureFile);
                openCamera.putExtra(MediaStore.EXTRA_OUTPUT, takenPictureUri);
                startActivityForResult(openCamera, TAKE_PIC_CODE);
                mapViewVisibility(false);
            }
        }

    }

    //starts the activity of choosing a video from the gallery
    private void uploadVid() {
        Intent upload_vid= new Intent(Intent.ACTION_OPEN_DOCUMENT);
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
                optionsTab.setVisibility(View.VISIBLE);
                for (int i=0; i<data.getClipData().getItemCount();i++){
                    imageUri.add(i,data.getClipData().getItemAt(i).getUri());
                    Bundle args=new Bundle();
                    args.putString("the image", imageUri.get(i).toString());
                    ImageFragment chosenImageFragment= new ImageFragment();
                    chosenImageFragment.setArguments(args);
                    chosenViewsArrayList.add(chosenImageFragment);
                    chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                    createMemorySlider.setAdapter(chosenViewsAdapter);
                }
            }
            else if (data.getData() !=null){
                optionsTab.setVisibility(View.VISIBLE);
                imageUri.add(data.getData());
                Bundle args=new Bundle();
                args.putString("the image", imageUri.get(imageUri.size()-1).toString());
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
                optionsTab.setVisibility(View.VISIBLE);
                for(int i=0;i<data.getClipData().getItemCount();i++){
                    recordedVideoUri.add(i,data.getClipData().getItemAt(i).getUri());
                    Bundle args = new Bundle();
                    args.putString("the video", recordedVideoUri.get(i).toString());
                    VidFragment chosenVideoFragment = new VidFragment();
                    chosenVideoFragment.setArguments(args);
                    chosenViewsArrayList.add(chosenVideoFragment);
                    chosenViewsAdapter = new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
                    createMemorySlider.setAdapter(chosenViewsAdapter);
                }
            }
            else if (recordedVideoUri != null) {
                optionsTab.setVisibility(View.VISIBLE);
                recordedVideoUri.add(data.getData());
                Bundle args = new Bundle();
                args.putString("the video", recordedVideoUri.get(recordedVideoUri.size()-1).toString());
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

            optionsTab.setVisibility(View.VISIBLE);
            imageUri.add(takenPictureUri);
            Bundle args=new Bundle();
            args.putString("the image", imageUri.get(imageUri.size()-1).toString());
            ImageFragment chosenImageFragment= new ImageFragment();
            chosenImageFragment.setArguments(args);
            chosenViewsArrayList.add(chosenImageFragment);
            chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
            createMemorySlider.setAdapter(chosenViewsAdapter);

//            if(data.getExtras()==null){
//                Toast.makeText(getApplicationContext(),"Please take a picture",Toast.LENGTH_SHORT).show();
//            }
//            else{
//                optionsTab.setVisibility(View.VISIBLE);
//                imageBitmaps.add((Bitmap) data.getExtras().get("data"));
//                Bundle args=new Bundle();
//                ByteArrayOutputStream takenImageOutputStream= new ByteArrayOutputStream();
//                imageBitmaps.get(imageBitmaps.size()-1).compress(Bitmap.CompressFormat.JPEG,100,takenImageOutputStream);
//                byte[] takenImageByteArray= takenImageOutputStream.toByteArray();
//                args.putString("the cam", Base64.encodeToString(takenImageByteArray,Base64.DEFAULT));
//                CapImageFragment capturedImageFragment= new CapImageFragment();
//                capturedImageFragment.setArguments(args);
//                chosenViewsArrayList.add(capturedImageFragment);
//                chosenViewsAdapter =new SwipeAdapter(getSupportFragmentManager(), chosenViewsArrayList);
//                createMemorySlider.setAdapter(chosenViewsAdapter);
//            }
        }
        else if (requestCode==RECORD_VIDEO_CODE && resultCode== Activity.RESULT_OK){
            if (data.getData() ==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                optionsTab.setVisibility(View.VISIBLE);
                recordedVideoUri.add(data.getData());
                Bundle args=new Bundle();
                args.putString("the video", recordedVideoUri.get(recordedVideoUri.size()-1).toString());
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Intent intent = getIntent();
        Integer markerColor = intent.getIntExtra("color",1);
        point = intent.getParcelableExtra("location");
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        //Create camera zoom to show marker close
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(point).
                zoom(15).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    public void changeMarkerColor(Integer markerColor){
        mMap.clear();
        color = markerColor;
        mMap.addMarker(new MarkerOptions()
                .position(point)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePic();
        }
        else{
            Toast.makeText(getApplicationContext(), "Permission denied... cannot open the camera", Toast.LENGTH_LONG);
        }
    }
}