package com.maha.homework_bmp601;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DelegateApp.db";
    private static final String TABLE_NAME = "Delegates";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_NUMBER = "Number";
    private static final String COL_PHOTO = "Photo";
    private static final String COL_AREA = "Area";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_NUMBER + " TEXT, " +
                COL_PHOTO + " TEXT, " +
                COL_AREA + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new delegate
    public boolean insertDelegate(String name, String number, String photo, String area) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_NUMBER, number);
        contentValues.put(COL_PHOTO, photo);
        contentValues.put(COL_AREA, area);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // Fetch all delegates
    public Cursor getAllDelegates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Update a delegate
    public boolean updateDelegate(int id, String name, String number, String photo, String area) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_NUMBER, number);
        contentValues.put(COL_PHOTO, photo);
        contentValues.put(COL_AREA, area);

        int result = db.update(TABLE_NAME, contentValues, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Delete a delegate
    public boolean deleteDelegate(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}