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
    private Button goback;
    TextView title,date;
    VideoView sent_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);
        sent_image=(ImageView) findViewById(R.id.Sent_image);
        senttext=(TextView) findViewById(R.id.sent_text);
        goback=(Button) findViewById(R.id.go_back) ;
        title=findViewById(R.id.sent_title);
        date=findViewById(R.id.sent_date);
        sent_video=findViewById(R.id.sent_video);
        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        senttext.setText(getIntent().getStringExtra("senttext"));
        sent_image.setImageURI(MainActivity.image);
        sent_video.setVideoURI(MainActivity.the_chosen_video);


        sent_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sent_video!= null){
                sent_video.start();
            }
        }});


        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home_clicked();
            }
        });

    }

    private void Home_clicked() {
        Intent homepage=new Intent(this,MainActivity.class);
        startActivity(homepage);
    }
}
