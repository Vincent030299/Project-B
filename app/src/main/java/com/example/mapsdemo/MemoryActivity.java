package com.example.mapsdemo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MemoryActivity extends AppCompatActivity {

    Button btn;
    LatLng latlng;
    String title, desc;
    EditText titleField, descField;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        getSupportActionBar().setTitle("Create memory");

        Intent intent = getIntent();
        latlng = intent.getParcelableExtra("location");

        titleField = findViewById(R.id.titleField);
        descField = findViewById(R.id.descField);
        btn = findViewById(R.id.submitMemory);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = titleField.getText().toString();
                desc = descField.getText().toString();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("location", latlng);
                resultIntent.putExtra("title", title);
                resultIntent.putExtra("description", desc);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });
    }

}