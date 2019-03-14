package com.example.test_app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Date;

/*used to learn and produce this code
https://www.youtube.com/watch?v=HZYYjY2NSKk
https://www.youtube.com/watch?v=2BBrD0wL78s
https://developer.android.com
https://www.youtube.com/playlist?list=PLGLfVvz_LVvSPjWpLPFEfOCbezi6vATIh
*/


public class MainActivity extends AppCompatActivity {
    public static Uri imageuri,viduri;
    private EditText Description;
    private final int  pick_image_code=1000;
    private final int pick_video_code=1001;
    private final int take_pic_code=1002;
    private final int record_vid_code=1003;
    private  static String text;
    DatePicker chosen_date;
    EditText title;
    ImageView mainimage;
    VideoView mainvideo;
    int todaysdate,theMonth,year;
    ImageButton choose_pic_gallery,choose_vid_gallery,take_pic,record_vid;
    ViewPager mypager;
    swipeadapter myadapter;
    ArrayList<Fragment> mlist=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Description= findViewById(R.id.Description);
        ImageButton save_button = findViewById(R.id.Save_button);
        chosen_date=findViewById(R.id.date);
        title= findViewById(R.id.title);
        choose_pic_gallery=findViewById(R.id.gallery_image);
        choose_vid_gallery=findViewById(R.id.upload_video);
        take_pic=findViewById(R.id.take_pic);
        record_vid=findViewById(R.id.record_vid);
        mypager=findViewById(R.id.myviewpager);
        myadapter=new swipeadapter(getSupportFragmentManager(),mlist);
        mypager.setAdapter(myadapter);



        todaysdate= chosen_date.getDayOfMonth();
        theMonth=chosen_date.getMonth();
        year=chosen_date.getYear();

        choose_pic_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
            }
        });
        choose_vid_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadvideo();
            }
        });
        take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takepic();
            }
        });
        record_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordvid();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text=Description.getText().toString();
                Save_content();
            }
        });

    }

    private void recordvid() {
        Intent recordvid= new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(recordvid,record_vid_code);
    }

    private void takepic() {
        Intent takepic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takepic,take_pic_code);

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
        else if(mlist.size()==0){
            Toast.makeText(getApplicationContext(),"Please choose an image or a video",Toast.LENGTH_SHORT).show();
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
                Bundle args=new Bundle();
                args.putString("the image",imageuri.toString());
                imagefragment thefrag= new imagefragment();
                thefrag.setArguments(args);
                mlist.add(thefrag);
                myadapter=new swipeadapter(getSupportFragmentManager(),mlist);
                mypager.setAdapter(myadapter);

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
                mlist.add(thefrag);
                myadapter=new swipeadapter(getSupportFragmentManager(),mlist);
                mypager.setAdapter(myadapter);
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
                mlist.add(thefrag);
                myadapter=new swipeadapter(getSupportFragmentManager(),mlist);
                mypager.setAdapter(myadapter);
            }

        }
        else if (requestCode==record_vid_code && resultCode==Activity.RESULT_OK){

            viduri=data.getData();
            if (viduri==null){
                Toast.makeText(getApplicationContext(),"please choose a video",Toast.LENGTH_LONG).show();
            }
            else{
                Bundle args=new Bundle();
                args.putString("the video",viduri.toString());
                vidfragment thefrag= new vidfragment();
                thefrag.setArguments(args);
                mlist.add(thefrag);
                myadapter=new swipeadapter(getSupportFragmentManager(),mlist);
                mypager.setAdapter(myadapter);
            }

        }
        else {
            Toast.makeText(getApplicationContext(),"please choose a video or an image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
