package com.example.test_app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class second_activity extends MainActivity {
    private TextView senttext;
    private Button goback;
    TextView title,date;
    swipeadapter secondadapter;
    ViewPager secondpager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_activity);
        initiante();

        title.setText(getIntent().getStringExtra("title"));
        date.setText(getIntent().getStringExtra("date"));
        senttext.setText(getIntent().getStringExtra("senttext"));


        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home_clicked();
            }
        });

    }

    private void initiante() {
        secondpager=findViewById(R.id.secondviewpager);
        ArrayList<Fragment> mylist = super.mlist;
        secondadapter= new swipeadapter(getSupportFragmentManager(),mylist);
        secondpager.setAdapter(secondadapter);
        senttext= findViewById(R.id.sent_text);
        goback=findViewById(R.id.go_back) ;
        title=findViewById(R.id.sent_title);
        date=findViewById(R.id.sent_date);
    }

    private void Home_clicked() {
        Intent homepage=new Intent(this,MainActivity.class);
        startActivity(homepage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
