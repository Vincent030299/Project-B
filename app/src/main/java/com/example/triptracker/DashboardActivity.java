package com.example.triptracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.sip.SipSession;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EventListener;

public class DashboardActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private ListView memoriesList;
    private ArrayList<String> memoryTitles,memoryDates,memoryDiscriptions;
    private ArrayList<Integer> memoryIds;
    protected ListViewAdapter memoryListAdapter;
    private LinearLayout sortLayout;


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
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if(useDarkTheme) {
            setTheme(R.style.memoriesThemeDark);
        } else {
            setTheme(R.style.memoriesTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        memoriesList = findViewById(R.id.memoriesList);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        Intent sort = getIntent();
        Cursor memories;
        if (sort.getStringExtra("name") != null) {
            memories = databaseHelper.getDataOrderName();
        } else if (sort.getStringExtra("date") != null ) {
            memories = databaseHelper.getDataOrderDate();
        } else if (sort.getStringExtra("description") != null ) {
            memories = databaseHelper.getDataOrderDescription();
        } else {
            memories = databaseHelper.getData();
        }
        memoryTitles = new ArrayList<>();
        memoryDates = new ArrayList<>();
        memoryIds  = new ArrayList<>();
        memoryDiscriptions = new ArrayList<>();

        sortLayout = findViewById(R.id.sortLayout);
        sortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu sortMenu = new PopupMenu(DashboardActivity.this, sortLayout);
                sortMenu.inflate(R.menu.sort_menu);
                sortMenu.show();
                sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.name:
                                Intent sortName = new Intent(getApplicationContext(), DashboardActivity.class);
                                sortName.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sortName.putExtra("name","name");
                                getApplicationContext().startActivity(sortName);
                                break;
                            case R.id.date:
                                Intent sortDate = new Intent(getApplicationContext(), DashboardActivity.class);
                                sortDate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sortDate.putExtra("date","date");
                                getApplicationContext().startActivity(sortDate);
                                break;
                            case R.id.description:
                                Intent sortDescription = new Intent(getApplicationContext(), DashboardActivity.class);
                                sortDescription.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sortDescription.putExtra("description","description");
                                getApplicationContext().startActivity(sortDescription);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        while(memories.moveToNext()){
            Integer id = memories.getInt(0);
            String title = memories.getString(1);
            String date = memories.getString(3);
            String discription = memories.getString(2);
            Log.e("date", date);
            Log.e("title",title);
            memoryDates.add(date);
            memoryIds.add(id);
            memoryTitles.add(title);
            memoryDiscriptions.add(discription);
        }
        memoryListAdapter = new ListViewAdapter(getApplicationContext(),memoryTitles, memoryDates, memoryIds,memoryDiscriptions,getSupportFragmentManager());
        memoriesList.setAdapter(memoryListAdapter);
    }

    public void openActivity(Class className) {
        Intent intent = new Intent(this, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0, 0);
        finish();
        startActivity(intent);
    }

//    @Override
//    public void onBackPressed() {
//        Toast.makeText(getApplicationContext(), "Please use the navigation bar to navigate", Toast.LENGTH_LONG).show();
//    }
}
