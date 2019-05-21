package com.example.triptracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ListViewAdapter extends BaseAdapter {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    ArrayList<String> memoryTitles,memoryDates,memoryDiscriptions;
    ArrayList<String> memoryImages = new ArrayList<>();
    ArrayList<String> memoryVideos = new ArrayList<>();
    ArrayList<String> memoryBitmaps = new ArrayList<>();
    ArrayList<Integer> memoryIds;
    private TextView memoryTitle,memoryDate;
    private ImageButton openMemoryBtn,deleteMemoryBtn;
    private Context context;
    private FragmentManager fragmentManager;

    public ListViewAdapter(Context context,ArrayList<String> memoryTitles, ArrayList<String> memoryDates, ArrayList<Integer> memoryIds, ArrayList<String> memoryDiscriptions, FragmentManager fragmentManager) {
        this.memoryTitles = memoryTitles;
        this.memoryDates = memoryDates;
        this.memoryIds = memoryIds;
        this.context = context;
        this.memoryBitmaps = memoryBitmaps;
        this.memoryDiscriptions = memoryDiscriptions;
        this.fragmentManager = fragmentManager;
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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        final View singleMemory;
        if(useDarkTheme) {
            singleMemory = layoutInflater.inflate(R.layout.memorylistitemnight, parent, false);

        }  else {

            singleMemory = layoutInflater.inflate(R.layout.memorylistitem, parent, false);
        }
        openMemoryBtn = singleMemory.findViewById(R.id.open);
        deleteMemoryBtn = singleMemory.findViewById(R.id.delete);
        memoryDate = singleMemory.findViewById(R.id.singleMemoryDate);
        memoryTitle = singleMemory.findViewById(R.id.singleMemoryTitle);
        memoryTitle.setText(memoryTitles.get(position));
        memoryDate.setText(memoryDates.get(position));

        singleMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper mDataBaseHelper = new DatabaseHelper(context.getApplicationContext());
                Cursor allImagesForMemory = mDataBaseHelper.getImages(memoryIds.get(position));
                Cursor allVideosForMemory = mDataBaseHelper.getVideos(memoryIds.get(position));
                Cursor allBitmapsForMemory = mDataBaseHelper.getPicturesBitmaps(memoryIds.get(position));

                while(allImagesForMemory.moveToNext()){
                    String singleImage = allImagesForMemory.getString(1);
                    memoryImages.add(singleImage);
                }
                while (allVideosForMemory.moveToNext()){
                    String singleVideo = allVideosForMemory.getString(1);
                    memoryVideos.add(singleVideo);
                }
                while(allBitmapsForMemory.moveToNext()){
                    byte[] singleBitmap = allBitmapsForMemory.getBlob(1);
                    memoryBitmaps.add(Base64.encodeToString(singleBitmap, Base64.DEFAULT));
                }
                Intent openMemory = new Intent(context.getApplicationContext(), ViewMemoryActivity.class);
                openMemory.addFlags(FLAG_ACTIVITY_NEW_TASK);
                openMemory.putStringArrayListExtra("images", memoryImages);
                openMemory.putStringArrayListExtra("bitmaps", memoryBitmaps);
                openMemory.putStringArrayListExtra("videos", memoryVideos);
                openMemory.putExtra("description", memoryDiscriptions.get(position));
                openMemory.putExtra("title", memoryTitles.get(position));
                openMemory.putExtra("date", memoryDates.get(position));
                Cursor singleMemoryInfos = mDataBaseHelper.getSingleMemoryData(memoryIds.get(position));
                if (singleMemoryInfos.moveToFirst()) {
                    Double lat = singleMemoryInfos.getDouble(4);
                    Double lng = singleMemoryInfos.getDouble(5);
                    openMemory.putExtra("lat", lat);
                    openMemory.putExtra("lng", lng);
                }
                openMemory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.getApplicationContext().startActivity(openMemory);

            }
        });

        deleteMemoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(memoryIds.get(position));
            }
        });
        return singleMemory;
    }

    private void openDeleteDialog(int position){
        Bundle memoryPosition = new Bundle();
        memoryPosition.putInt("id",position);
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.setArguments(memoryPosition);
        deleteDialog.show(this.fragmentManager,"delete dialog");
    }
}
