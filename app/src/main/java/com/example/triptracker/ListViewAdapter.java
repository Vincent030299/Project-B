package com.example.triptracker;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ListViewAdapter extends BaseAdapter {
    ArrayList<String> memoryTitles,memoryDates;
    ArrayList<Integer> memoryIds;
    private TextView memoryTitle,memoryDate;
    private ImageButton openMemoryBtn,deleteMemoryBtn;
    private Context context;

    public ListViewAdapter(Context context,ArrayList<String> memoryTitles, ArrayList<String> memoryDates, ArrayList<Integer> memoryIds) {
        this.memoryTitles = memoryTitles;
        this.memoryDates = memoryDates;
        this.memoryIds = memoryIds;
        this.context = context;
    }

    public boolean addDate(String s) {
        return memoryDates.add(s);
    }

    public boolean addId(Integer integer) {
        return memoryIds.add(integer);
    }
    public boolean addTitle(String s) {
        return memoryTitles.add(s);
    }

    @Override
    public int getCount() {
        return memoryIds.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View singleMemory = layoutInflater.inflate(R.layout.memorylistitem, parent, false);
        openMemoryBtn = singleMemory.findViewById(R.id.open);
        deleteMemoryBtn = singleMemory.findViewById(R.id.delete);
        memoryDate = singleMemory.findViewById(R.id.singleMemoryDate);
        memoryTitle = singleMemory.findViewById(R.id.singleMemoryTitle);
        memoryTitle.setText(memoryTitles.get(position));
        memoryDate.setText(memoryDates.get(position));
        return singleMemory;
    }
}
