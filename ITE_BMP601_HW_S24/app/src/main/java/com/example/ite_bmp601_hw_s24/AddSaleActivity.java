package com.example.ite_bmp601_hw_s24;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AddSaleActivity extends AppCompatActivity {
    EditText ed_name,ed_month,ed_year, ed_north, ed_south, ed_west, ed_east, ed_lebanon;
    Button btn_profit, btn_search;
    Button btn_view_sales;
    Button btn_view_commissions;

    ImageView im_yourPhoto;
    TextView tv_yourRegion;
    SQLiteDatabase db;
    MediaPlayer mediaPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);

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


        db = openOrCreateDatabase("EmployeeDB", Context.MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS sales");
        //db.execSQL("DROP TABLE IF EXISTS commissions");
        db.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                "Rollno NVARCHAR PRIMARY KEY, " +
                "Name NVARCHAR UNIQUE NOT NULL, " +
                "Region NVARCHAR NOT NULL, " +
                "JoinDate NVARCHAR)");

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

        db.execSQL("CREATE TABLE IF NOT EXISTS commissions (" +
                "Name NVARCHAR NOT NULL, " +
                "Month INTEGER NOT NULL, " +
                "Year INTEGER NOT NULL, " +
                "TotalCommission NUMBER DEFAULT 0, " +
                "PRIMARY KEY (Name, Month, Year), " +
                "FOREIGN KEY (Name) REFERENCES employees(Name) ON DELETE CASCADE)");

        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);

        btn_profit.setOnClickListener(this::onProfitClick);
        btn_search.setOnClickListener(this::onSearchClick);
        btn_view_sales.setOnClickListener(view -> viewSales());
        btn_view_commissions.setOnClickListener(view -> viewCommissions());

    }

    public void onSearchClick(View view) {
        // جلب اسم الموظف من الحقل
        String name = ed_name.getText().toString().trim();

        // التحقق من أن الاسم غير فارغ
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an employee name", Toast.LENGTH_SHORT).show();
            return;
        }

        // تنفيذ استعلام SQL للبحث عن الموظف بناءً على الاسم
        Cursor cursor = db.rawQuery("SELECT Region FROM employees WHERE Name = ?", new String[]{name});

        // إذا تم العثور على الموظف
        if (cursor.moveToFirst()) {
            String region = cursor.getString(0);
            tv_yourRegion.setText("Region: " + region);

            // عرض الصورة الافتراضية دائمًا
            im_yourPhoto.setImageResource(R.drawable.ic_placeholder);
        } else {
            // إذا لم يتم العثور على الموظف
            Toast.makeText(this, "Employee not found", Toast.LENGTH_SHORT).show();
            tv_yourRegion.setText("Region: Not Found");
            im_yourPhoto.setImageResource(R.drawable.ic_placeholder);
        }

        // إغلاق المؤشر
        cursor.close();
    }


    public void onProfitClick(View view) {
        String name = ed_name.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter an employee name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmployeeExists(name)) {
            Toast.makeText(this, "Employee does not exist. Please add the employee first.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String month = ed_month.getText().toString().trim();
            String year = ed_year.getText().toString().trim();

            double north = Double.parseDouble(ed_north.getText().toString());
            double south = Double.parseDouble(ed_south.getText().toString());
            double west = Double.parseDouble(ed_west.getText().toString());
            double east = Double.parseDouble(ed_east.getText().toString());
            double lebanon = Double.parseDouble(ed_lebanon.getText().toString());

            double totalCommission = calculateTotalCommission(name, north, south, west, east, lebanon);

            if (isSalesExists(name, month, year)) {
                playAlertSound(); // تشغيل الصوت
                showReplaceDialog(name, month, year, totalCommission, north, south, west, east, lebanon);
            } else {
                insertSales(name, month, year, north, south, west, east, lebanon);
                insertCommission(name, month, year, totalCommission);

                // عرض رسالة النجاح
                Toast.makeText(this, "Sales and commission added successfully!", Toast.LENGTH_LONG).show();

                // عرض رسالة إضافية كنافذة حوار
                new AlertDialog.Builder(this)
                        .setTitle("Operation Successful")
                        .setMessage("Sales and commission have been successfully added to the database.\nTotal Commission: " + totalCommission + " S.P")
                        .setPositiveButton("OK", null)
                        .show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid sales values.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isEmployeeExists(String name) {
        Cursor cursor = db.rawQuery("SELECT * FROM employees WHERE Name = ?", new String[]{name});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    private void insertSales(String name, String month, String year, double north, double south, double west, double east, double lebanon) {
        db.execSQL("INSERT INTO sales (Name, Month, Year, SaleNorthRegion, SaleSouthRegion, SaleWestRegion, SaleEastRegion, SaleLebanonRegion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{name, month, year, north, south, west, east, lebanon});
    }

    private void insertCommission(String name, String month, String year, double totalCommission) {
        db.execSQL("INSERT INTO commissions (Name, Month, Year, TotalCommission) VALUES (?, ?, ?, ?)",
                new Object[]{name, month, year, totalCommission});
    }

    private void showReplaceDialog(String name, String month, String year, double totalCommission, double north, double south, double west, double east, double lebanon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Replace Existing Data?");
        builder.setMessage("Sales data for this employee already exists for the same month and year. Do you want to replace it?");
        builder.setPositiveButton("Replace", (dialog, which) -> {
            updateSales(name, month, year, north, south, west, east, lebanon);
            updateCommission(name, month, year, totalCommission);
            showSuccessDialog(totalCommission);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSuccessDialog(double totalCommission) {
        new AlertDialog.Builder(this)
                .setTitle("Operation Successful")
                .setMessage("The operation was successful!\nTotal Commission: " + totalCommission + " S.P")
                .setPositiveButton("OK", null)
                .show();
    }
    private void viewSales() {
        Cursor cursor = db.rawQuery("SELECT * FROM sales", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No sales records found.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            builder.append("Sale ID: ").append(cursor.getInt(0))
                    .append("\nName: ").append(cursor.getString(1))
                    .append("\nMonth: ").append(cursor.getInt(2))
                    .append("\nYear: ").append(cursor.getInt(3))
                    .append("\nNorth: ").append(cursor.getDouble(4))
                    .append("\nSouth: ").append(cursor.getDouble(5))
                    .append("\nWest: ").append(cursor.getDouble(6))
                    .append("\nEast: ").append(cursor.getDouble(7))
                    .append("\nLebanon: ").append(cursor.getDouble(8))
                    .append("\n\n");
        }
        cursor.close();

        new AlertDialog.Builder(this)
                .setTitle("Sales Records")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void viewCommissions() {
        Cursor cursor = db.rawQuery("SELECT * FROM commissions", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No commission records found.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            builder.append("Name: ").append(cursor.getString(0))
                    .append("\nMonth: ").append(cursor.getInt(1))
                    .append("\nYear: ").append(cursor.getInt(2))
                    .append("\nTotal Commission: ").append(cursor.getDouble(3))
                    .append("\n\n");
        }
        cursor.close();

        new AlertDialog.Builder(this)
                .setTitle("Commission Records")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }


    private boolean isSalesExists(String name, String month, String year) {
        String query = "SELECT * FROM sales WHERE Name = ? AND Month = ? AND Year = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name.trim(), month.trim(), year.trim()});

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        return exists;
    }

    private void updateSales(String name, String month, String year, double north, double south, double west, double east, double lebanon) {
        db.execSQL("UPDATE sales SET SaleNorthRegion = ?, SaleSouthRegion = ?, SaleWestRegion = ?, SaleEastRegion = ?, SaleLebanonRegion = ? " +
                        "WHERE Name = ? AND Month = ? AND Year = ?",
                new Object[]{north, south, west, east, lebanon, name, month, year});
    }

    private void updateCommission(String name, String month, String year, double totalCommission) {
        db.execSQL("UPDATE commissions SET TotalCommission = ? WHERE Name = ? AND Month = ? AND Year = ?",
                new Object[]{totalCommission, name, month, year});
    }

    private double calculateTotalCommission(String name, double north, double south, double west, double east, double lebanon) {
        String employeeRegion = getEmployeeRegion(name);

        double commissionNorth = calculateCommission(north, employeeRegion.equals("North"));
        double commissionSouth = calculateCommission(south, employeeRegion.equals("South"));
        double commissionWest = calculateCommission(west, employeeRegion.equals("West"));
        double commissionEast = calculateCommission(east, employeeRegion.equals("East"));
        double commissionLebanon = calculateCommission(lebanon, employeeRegion.equals("Lebanon"));

        double totalCommission=(commissionNorth + commissionSouth + commissionWest + commissionEast + commissionLebanon);
        totalCommission = Math.round(totalCommission);
        return totalCommission;
    }

    private String getEmployeeRegion(String name) {
        Cursor cursor = db.rawQuery("SELECT Region FROM employees WHERE Name = ?", new String[]{name});
        if (cursor.moveToFirst()) {
            String region = cursor.getString(0);
            cursor.close();
            return region;
        }
        cursor.close();
        return "Unknown";
    }

    private double calculateCommission(double sales, boolean isSameRegion) {
        if (sales <= 100000000) {
            return sales * (isSameRegion ? 0.05 : 0.03);
        } else {
            return 100000000 * (isSameRegion ? 0.05 : 0.03) + (sales - 1000000) * (isSameRegion ? 0.07 : 0.04);
        }
    }

    private void playAlertSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
