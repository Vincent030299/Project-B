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
    private static final String TABLE_NAME_VIDEOS = "Videos";
    private static final String TABLE_NAME_PICTURE = "Picture";
    private static final String COL_NAME = "Name";
    private static final String COL_ID = "ID";
    private static final String COL_DATE = "Date";
    private static final String COL_DESCRIPTION = "Description";
    private static final String COL_PATH = "path";
    private static final String COL_MEMORY_ID = "memory_id";

    /*
    constructor is getting used when i implement the one to many relation thats why the error at super
    */
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME +" TEXT,"+ COL_DESCRIPTION + "TEXT," + COL_DATE + "TEXT)";
        String createVideos = "CREATE TABLE " + TABLE_NAME_PICTURE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PATH +" TEXT,"+ COL_MEMORY_ID + "INT,)";
        String createPicture = "CREATE TABLE " + TABLE_NAME_VIDEOS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PATH +" TEXT,"+ COL_MEMORY_ID + "INT,)";
        db.execSQL(createTable);
        db.execSQL(createPicture);
        db.execSQL(createVideos);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, item);
        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME);
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
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_ID + " FROM " + TABLE_NAME +
                " WHERE " + COL_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_NAME +
                " = '" + newName + "' WHERE " + COL_ID + " = '" + id + "'" +
                " AND " + COL_NAME + " = '" + oldName + "'";
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
                + COL_ID + " = '" + id + "'" +
                " AND " + COL_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }
}
