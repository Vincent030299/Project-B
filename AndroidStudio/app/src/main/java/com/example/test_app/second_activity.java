package com.example.test_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class second_activity extends AppCompatActivity {
    private ImageView sent_image;
    private TextView senttext;
    private Button goback,nextbtn2,prevbtn2;
    TextView title,date;
    VideoView sent_video;
    int vidclickcounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);
        initiante();

        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        senttext.setText(getIntent().getStringExtra("senttext"));
        sent_image.setImageURI(MainActivity.imageuri);
        sent_video.setVideoURI(MainActivity.viduri);
        sent_video.seekTo(1);
        nextbtn2.getBackground().setAlpha(0);
        prevbtn2.getBackground().setAlpha(0);
        sent_image.setVisibility(View.INVISIBLE);
        nextbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sent_video.setVisibility(View.INVISIBLE);
                sent_image.setVisibility(View.VISIBLE);

            }
        });
        prevbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sent_video.getDuration()==0){

                    sent_image.setVisibility(View.INVISIBLE);
                    sent_video.setVisibility(View.VISIBLE);
                }
                else{
                    sent_video.setVisibility(View.VISIBLE);
                    sent_video.seekTo(1);
                    sent_image.setVisibility(View.INVISIBLE);

                }
            }
        });

        sent_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sent_video!= null & vidclickcounter==0){
                sent_video.start();
                vidclickcounter++;
            }
            else if (sent_video!=null & vidclickcounter==1){
                sent_video.pause();
                vidclickcounter=0;
            }
        }});


        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home_clicked();
            }
        });

    }

    private void initiante() {
        sent_image= findViewById(R.id.sent_image);
        senttext= findViewById(R.id.sent_text);
        goback=findViewById(R.id.go_back) ;
        nextbtn2=findViewById(R.id.nextbtn2);
        prevbtn2=findViewById(R.id.prevbtn2);
        title=findViewById(R.id.sent_title);
        date=findViewById(R.id.sent_date);
        sent_video=findViewById(R.id.sent_video);
    }

    private void Home_clicked() {
        Intent homepage=new Intent(this,MainActivity.class);
        startActivity(homepage);
    }
}
