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
    EditText ed_name, ed_month, ed_year;
    Button btn_search;
    TextView tv_result;
    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_commission);

        // ربط عناصر واجهة المستخدم
        ed_name = findViewById(R.id.ed_name);
        ed_month = findViewById(R.id.ed_month);
        ed_year = findViewById(R.id.ed_year);
        btn_search = findViewById(R.id.btn_search);
        tv_result = findViewById(R.id.tv_result);

        // فتح قاعدة البيانات
        db = openOrCreateDatabase("EmployeeDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS commissions(Name NVARCHAR, Month INTEGER, Year INTEGER, TotalCommission NUMBER)");


        btn_search.setOnClickListener(view -> searchCommission());
    }

    @SuppressLint("SetTextI18n")
    private void searchCommission() {
        String name = ed_name.getText().toString().trim();
        String monthStr = ed_month.getText().toString().trim();
        String yearStr = ed_year.getText().toString().trim();

        if (name.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int month, year;
        try {
            month = Integer.parseInt(monthStr);
            year = Integer.parseInt(yearStr);

            if (month < 1 || month > 12) {
                Toast.makeText(this, "Invalid month. Enter a value between 1 and 12.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (year < 1900 || year > 2100) { // تحديد نطاق زمني منطقي للسنة
                Toast.makeText(this, "Invalid year. Enter a valid year.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Month and Year must be numeric.", Toast.LENGTH_SHORT).show();
            return;
        }

        // استعلام
        Cursor cursor = db.rawQuery("SELECT TotalCommission FROM commissions WHERE Name = ? AND Month = ? AND Year = ?",
                new String[]{name, String.valueOf(month), String.valueOf(year)});

        if (cursor.getCount() == 0) {
            tv_result.setText("No commission found for the given criteria.");
        } else {
            cursor.moveToFirst();
            String totalCommission = cursor.getString(0); // TotalCommission
            tv_result.setText(String.format("Commission for %s:\nTotal: %s S.P", name, totalCommission));
        }

        cursor.close();
    }
}
