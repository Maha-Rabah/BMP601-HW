package com.maha.homework_bmp601;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DelegateApp.db";
    public static final int DATABASE_VERSION = 1;

    private static final String TABLE_Delegate = "Delegates";
    private static final String COL_Delegate_ID = "delegateID";
    private static final String COL_Delegate_NAME = "delegate_name";
    private static final String COL_Delegate_Number = "delegate_number";
    private static final String COL_Delegate_Photo = "delegate_photo";
    //REPLACED WITH REGION to link databases delegates and Region
    //private static final String COL_Region = "region";
    public static final String TABLE_REGION = "region";
    //region id is the link between most tables
    public static final String COL_REGION_ID = "regionID";
    public static final String COL_REGION_NAME = "region_name";
    public static final String TABLE_SALE = "sale";
    public static final String COL_SALE_ID = "saleID";
    public static final String COL_SALE_AMOUNT = "sale_amount";
    public static final String COL_SALE_MONTH = "sale_month";
    public static final String COL_SALE_YEAR = "sale_year";
    public static final String TABLE_COMMISSION = "commission";
    public static final String COL_COMMISSION_ID = "commissionID";
    public static final String COL_COMMISSION_AMOUNT = "commission_amount";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     try{
        String delegates_query = "CREATE TABLE " + TABLE_Delegate + " (" +
                COL_Delegate_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Delegate_NAME + " TEXT, " +
                COL_Delegate_Number + " TEXT, " +
                COL_Delegate_Photo + " TEXT, " +
                COL_REGION_ID + " INTEGER,"+
                "FOREIGN KEY (" + COL_REGION_ID + ") REFERENCES " + TABLE_REGION + "(" + COL_REGION_ID + "));";
        String region_query = "CREATE TABLE " + TABLE_REGION +
                 " (" + COL_REGION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_REGION_NAME + " TEXT NOT NULL);";
        String region_insert_query = "INSERT INTO " + TABLE_REGION + " (" + COL_REGION_NAME + ") VALUES " +
                 "('لبنان')," +
                 "('سوريا المنطقة الشمالية')," +
                 "('سوريا المنطقة الجنوبية')," +
                 "('سوريا المنطقة الشرقية')," +
                 "('سوريا المنطقة الغربية');";
        String sale_query = "CREATE TABLE " + TABLE_SALE +
                 " (" + COL_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_Delegate_ID + " INTEGER NOT NULL," +
                COL_REGION_ID + " INTEGER NOT NULL," +
                 COL_SALE_AMOUNT + " REAL NOT NULL," +
                 COL_SALE_MONTH + " INTEGER NOT NULL," +
                 COL_SALE_YEAR + " INTEGER NOT NULL," +
                 "FOREIGN KEY (" + COL_Delegate_ID + ") REFERENCES " + TABLE_Delegate + "(" + COL_Delegate_ID + ")," +
                 "FOREIGN KEY (" + COL_REGION_ID + ") REFERENCES " + TABLE_REGION + "(" + COL_REGION_ID + "));";

        String commission_query = "CREATE TABLE " + TABLE_COMMISSION  +
                 " (" + COL_COMMISSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_Delegate_ID + " INTEGER NOT NULL," +
                 COL_SALE_ID + " INTEGER NOT NULL," +
                 COL_COMMISSION_AMOUNT + " REAL NOT NULL," +
                 "FOREIGN KEY (" + COL_Delegate_ID + ") REFERENCES " + TABLE_Delegate + "(" + COL_Delegate_ID + ")," +
                 "FOREIGN KEY (" + COL_SALE_ID + ") REFERENCES " + TABLE_SALE + "(" + COL_SALE_ID + "));";
        db.execSQL(region_query);
        db.execSQL(region_insert_query);
        db.execSQL(delegates_query);
        db.execSQL(sale_query);
        db.execSQL(commission_query);
    } catch (SQLException e) {
        // Handle the exception (e.g., log the error)
        Log.e("Database", "Error creating tables: " + e.getMessage());
    }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Delegate);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMISSION);
            onCreate(db);
        } catch (SQLException e) {
            // Handle the exception (e.g., log the error)
            Log.e("Database", "Error upgrading database: " + e.getMessage());
        }
    }

    // Insert a new delegate
    public boolean insertDelegate(String name, String number, String photo, String area) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_Delegate_NAME, name);
        contentValues.put(COL_Delegate_Number, number);
        contentValues.put(COL_Delegate_Photo, photo);
        //make the region as a select from drop menu not entered manually it will insert into the database the region_id to be linked with region table
        //contentValues.put(COL_Region, area);

        long result = db.insert(TABLE_Delegate, null, contentValues);
        return result != -1;
    }

    // Fetch all delegates
    public Cursor getAllDelegates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_Delegate, null);
    }

    // Update a delegate
    public boolean updateDelegate(int id, String name, String number, String photo, String area) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_Delegate_NAME, name);
        contentValues.put(COL_Delegate_Number, number);
        contentValues.put(COL_Delegate_Photo, photo);
        //make the region as a select from drop menu not entered manually it will insert into the database the region_id to be linked with region table
        //contentValues.put(COL_Region, area);

        int result = db.update(TABLE_Delegate, contentValues, COL_Delegate_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    // Delete a delegate
    public boolean deleteDelegate(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_Delegate, COL_Delegate_ID + "=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}