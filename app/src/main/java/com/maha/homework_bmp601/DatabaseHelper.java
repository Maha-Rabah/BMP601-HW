package com.maha.homework_bmp601;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SalesApp.db";
    private static final String TABLE_NAME = "Representatives";
    private static final String COL_ID = "ID";
    private static final String COL_NAME = "Name";
    private static final String COL_PHONE = "Phone";
    private static final String COL_REGION = "Region";
    private static final String COL_IMAGE = "Image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_REGION + " TEXT, " +
                COL_IMAGE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // إضافة مندوب جديد
    public boolean insertRepresentative(String name, String phone, String region, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_REGION, region);
        contentValues.put(COL_IMAGE, image);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // جلب جميع المندوبين
    public Cursor getAllRepresentatives() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // تحديث بيانات مندوب
    public boolean updateRepresentative(int id, String name, String phone, String region, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_PHONE, phone);
        contentValues.put(COL_REGION, region);
        contentValues.put(COL_IMAGE, image);

        int result = db.update(TABLE_NAME, contentValues, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // حذف مندوب
    public boolean deleteRepresentative(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}