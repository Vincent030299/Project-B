package com.example.triptracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "Memory";
    private static final String COL_MEMORY_NAME = "memory_name";
    private static final String COL_MEMORY_ID = "memory_id";
    private static final String COL_MEMORY_DATE = "memory_date";
    private static final String COL_MEMORY_DESCRIPTION = "memory_description";
    private static final String COL_MEDIA_PATH = "media_path";
    private static final String COL_MARKER_LAT = "marker_lat";
    private static final String COL_MARKER_LONG = "marker_long";

    /*
    constructor is getting used when i implement the one to many relation thats why the error at super
    */
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEMORY_NAME +" TEXT,"+ COL_MEMORY_DESCRIPTION + "TEXT," + COL_MEMORY_DATE + "TEXT," + COL_MEDIA_PATH + "TEXT," + COL_MARKER_LAT + "REAL," + COL_MARKER_LONG + "REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert 1 memory to the database
    public boolean addData(String memoryName, String memoryDate, String memoryDescription, String mediaPath, Double markerLat, Double markerLong) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MEMORY_NAME, memoryName);
        contentValues.put(COL_MEMORY_DATE, memoryDate);
        contentValues.put(COL_MEMORY_DESCRIPTION, memoryDescription);
        contentValues.put(COL_MEDIA_PATH, mediaPath);
        contentValues.put(COL_MARKER_LAT, markerLat);
        contentValues.put(COL_MARKER_LONG, markerLong);

        Log.d(TAG, "addData: Adding " + memoryName + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);

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

    /**
     * Returns one Memory
     * @param id
     * @return
     */
    public Cursor getItem(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_MEMORY_ID + " = '" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the memory
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName, String newDate, String newDescription, String newMediaPath, Double newMarkerLat, Double newMarkerLong){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_MEMORY_NAME +
                " = '" + newName + "'," + COL_MEMORY_DATE + " = '"+ newDate +
                "', " + COL_MEMORY_DESCRIPTION + "= '" + newDescription +
                "', " + COL_MEDIA_PATH + "= '" + COL_MEDIA_PATH +
                "', " + COL_MARKER_LAT + "= '" + COL_MARKER_LAT +
                "', " + COL_MARKER_LONG + "= '" + COL_MARKER_LONG +
                "' WHERE " + COL_MEMORY_ID + " = '" + id + "'" +
                " AND " + COL_MEMORY_NAME + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }

    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL_MEMORY_ID + " = '" + id + "'" +
                " AND " + COL_MEMORY_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }
}
