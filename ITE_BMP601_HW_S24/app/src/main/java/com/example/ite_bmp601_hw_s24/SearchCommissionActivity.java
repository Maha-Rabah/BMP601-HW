package com.example.ite_bmp601_hw_s24;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SearchCommissionActivity extends AppCompatActivity {
    //declaring variables
    EditText ed_name, ed_month, ed_year;
    Button btn_search;
    TextView tv_result;
    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_commission);

        // Initialize views

        ed_name = findViewById(R.id.ed_name);
        ed_month = findViewById(R.id.ed_month);
        ed_year = findViewById(R.id.ed_year);
        btn_search = findViewById(R.id.btn_search);
        tv_result = findViewById(R.id.tv_result);

        // Initialize database

        db = openOrCreateDatabase("EmployeeDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS commissions(Name NVARCHAR, Month INTEGER, Year INTEGER, TotalCommission NUMBER)");
        // Search button click listener to handle search
        btn_search.setOnClickListener(view -> searchCommission());
    }
    // Method to search commission
    @SuppressLint("SetTextI18n")
    private void searchCommission() {
        String name = ed_name.getText().toString().trim().toLowerCase(); //case sensitive
        String monthStr = ed_month.getText().toString().trim();
        String yearStr = ed_year.getText().toString().trim();
        // Entered Data Validation
        if (name.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int month, year;
        try {
            month = Integer.parseInt(monthStr);
            year = Integer.parseInt(yearStr);
            // Entered Data Validation for month
            if (month < 1 || month > 12) {
                Toast.makeText(this, "Invalid month. Enter a value between 1 and 12.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Entered Data Validation for year
            if (year < 1900 || year > 2100) { // تحديد نطاق زمني منطقي للسنة
                Toast.makeText(this, "Invalid year. Enter a valid year.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            // Entered Data Validation for month and year
            Toast.makeText(this, "Month and Year must be numeric.", Toast.LENGTH_SHORT).show();
            return;
        }
        //sql statement to retrieve data from the database
        Cursor cursor = db.rawQuery("SELECT TotalCommission FROM commissions WHERE Name = ? AND Month = ? AND Year = ?",
                new String[]{name, String.valueOf(month), String.valueOf(year)});

        if (cursor.getCount() == 0) {
            //display error message if no commission found
            tv_result.setText("No commission found for the given criteria.");
        } else {
            //extract the retrieved data and storing in variables
            cursor.moveToFirst();
            String totalCommission = cursor.getString(0); // TotalCommission
            tv_result.setText(String.format("Commission for %s:\nTotal: %s S.P", name, totalCommission));
        }
        cursor.close();
    }
}
