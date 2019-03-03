package com.example.test_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.VideoView;

public class Card_view extends AppCompatActivity {

    ImageView firstimage;
    VideoView video1;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_test);
        firstimage=findViewById(R.id.firstimage);
        video1=findViewById(R.id.secondimage);
    }
}