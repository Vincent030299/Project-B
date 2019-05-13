package com.example.triptracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    // The memory database
    private static final String TABLE_NAME = "memory";
    private static final String COL_MEMORY_NAME = "memory_name";
    private static final String COL_MEMORY_ID = "memory_id";
    private static final String COL_MEMORY_DATE = "memory_date";
    private static final String COL_MEMORY_DESCRIPTION = "memory_description";
    private static final String COL_MARKER_LAT = "marker_lat";
    private static final String COL_MARKER_LONG = "marker_long";
    private static final String COL_MEMORY_FEELING = "memory_feeling";
    private static final String COL_MEMORY_FEELING_DESCRIPTION = "memory_feeling_description";

    // The image database
    private static final String IMAGE_NAME = "image";
    private static final String COL_IMAGE_ID = "id";
    private static final String COL_IMAGE_URI = "image_uri";

    // The video database
    private static final String VIDEO_NAME = "video";
    private static final String COL_VIDEO_ID = "id";
    private static final String COL_VIDEO_URI = "video_uri";

    // The image capture database
    private static final String IMAGE_CAPTURE_NAME = "image_capture";
    private static final String COL_IMAGE_CAPTURE_ID = "id";
    private static final String COL_IMAGE_CAPTURE_BITMAP = "image_capture_bitmap";

    /*
    constructor is getting used when i implement the one to many relation thats why the error at super
    */
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEMORY_NAME +" TEXT,"+ COL_MEMORY_DESCRIPTION + " TEXT," + COL_MEMORY_DATE + " TEXT," + COL_MARKER_LAT + " TEXT," + COL_MARKER_LONG + " TEXT," + COL_MEMORY_FEELING + " INTEGER," + COL_MEMORY_FEELING_DESCRIPTION + " TEXT)";

        String tableImage = "CREATE TABLE " + IMAGE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_IMAGE_URI + " TEXT," + COL_MEMORY_ID + " INTEGER)";

        String tableVideo = "CREATE TABLE " + VIDEO_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_VIDEO_URI + " TEXT," + COL_MEMORY_ID + " INTEGER)";

        String tableImageCapture = "CREATE TABLE " + IMAGE_CAPTURE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_IMAGE_CAPTURE_BITMAP + " BLOB, memory_id INTEGER)";

        db.execSQL(createTable);
        db.execSQL(tableImage);
        db.execSQL(tableImageCapture);
        db.execSQL(tableVideo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert 1 memory to the database
    public boolean addData(String memoryName, String memoryDate, String memoryDescription, ArrayList<Uri> images, ArrayList<Uri> videos, ArrayList<Bitmap> imageCaptures, Double markerLat, Double markerLong,int feeling,String feelingDescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MEMORY_NAME, memoryName);
        contentValues.put(COL_MEMORY_DATE, memoryDate);
        contentValues.put(COL_MEMORY_DESCRIPTION, memoryDescription);
        contentValues.put(COL_MARKER_LAT, markerLat);
        contentValues.put(COL_MARKER_LONG, markerLong);
        contentValues.put(COL_MEMORY_FEELING, feeling);
        contentValues.put(COL_MEMORY_FEELING_DESCRIPTION, feelingDescription);
        Log.d(TAG, "addData: Adding " + memoryName + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);

        // get latest row
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC LIMIT 1";
        Cursor data = db.rawQuery(query, null);

        if (!images.isEmpty()) {
            for (int x = 0; x < images.size(); x++) {
                ContentValues imageValues = new ContentValues();
                imageValues.put(COL_IMAGE_URI, images.get(x).toString());
                if (data.moveToFirst()){
                    imageValues.put(COL_MEMORY_ID, data.getInt(0));
                }
                db.insert(IMAGE_NAME, null, imageValues);
            }
        }

        if (!videos.isEmpty()) {
            for (int x = 0; x < videos.size(); x++) {
                Log.d("this is the video strin", videos.get(x).toString());
                ContentValues videoValues = new ContentValues();
                videoValues.put(COL_VIDEO_URI, videos.get(x).toString());
                if (data.moveToFirst()){
                    videoValues.put(COL_MEMORY_ID, data.getInt(0));
                }

                db.insert(VIDEO_NAME, null, videoValues);
            }
        }

        if (!imageCaptures.isEmpty()) {
            for (int x = 0; x < imageCaptures.size(); x++) {
                ByteArrayOutputStream takenImageOutputStream= new ByteArrayOutputStream();
                imageCaptures.get(x).compress(Bitmap.CompressFormat.JPEG,100,takenImageOutputStream);
                byte[] takenImageByteArray= takenImageOutputStream.toByteArray();

                ContentValues imageCaptureValues = new ContentValues();
                imageCaptureValues.put(COL_IMAGE_CAPTURE_BITMAP, takenImageByteArray);
                if (data.moveToFirst()){
                    imageCaptureValues.put(COL_MEMORY_ID, data.getInt(0));
                }
                db.insert(IMAGE_CAPTURE_NAME, null, imageCaptureValues);
            }
        }

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public Cursor getSingleMemoryData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + "ID" + " = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns one Memory
     * @param markerLat
     * @param markerLong
     * @return
     */
    public Cursor getItem(Double markerLat, Double markerLong){
        SQLiteDatabase db = this.getWritableDatabase();
        String markerLatText = String.valueOf(markerLat);
        String markerLongText = String.valueOf(markerLong);
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_MARKER_LAT + " = " + markerLatText + " AND " + COL_MARKER_LONG + " = " + markerLongText;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getImage(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + IMAGE_NAME +
                " WHERE " + COL_MEMORY_ID + " = " + id;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the memory
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName, String newDate, String newDescription, String newImageUri,String newImageBitmap,String newVideoUri, Double newMarkerLat, Double newMarkerLong){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_MEMORY_NAME +
                " = '" + newName + "'," + COL_MEMORY_DATE + " = '"+ newDate +
                "', " + COL_MEMORY_DESCRIPTION + "= '" + newDescription +
                "', " + COL_MARKER_LAT + "= '" + newMarkerLat +
                "', " + COL_MARKER_LONG + "= '" + newMarkerLong +
                "' WHERE " + COL_MEMORY_ID + " = '" + id + "'" +
                " AND " + COL_MEMORY_NAME + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }

    /**
     * Delete from database
     * @param id
     */
    public void deleteName(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id =" + id;
        String deleteImages = "DELETE FROM " + IMAGE_NAME + " WHERE " + COL_MEMORY_ID + " = " + id;
        String deleteVideos = "DELETE FROM " + VIDEO_NAME + " WHERE " + COL_MEMORY_ID + " = " + id;
        String deleteTakenImages = "DELETE FROM " + IMAGE_CAPTURE_NAME + " WHERE " + COL_MEMORY_ID + " = " + id;
        Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
        db.execSQL(deleteImages);
        db.execSQL(deleteVideos);
        db.execSQL(deleteTakenImages);
    }

    public Cursor getImages(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + IMAGE_NAME +
                " WHERE " + COL_MEMORY_ID + " = " + id;
        Cursor images = db.rawQuery(query, null);
        return images;
    }

    public Cursor getPicturesBitmaps(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + IMAGE_CAPTURE_NAME +
                " WHERE " + COL_MEMORY_ID + " = " + id;
        Cursor imagesBitmaps = db.rawQuery(query, null);
        return imagesBitmaps;
    }

    public Cursor getVideos(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + VIDEO_NAME + " WHERE " + COL_MEMORY_ID + " = " + id;
        Cursor videos = db.rawQuery(query, null);
        return videos;
    }
}
