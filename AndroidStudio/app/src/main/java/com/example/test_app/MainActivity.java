package com.example.test_app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.time.MonthDay;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static Uri imageuri,viduri;
//    private ImageView Chosen_element;
    private EditText Description;
    private final int  pick_image_code=1000;
    private final int pick_video_code=2000;
    private  static String text;
    DatePicker chosen_date;
    EditText title;
    ImageView mainimage;
    VideoView mainvideo;
    int todaysdate,theMonth,year;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Description= findViewById(R.id.Description);
        ImageButton save_button = findViewById(R.id.Save_button);
        chosen_date=findViewById(R.id.date);
        title= findViewById(R.id.title);
        Button nextbtn = findViewById(R.id.nextbtn);
        Button prevbtn = findViewById(R.id.prevbtn);
        mainimage= findViewById(R.id.mainimageview);
        mainvideo=findViewById(R.id.mainvideoview);
        nextbtn.getBackground().setAlpha(0);
        prevbtn.getBackground().setAlpha(0);
//        save_button.getBackground().setAlpha(0);
        todaysdate= chosen_date.getDayOfMonth();
        theMonth=chosen_date.getMonth();
        year=chosen_date.getYear();


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainvideo.setVisibility(View.INVISIBLE);
                mainimage.setVisibility(View.VISIBLE);

            }
        });
        prevbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mainvideo.getDuration()==0){

                    mainimage.setVisibility(View.INVISIBLE);
                    mainvideo.setVisibility(View.VISIBLE);
                }
                else{
                    mainvideo.setVisibility(View.VISIBLE);
                    mainvideo.seekTo(1);
                    mainimage.setVisibility(View.INVISIBLE);

                }

            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text=Description.getText().toString();
                Save_content();
            }
        });
        mainimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });
        mainvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadvideo();
            }
        });

    }

    private void Save_content() {
        int secondday= chosen_date.getDayOfMonth();
        int secondmonth= chosen_date.getMonth();
        int secondyear=chosen_date.getYear();
        if (title.getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please enter a title",Toast.LENGTH_SHORT).show();
        }
        else if (Description.getText().length()==0){
            Toast.makeText(getApplicationContext(),"Please enter a description",Toast.LENGTH_SHORT).show();
        }
        else if ((secondday>todaysdate & secondmonth>theMonth & secondyear>year)
                |(secondday>todaysdate & secondmonth==theMonth & year==secondyear)
                |(secondday==todaysdate & secondmonth>theMonth & secondyear==year)
                |(secondday==todaysdate & theMonth==secondmonth & secondyear>year)){
            Toast.makeText(getApplicationContext(),"Please choose another date",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent save_content=new Intent(MainActivity.this,second_activity.class);
            String the_date= String.valueOf(chosen_date.getDayOfMonth())+"-"+String.valueOf(chosen_date.getMonth())+"-"+String.valueOf(chosen_date.getYear());
            save_content.putExtra("senttext",Description.getText().toString() );
            save_content.putExtra("date",the_date);
            save_content.putExtra("title",title.getText().toString());
            startActivity(save_content);
        }

    }
    private void uploadvideo() {
        Intent upload_vid= new Intent(Intent.ACTION_GET_CONTENT);
        upload_vid.setType("video/*");
        startActivityForResult(upload_vid,pick_video_code);
    }

    private void uploadimage() {
        Intent upload= new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("image/*");
        startActivityForResult(upload,pick_image_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==pick_image_code && resultCode== Activity.RESULT_OK){
            imageuri=data.getData();
            if (imageuri==null){

                Toast.makeText(getApplicationContext(),"please choose an image",Toast.LENGTH_LONG).show();
            }
            else{
                mainimage.getForeground().setAlpha(0);
                mainimage.setImageURI(imageuri);
            }


        }
        else if (requestCode==pick_video_code && resultCode==Activity.RESULT_OK){

            viduri=data.getData();
            if (viduri==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                mainvideo.getForeground().setAlpha(0);
                mainvideo.setVideoURI(viduri);
                mainvideo.seekTo(1);
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"please choose a video or an image",Toast.LENGTH_LONG).show();
        }
    }
}
