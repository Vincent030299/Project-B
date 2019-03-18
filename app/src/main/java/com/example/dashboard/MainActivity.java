package com.example.dashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int[] IMAGES = {R.drawable.holland, R.drawable.ireland,R.drawable.italy,R.drawable.france,R.drawable.germany, R.drawable.belgium, R.drawable.russia, R.drawable.england};

    String[] MARKER_NAME = {"Holland", "Ireland", "Italy", "France", "Germany", "Belgium", "Russia", "England"};

    String[] DATE = {"08-12-2017","28-01-2018","10-02-2018", "15-06-2018", "15-12-2018", "10-01-2019", "20-01-2019", "05-02-2019"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView =  (ListView)findViewById(R.id.listView);

        CustomAdapter customAdapter = new CustomAdapter();

        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return IMAGES.length;
        }

        @Override
        public  Object getItem(int i) {
            return  null;
        }

        @Override
        public long getItemId(int i ) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlayout,null);

            ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
            TextView textView_marker = (TextView)view.findViewById(R.id.textView_marker);
            TextView textView_date = (TextView)view.findViewById(R.id.textView_date);

            imageView.setImageResource(IMAGES[i]);
            textView_marker.setText(MARKER_NAME[i]);
            textView_date.setText(DATE[i]);

            return view;
        }
    }
}
