package com.example.ite_bmp601_hw_s24;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddSaleActivity extends AppCompatActivity {
    //declaring variables
    EditText ed_name,ed_month,ed_year, ed_north, ed_south, ed_west, ed_east, ed_lebanon;
    Button btn_profit, btn_search;
    Button btn_view_sales;
    Button btn_view_commissions;
    ImageView im_yourPhoto;
    TextView tv_yourRegion;
    SQLiteDatabase db;
    MediaPlayer mediaPlayer;
    //declaring variables for image
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private byte[] imageBytes;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);

        // Initialize the ActivityResultLauncher

        ed_name = findViewById(R.id.ed_name);
        ed_year=findViewById(R.id.ed_year);
        ed_month=findViewById(R.id.ed_month);
        ed_north = findViewById(R.id.ed_north);
        ed_south = findViewById(R.id.ed_south);
        ed_west = findViewById(R.id.ed_west);
        ed_east = findViewById(R.id.ed_east);
        ed_lebanon = findViewById(R.id.ed_lebanon);
        btn_profit = findViewById(R.id.btn_profit);
        btn_search = findViewById(R.id.btn_search);
        im_yourPhoto = findViewById(R.id.im_yourPhoto);
        tv_yourRegion = findViewById(R.id.tv_yourRegion);
        btn_view_sales = findViewById(R.id.btn_view_sales);
        btn_view_commissions = findViewById(R.id.btn_view_commissions);

        //Database creation
        db = openOrCreateDatabase("EmployeeDB", Context.MODE_PRIVATE, null);
        //table employees
        db.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                "Rollno NVARCHAR PRIMARY KEY, " +
                "Name NVARCHAR UNIQUE, " +
                "Region NVARCHAR, " +
                "JoinDate NVARCHAR, " +
                "Photo BLOB)");
        //table sales
        db.execSQL("CREATE TABLE IF NOT EXISTS sales (" +
                "Saleno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name NVARCHAR NOT NULL, " +
                "Month NVARCHAR NOT NULL, " +
                "Year NVARCHAR NOT NULL, " +
                "SaleNorthRegion NUMBER DEFAULT 0, " +
                "SaleSouthRegion NUMBER DEFAULT 0, " +
                "SaleWestRegion NUMBER DEFAULT 0, " +
                "SaleEastRegion NUMBER DEFAULT 0, " +
                "SaleLebanonRegion NUMBER DEFAULT 0, " +
                "FOREIGN KEY (Name) REFERENCES employees(Name) ON DELETE CASCADE)");
        //table commissions
        db.execSQL("CREATE TABLE IF NOT EXISTS commissions (" +
                "Name NVARCHAR NOT NULL, " +
                "Month INTEGER NOT NULL, " +
                "Year INTEGER NOT NULL, " +
                "TotalCommission NUMBER DEFAULT 0, " +
                "PRIMARY KEY (Name, Month, Year), " +
                "FOREIGN KEY (Name) REFERENCES employees(Name) ON DELETE CASCADE)");
        //media player sound effect for message
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
        //profit button start method to start method to insert sale into database
        btn_profit.setOnClickListener(this::onProfitClick);
        //search button start method to search employee in database
        btn_search.setOnClickListener(this::onSearchClick);
        //view sales button start method to view sales from database
        btn_view_sales.setOnClickListener(view -> viewSales());
        //view commissions button start method to view commissions from database
        btn_view_commissions.setOnClickListener(view -> viewCommissions());
        //image view to default image
        im_yourPhoto.setImageResource(R.drawable.ic_default_photo);
    }
    //Method to search employee in database
    @SuppressLint("SetTextI18n")
    public void onSearchClick(View view) {
        //extract the Rollno entered to search
        String name = ed_name.getText().toString().trim().toLowerCase();
        //checking entry validity
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an employee name", Toast.LENGTH_SHORT).show();
            return;
        }
        //SQL statement to retrieve data from the database
        Cursor cursor = db.rawQuery("SELECT Region, Photo FROM employees WHERE Name = ?", new String[]{name});
        //checking if employee exists
        try {
            if (cursor.moveToFirst()) {
                //extract the retrieved data and storing in variables
                String region = cursor.getString(0);
                byte[] photoBytes = cursor.getBlob(1); // Get the image as BLOB
                //display the retrieved data in message
                tv_yourRegion.setText("Region: " + region);
                //display the retrieved image in imageView
                if (photoBytes != null) {
                    // Convert byte array to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
                    im_yourPhoto.setImageBitmap(bitmap);//setting image to the imageView
                } else {
                    im_yourPhoto.setImageResource(R.drawable.ic_placeholder);//setting default image to the imageView
                }
            } else {
                //display error message if no employee found
                Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
                tv_yourRegion.setText("Region: Not Found");
                im_yourPhoto.setImageResource(R.drawable.ic_placeholder);
            }
        } finally {
            cursor.close();
        }
    }
    //Method to insert sale into database
    public void onProfitClick(View view) {
        //checking entry validity
        String name = ed_name.getText().toString().trim().toLowerCase();
        if (name.isEmpty()) {
            //display error message if no employee found
            Toast.makeText(this, "Please enter an employee name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmployeeExists(name)) {
            //display error message if no employee found
            Toast.makeText(this, "Employee does not exist. Please add the employee first.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //extracting data from views
            String month = ed_month.getText().toString().trim();//extract from month view
            String year = ed_year.getText().toString().trim();//extract from year view
            //sales data
            double north = Double.parseDouble(ed_north.getText().toString());//extract from north view
            double south = Double.parseDouble(ed_south.getText().toString());//extract from south view
            double west = Double.parseDouble(ed_west.getText().toString());//extract from west view
            double east = Double.parseDouble(ed_east.getText().toString());//extract from east view
            double lebanon = Double.parseDouble(ed_lebanon.getText().toString());//extract from lebanon view
            //Calling method to calculate the commission as per the numbers entered
            double totalCommission = calculateTotalCommission(name, north, south, west, east, lebanon);
            //checking if sales already exists
            if (isSalesExists(name, month, year)) {
                playAlertSound(); // تشغيل الصوت
                showReplaceDialog(name, month, year, totalCommission, north, south, west, east, lebanon);
            } else {
                //calling method to insert data into sales database
                insertSales(name, month, year, north, south, west, east, lebanon);
                //calling method to insert data into commissions database
                insertCommission(name, month, year, totalCommission);
                Toast.makeText(this, "Sales and commission added successfully!", Toast.LENGTH_LONG).show();
                //display success message
                new AlertDialog.Builder(this)
                        .setTitle("Operation Successful")
                        .setMessage("Sales and commission have been successfully added to the database.\nTotal Commission: " + formatNumber(totalCommission) + " S.P")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid sales values.", Toast.LENGTH_SHORT).show();
        }
    }
    //Method to check if employee exists
    private boolean isEmployeeExists(String name) {
        //SQL statement to retrieve data from the database
        Cursor cursor = db.rawQuery("SELECT * FROM employees WHERE Name = ?", new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    //Method to insert data into sales database
    private void insertSales(String name, String month, String year, double north, double south, double west, double east, double lebanon) {
        db.execSQL("INSERT INTO sales (Name, Month, Year, SaleNorthRegion, SaleSouthRegion, SaleWestRegion, SaleEastRegion, SaleLebanonRegion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{name, month, year, north, south, west, east, lebanon});
    }
    //Method to insert data into commissions database
    private void insertCommission(String name, String month, String year, double totalCommission) {
        db.execSQL("INSERT INTO commissions (Name, Month, Year, TotalCommission) VALUES (?, ?, ?, ?)",
                new Object[]{name, month, year, totalCommission});
    }
    //Method to replace existing data
    private void showReplaceDialog(String name, String month, String year, double totalCommission, double north, double south, double west, double east, double lebanon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //display message of replace existing data statement
        builder.setTitle("Replace Existing Data?");
        builder.setMessage("Sales data for this employee already exists for the same month and year. Do you want to replace it?");
        builder.setPositiveButton("Replace", (dialog, which) -> {
            //calling method to update data in sales database
            updateSales(name, month, year, north, south, west, east, lebanon);
            //calling method to update data in commissions database
            updateCommission(name, month, year, totalCommission);
            //display success message
            showSuccessDialog(totalCommission);
            Toast.makeText(this, "Replacement successfully Done", Toast.LENGTH_SHORT).show();
        });
        //display cancel button
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    //Method to display success message
    private void showSuccessDialog(double totalCommission) {
        new AlertDialog.Builder(this)
                .setTitle("Operation Successful")
                .setMessage("The operation was successful!\nTotal Commission: " + totalCommission + " S.P")
                .setPositiveButton("OK", null)
                .show();
    }
    //Method to view sales from database
    private void viewSales() {
        //SQL statement to retrieve all data from the database
        Cursor cursor = db.rawQuery("SELECT * FROM sales", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No sales records found.", Toast.LENGTH_SHORT).show();
            return;
        }
        //display the retrieved data in message
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            builder.append("Sale ID: ").append(cursor.getInt(0))
                    .append("\nName: ").append(cursor.getString(1))
                    .append("\nMonth: ").append(cursor.getInt(2))
                    .append("\nYear: ").append(cursor.getInt(3))
                    .append("\nNorth: ").append(formatNumber(cursor.getDouble(4)))
                    .append("\nSouth: ").append(formatNumber(cursor.getDouble(5)))
                    .append("\nWest: ").append(formatNumber(cursor.getDouble(6)))
                    .append("\nEast: ").append(formatNumber(cursor.getDouble(7)))
                    .append("\nLebanon: ").append(formatNumber(cursor.getDouble(8)))
                    .append("\n\n");
        }
        cursor.close();
        //display the retrieved data in message
        new AlertDialog.Builder(this)
                .setTitle("Sales Records")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }
    // Helper method to format numbers
    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value); // Format as integer if no decimal part
        } else {
            return String.format("%.2f", value); // Format as fixed-point decimal
        }
    }
    //Method to view commissions from database
    private void viewCommissions() {
        //sql statement to retrieve all data from the database
        Cursor cursor = db.rawQuery("SELECT * FROM commissions", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No commission records found.", Toast.LENGTH_SHORT).show();
            return;
        }
        //display the retrieved data in message
        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            builder.append("Name: ").append(cursor.getString(0))
                    .append("\nMonth: ").append(cursor.getInt(1))
                    .append("\nYear: ").append(cursor.getInt(2))
                    .append("\nTotal Commission: ").append(formatNumber(cursor.getDouble(3)))
                    .append("\n\n");
        }
        cursor.close();
        //display the retrieved data in message
        new AlertDialog.Builder(this)
                .setTitle("Commission Records")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }
    //Method to check if sales already exists
    private boolean isSalesExists(String name, String month, String year) {
        //SQL statement to retrieve data from the database
        String query = "SELECT * FROM sales WHERE Name = ? AND Month = ? AND Year = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name.trim(), month.trim(), year.trim()});
        //checking if sales already exists
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    //Method to update data in sales database
    private void updateSales(String name, String month, String year, double north, double south, double west, double east, double lebanon) {
        //SQL statement to update data in the database
        db.execSQL("UPDATE sales SET SaleNorthRegion = ?, SaleSouthRegion = ?, SaleWestRegion = ?, SaleEastRegion = ?, SaleLebanonRegion = ? " +
                        "WHERE Name = ? AND Month = ? AND Year = ?",
                new Object[]{north, south, west, east, lebanon, name, month, year});
    }
    //Method to update data in commissions database
    private void updateCommission(String name, String month, String year, double totalCommission) {
        //SQL statement to update data in the database
        db.execSQL("UPDATE commissions SET TotalCommission = ? WHERE Name = ? AND Month = ? AND Year = ?",
                new Object[]{totalCommission, name, month, year});
    }
    //Method to calculate the total commissions
    private double calculateTotalCommission(String name, double north, double south, double west, double east, double lebanon) {
        String employeeRegion = getEmployeeRegion(name);
        //storing data from views to be calculated
        double commissionNorth = calculateCommission(north, employeeRegion.equals("North"));
        double commissionSouth = calculateCommission(south, employeeRegion.equals("South"));
        double commissionWest = calculateCommission(west, employeeRegion.equals("West"));
        double commissionEast = calculateCommission(east, employeeRegion.equals("East"));
        double commissionLebanon = calculateCommission(lebanon, employeeRegion.equals("Lebanon"));
        //calculating total commission
        double totalCommission=(commissionNorth + commissionSouth + commissionWest + commissionEast + commissionLebanon);
        totalCommission = Math.round(totalCommission);
        return totalCommission;
    }
    //Method to get employee region
    private String getEmployeeRegion(String name) {
        //SQL statement to get the region of the entered name
        Cursor cursor = db.rawQuery("SELECT Region FROM employees WHERE Name = ?", new String[]{name});
        if (cursor.moveToFirst()) {
            String region = cursor.getString(0);
            cursor.close();
            return region;
        }
        cursor.close();
        return "Unknown";
    }
    //Method to calculate the commission
    private double calculateCommission(double sales, boolean isSameRegion) {
        //calculation logic
        if (sales <= 100000000) {
            //calculation if the sale is less than 100000000 ( if same region > yes commission is 5% no commission is 3%)
            return sales * (isSameRegion ? 0.05 : 0.03);
        } else {
            //calculation if the sale is more than 100000000
            return 100000000 * (isSameRegion ? 0.05 : 0.03) + (sales - 1000000) * (isSameRegion ? 0.07 : 0.04);
        }
    }
    //alert sound effect for message
    private void playAlertSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    //alert sound effect for message stop
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }

}
