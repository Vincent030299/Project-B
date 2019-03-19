package com.example.myapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import static com.example.myapplication.R.color.colorAccent;
import static com.example.myapplication.R.color.colorPrimary;
import static com.example.myapplication.R.color.dark;
import static com.example.myapplication.R.color.white;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch;
    TextView text1;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = this.getWindow().getDecorView();
        view.setBackgroundResource(R.color.white);
        text1 = (TextView)findViewById(R.id.Text);

        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked == true) {
                    view.setBackgroundResource(white);
                    text1.setTextColor(Color.BLACK);
                } else{
                    view.setBackgroundResource(dark);
                    text1.setTextColor(Color.WHITE);
                }
            }
        });

    }
    public void Nightmode(View v){
        view.setBackgroundResource(colorAccent);
    }
}
