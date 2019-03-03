package com.example.test_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    public static Uri image,the_chosen_video;
    private ImageView Chosen_element;
    private EditText Description;
    private Button Save_button,button;
    private final int  pick_image_code=1000;
    private final int pick_video_code=2000;
    private  static String text;
    DatePicker chosen_date;
    EditText title;
    VideoView thevideo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Chosen_element= findViewById(R.id.chosen_element);
        Description= findViewById(R.id.Description);
        Save_button= findViewById(R.id.Save_button);
        chosen_date=findViewById(R.id.date);
        title= findViewById(R.id.title);
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Card_view.class);
                startActivity(intent);
            }
        });

        thevideo=findViewById(R.id.video);
        thevideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosevideo();
            }
        });



        Chosen_element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_element();
            }
        });
        Save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text=Description.getText().toString();
                Save_content();
            }
        });
    }

    private void choosevideo() {
        Intent pick_video= new Intent(Intent.ACTION_GET_CONTENT);
        pick_video.setType("video/*");
        startActivityForResult(pick_video,pick_video_code);
    }




    private void upload_element() {
        Intent pick_image= new Intent(Intent.ACTION_GET_CONTENT);
        pick_image.setType("image/*");
        startActivityForResult(pick_image,pick_image_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultdata) {
        if (requestCode==pick_image_code && resultCode== Activity.RESULT_OK){
            image=resultdata.getData();
            Chosen_element.setImageURI(image);
        }
        else if (requestCode==pick_video_code && resultCode==Activity.RESULT_OK){
            the_chosen_video=resultdata.getData();
            thevideo.setVideoURI(the_chosen_video);
        }
    }

    private void Save_content() {
        Intent save_content=new Intent(MainActivity.this,second_activity.class);
        String the_date= String.valueOf(chosen_date.getDayOfMonth())+"-"+String.valueOf(chosen_date.getMonth())+"-"+String.valueOf(chosen_date.getYear());
        save_content.putExtra("senttext",text );
        save_content.putExtra("image",image);
        save_content.putExtra("date",the_date);
        save_content.putExtra("title",title.getText().toString());
        save_content.putExtra("video",the_chosen_video);
        startActivity(save_content);

    }
}
