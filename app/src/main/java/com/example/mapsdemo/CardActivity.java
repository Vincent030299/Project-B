package com.example.mapsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CardActivity extends AppCompatActivity {

    TextView markerTitle, markerDesc, markerPos;
    LatLng pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card);

        Intent intent = getIntent();
        String title = intent.getStringExtra("markerTitle");
        String desc = intent.getStringExtra("markerDesc");
        pos = intent.getParcelableExtra("markerPos");

        markerTitle = findViewById(R.id.markerTitle);
        markerDesc = findViewById(R.id.markerDesc);
        markerPos = findViewById(R.id.markerPos);

        markerTitle.setText(title);
        markerDesc.setText(desc);
        markerPos.setText(String.valueOf(pos));

    }

}
