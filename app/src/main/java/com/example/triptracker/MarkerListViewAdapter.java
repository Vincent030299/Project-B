package com.example.triptracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MarkerListViewAdapter extends BaseAdapter {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    ArrayList<String> markerNames;
    private TextView markerName, markerColorView;
    private ImageButton deleteMarkerBtn;
    private ArrayList<Integer> markerColors;
    private ArrayList<Integer> markerIds;
    private Context context;
    private FragmentManager fragmentManager;
    private float displayWidth;
    private ImageButton deleteMemoryBtn;

    public MarkerListViewAdapter(Context context, ArrayList<String> markerNames, ArrayList<Integer> markerColors, ArrayList<Integer> markerIds, FragmentManager fragmentManager) {
        this.markerNames = markerNames;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.markerColors = markerColors;
        this.markerIds = markerIds;

    }

    public boolean addName(String s) {
        return markerNames.add(s);
    }

    @Override
    public int getCount() {
        return markerNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        displayWidth=context.getResources().getDisplayMetrics().xdpi;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View singleMarker;

        singleMarker = layoutInflater.inflate(R.layout.markerlistitem, parent, false);


        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            TextView markerNameColor = singleMarker.findViewById(R.id.markerName);
            markerNameColor.setTextColor(Color.rgb(20,145,218));
        }


        deleteMarkerBtn = singleMarker.findViewById(R.id.delete);
        markerName = singleMarker.findViewById(R.id.markerName);
        markerColorView = singleMarker.findViewById(R.id.markerColor);
        float hsv[] = new float[] {markerColors.get(position),100,100};
        int color = Color.HSVToColor(hsv);
        markerColorView.setBackgroundColor(color);
        markerName.setText(markerNames.get(position));
        deleteMemoryBtn = singleMarker.findViewById(R.id.delete);

        deleteMemoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(markerIds.get(position));
            }
        });

        return singleMarker;
    }

    private void openDeleteDialog(Integer position){
        Bundle memoryPosition = new Bundle();
        memoryPosition.putInt("id",position-1);
        DeleteMarkerDialog deleteMarkerDialog = new DeleteMarkerDialog();
        deleteMarkerDialog.setArguments(memoryPosition);
        deleteMarkerDialog.show(this.fragmentManager,"delete dialog");
    }
}
