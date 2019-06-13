package com.example.triptracker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tooltip.Tooltip;
import com.viewpagerindicator.CirclePageIndicator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class ViewMemoryActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private ViewPager viewMemoryMediaSlider;
    private TextView viewMemoryTitle,viewMemoryDate,viewMemoryDescription,viewMemoryFeelingDescription;
    private ImageButton viewMemoryShareButton,closeViewMemory,streetViewBtn,openInFullScreen;
    private Button toolTipButton;
    private Fragment viewmemoryMapFragment;
    private CirclePageIndicator viewMemoryDotsIndicator;
    private Switch viewMemoryMediaSwitch;
    private FragmentManager viewMemoryFragmentManager;
    private GoogleMap mMap;
    private SwipeAdapter viewMemorySwipeAdapter;
    private ArrayList<Fragment> memoryViewMediaFiles;
    private String memoryTitle, memoryDescription,memoryDate;
    private ArrayList<Uri> memoryImagesUris,imagesUris,videosUris;
    private ArrayList<String> memoryImages,memoryVideos;
    private LinearLayout mediaFilesLayout;
    private LatLng markerLoc;
    private Float color;
    private DatabaseHelper mDataBaseHelper;
    private ConstraintLayout viewMemoryOptionsTab;
    private ImageView feelingImageView;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            setTheme(R.style.AppThemeNight);
        }
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
        feelingImageView = findViewById(R.id.viewMemoryFeelingImage);
        viewMemoryFeelingDescription = findViewById(R.id.viewMemoryFeelingDescription);
        mDataBaseHelper = new DatabaseHelper(getApplicationContext());
        toolTipButton = findViewById(R.id.toolTipButton2);
        streetViewBtn=findViewById(R.id.streetViewBtn);
        memoryTitle = getIntent().getStringExtra("title");
        memoryDescription = getIntent().getStringExtra("description");
        memoryDate = getIntent().getStringExtra("date");
        markerLoc = new LatLng(getIntent().getExtras().getDouble("lat"), getIntent().getExtras().getDouble("lng"));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        openInFullScreen = findViewById(R.id.viewMemoryOpenInFullScreen);
        viewMemoryOptionsTab = findViewById(R.id.viewMemorytOptionsTab);
        resizeLayout();
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
        if(getIntent().getIntExtra("feelingimage", 1000)!= 1000){
            feelingImageView.setImageResource(feelingsEmojis[getIntent().getIntExtra("feelingimage", 1000)]);
            viewMemoryFeelingDescription.setText("Was feeling " + getIntent().getStringExtra("feelingdesc") + " - ");
        }

        memoryImages = getIntent().getStringArrayListExtra("images");
        memoryVideos = getIntent().getStringArrayListExtra("videos");
        memoryImagesUris = new ArrayList<>();
        imagesUris = new ArrayList<>();
        videosUris = new ArrayList<>();
        memoryViewMediaFiles = new ArrayList<>();


        if (memoryViewMediaFiles.isEmpty()){
            if(!memoryImages.isEmpty()){
                for(int i = 0; i<memoryImages.size(); i++){
                    Bundle fragmentArgs = new Bundle();
                    fragmentArgs.putString("the image", memoryImages.get(i));
                    ImageFragment singleImageFragment = new ImageFragment();
                    singleImageFragment.setArguments(fragmentArgs);
                    memoryViewMediaFiles.add(singleImageFragment);
                    memoryImagesUris.add(Uri.parse(memoryImages.get(i)));
                    imagesUris.add(Uri.parse(memoryImages.get(i)));
                }
            }
            if(!memoryVideos.isEmpty()){
                for (int i = 0; i<memoryVideos.size();i++){
                    Bundle fragmentArgs = new Bundle();
                    fragmentArgs.putString("the video", memoryVideos.get(i));
                    VidFragment singleVideoFragment = new VidFragment();
                    singleVideoFragment.setArguments(fragmentArgs);
                    memoryViewMediaFiles.add(singleVideoFragment);
                    memoryImagesUris.add(Uri.parse(memoryVideos.get(i)));
                    videosUris.add(Uri.parse(memoryVideos.get(i)));
                }
            }
        }
        else {
            memoryViewMediaFiles.clear();
        }



        final SupportMapFragment spmf=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.viewMemoryMap);
        Objects.requireNonNull(spmf).getMapAsync(this);

        viewMemorySwipeAdapter = new SwipeAdapter(getSupportFragmentManager(), memoryViewMediaFiles);
        viewMemorySwipeAdapter.notifyDataSetChanged();
        viewMemoryMediaSlider.setAdapter(viewMemorySwipeAdapter);
        viewMemoryDotsIndicator.setFillColor(Color.rgb(20,145,218));
        viewMemoryDotsIndicator.setRadius(8.0F);
        viewMemoryDotsIndicator.setStrokeColor(Color.rgb(20,145,218));
        viewMemoryOptionsTab.getBackground().setAlpha(75);
        toolTipButton.setVisibility(View.INVISIBLE);
        viewMemoryDotsIndicator.setViewPager(viewMemoryMediaSlider);
        viewMemoryDescription.setMovementMethod(new ScrollingMovementMethod());
        viewMemoryTitle.setMovementMethod(new ScrollingMovementMethod());
        viewMemoryMediaSwitch.setText("Map");

        viewMemoryFragmentManager=getSupportFragmentManager();
        viewmemoryMapFragment=viewMemoryFragmentManager.findFragmentById(R.id.viewMemoryMap);
        viewMemoryMapVisibility(false);
        toolTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToolTip(v);
            }
        });
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
                            case R.id.shareEmail:
                                shareMemory("email");
                                return true;
                            case R.id.shareFacebook:
                                shareMemory("facebook");
                                return true;
                            case R.id.shareInstagram:
                                shareMemory("instagram");
                                return true;
                            case R.id.shareWhatsapp:
                                shareMemory("whatsapp");
                                return true;
                        }
                        return false;
                    }
                });
            }
        });
        streetViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStreetView();
            }
        });
        viewMemoryMediaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (viewMemoryMediaSwitch.isChecked()){
                    viewMemoryMapVisibility(false);
                    viewMemoryMediaSwitch.setText("Map");
                }
                else{
                    viewMemoryMapVisibility(true);
                    viewMemoryMediaSwitch.setText("Media files");
                }
            }
        });
        openInFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openFullSizeMedia = new Intent(ViewMemoryActivity.this,FullSizeMediaFiles.class);
                openFullSizeMedia.putParcelableArrayListExtra("images", imagesUris);
                openFullSizeMedia.putParcelableArrayListExtra("videos", videosUris);
                startActivity(openFullSizeMedia);
            }
        });
    }

    private void resizeLayout() {
        if(1920-getResources().getDisplayMetrics().heightPixels >= 100){
            ConstraintLayout.LayoutParams newMemoryDescriptionDimensions = new ConstraintLayout.LayoutParams((getResources().getDisplayMetrics().widthPixels),(int)(getResources().getDisplayMetrics().heightPixels*0.2));
            newMemoryDescriptionDimensions.topToBottom = R.id.ViewMemoryTitleLayout;
            newMemoryDescriptionDimensions.startToStart = R.id.viewMemoryLayout;
            viewMemoryDescription.setLayoutParams(newMemoryDescriptionDimensions);
            viewMemoryDescription.requestLayout();
        }
    }

    private void openStreetView() {
        Intent openStreetViewActivity = new Intent(this,StreetViewActivity.class);
        openStreetViewActivity.putExtra("memory location", markerLoc);
        startActivity(openStreetViewActivity);
    }

    private void viewMemoryMapVisibility(boolean visible) {
        if (visible){
            viewMemoryDotsIndicator.setVisibility(View.INVISIBLE);
            viewMemoryMediaSlider.setVisibility(View.INVISIBLE);
            viewMemoryOptionsTab.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.show(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(false);
        }
        else {
            viewMemoryDotsIndicator.setVisibility(View.VISIBLE);
            viewMemoryMediaSlider.setVisibility(View.VISIBLE);
            viewMemoryOptionsTab.setVisibility(View.VISIBLE);
            viewMemoryOptionsTab.getBackground().setAlpha(75);
            FragmentTransaction fragmentTransaction = viewMemoryFragmentManager.beginTransaction();
            fragmentTransaction.hide(viewmemoryMapFragment);
            fragmentTransaction.commit();
            viewMemoryMediaSwitch.setChecked(true);
        }
    }
    private void createToolTip(View v) {
        Button btn = (Button)v;
        Tooltip tooltip = new Tooltip.Builder(btn)
                .setText("To move the marker simply long-press the marker and drag it to the location you wish.")
                .setTextColor(Color.BLACK)
                .setGravity(Gravity.BOTTOM)
                .setCornerRadius(8f)
                .setDismissOnClick(true)
                .setCancelable(true)
                .show();

    }

    private void shareMemory(String socialMedia) {
        switch (socialMedia) {
            case "twitter":
                Intent twitter = new Intent();
                try {
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.twitter.android", 0);
                    twitter.setPackage("com.twitter.android");
                    twitter.setAction(Intent.ACTION_SEND);
                    if (!memoryImages.isEmpty()) {
                        twitter.putExtra(Intent.EXTRA_STREAM, Uri.parse(memoryImages.get(0)));
                        twitter.setType("image/jpeg");
                    }
                    else if (!memoryVideos.isEmpty()) {
                        twitter.putExtra(Intent.EXTRA_STREAM, Uri.parse(memoryVideos.get(0)));
                        twitter.setType("video/*");
                    }
                    twitter.putExtra(Intent.EXTRA_TEXT, memoryTitle + " on " + memoryDate);
                    twitter.setType("text/plain");

                    startActivity(twitter);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "You don't have twitter installed on your device.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "email":
                try {
                    Intent shareEmail = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.google.android.gm", 0);
                    shareEmail.setPackage("com.google.android.gm");

                    if (!memoryImages.isEmpty()) {
                        shareEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareEmail.setType("image/jpeg");
                    }

                    else if (!memoryVideos.isEmpty()) {
                        shareEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareEmail.setType("video/*");
                    }
                    shareEmail.putExtra(Intent.EXTRA_SUBJECT, memoryTitle);
                    shareEmail.putExtra(Intent.EXTRA_TEXT, memoryDescription);
                    shareEmail.setType("text/plain");

                    startActivity(shareEmail);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "You don't have gmail installed on your device.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "facebook":
                try {
                    Intent shareFacebook = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.facebook.katana", 0);
                    shareFacebook.setPackage("com.facebook.katana");
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("title + description", memoryTitle + " \n" + memoryDescription);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Title and description added to clipboard", Toast.LENGTH_LONG).show();

                    if (!memoryImages.isEmpty()) {
                        shareFacebook.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareFacebook.setType("image/jpeg");
                    }

                    else if (!memoryVideos.isEmpty()) {
                        shareFacebook.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareFacebook.setType("video/*");
                    }

                    startActivity(shareFacebook);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "You don't have Facebook installed on your device.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "instagram":
                try {
                    Intent shareInstagram = new Intent(Intent.ACTION_SEND);
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.instagram.android", 0);
                    shareInstagram.setPackage("com.instagram.android");
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("title + description", memoryTitle + " \n" + memoryDescription);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Title and description added to clipboard", Toast.LENGTH_LONG).show();
                    if (!memoryImages.isEmpty()) {
                        shareInstagram.putExtra(Intent.EXTRA_STREAM, memoryImagesUris.get(0));
                        shareInstagram.setType("image/jpeg");
                    }
                    else if (!memoryVideos.isEmpty()) {
                        shareInstagram.putExtra(Intent.EXTRA_STREAM, Uri.parse(memoryVideos.get(0)));
                        shareInstagram.setType("video/*");
                    }


                    startActivity(shareInstagram);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "You don't have Instagram installed on your device.", Toast.LENGTH_SHORT).show();
                }
                break;
            case "whatsapp":
                try {
                    Intent shareWhatsapp = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.whatsapp", 0);
                    shareWhatsapp.setPackage("com.whatsapp");

                    if (!memoryImages.isEmpty()) {
                        shareWhatsapp.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareWhatsapp.setType("image/jpeg");
                    }

                    else if (!memoryVideos.isEmpty()) {
                        shareWhatsapp.putParcelableArrayListExtra(Intent.EXTRA_STREAM, memoryImagesUris);
                        shareWhatsapp.setType("video/*");
                    }
                    String whatsAppShareString = memoryTitle + "," + memoryDescription;
                    shareWhatsapp.putExtra(Intent.EXTRA_TEXT, whatsAppShareString);
                    shareWhatsapp.setType("text/plain");

                    startActivity(shareWhatsapp);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "You don't have whatsapp installed on your device.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        Intent intent = getIntent();
        markerLoc = intent.getParcelableExtra("location");
        Integer markerColor = intent.getIntExtra("color",1);
        if (markerLoc==null){
            markerLoc = new LatLng(getIntent().getDoubleExtra("lat", 0.0), getIntent().getDoubleExtra("lng", 0.0));
        }

        mMap.addMarker(new MarkerOptions()
                .position(markerLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        //Create camera zoom to show marker close
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(markerLoc).
                zoom(15).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    //open a given activity
    public void openActivity(Class className) {
        Intent intent = new Intent(getApplicationContext(), className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);

    }
}
