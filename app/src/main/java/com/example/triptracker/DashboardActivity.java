package com.example.triptracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private ListView memoriesList;
    private ArrayList<String> memoryTitles,memoryDates;
    private ArrayList<Integer> memoryIds;
    private ListViewAdapter memoryListAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openActivity(MapsActivity.class);
                    return true;
                case R.id.navigation_dashboard:
                    openActivity(DashboardActivity.class);
                    return true;
                case R.id.navigation_settings:
                    openActivity(SettingsActivity.class);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        memoriesList = findViewById(R.id.memoriesList);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor memories = databaseHelper.getData();
        memoryTitles = new ArrayList<>();
        memoryDates = new ArrayList<>();
        memoryIds  = new ArrayList<>();
        memoryListAdapter = new ListViewAdapter(getApplicationContext(),memoryTitles, memoryDates, memoryIds);
        while(memories.moveToNext()){
            Integer id = memories.getInt(0);
            String title = memories.getString(1);
            String date = memories.getString(3);
            Log.e("date", date);
            Log.e("title",title);
            memoryListAdapter.addDate(date);
            memoryListAdapter.addId(id);
            memoryListAdapter.addTitle(title);
        }
        memoriesList.setAdapter(memoryListAdapter);
    }

    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
