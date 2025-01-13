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

public class SearchSalesActivity extends AppCompatActivity {
    EditText ed_name, ed_month, ed_year;
    Button btn_search;
    TextView tv_result;
    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sales);

        ed_name = findViewById(R.id.ed_name);
        ed_month = findViewById(R.id.ed_month);
        ed_year = findViewById(R.id.ed_year);
        btn_search = findViewById(R.id.btn_search);
        tv_result = findViewById(R.id.tv_result);

        db = openOrCreateDatabase("EmployeeDB", MODE_PRIVATE, null);


        btn_search.setOnClickListener(view -> searchSales());
    }

    @SuppressLint("SetTextI18n")
    private void searchSales() {
        String name = ed_name.getText().toString().trim();
        String month = ed_month.getText().toString().trim();
        String year = ed_year.getText().toString().trim();

        if (name.isEmpty() || month.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM sales WHERE Name = ? AND Month = ? AND Year = ?",
                new String[]{name, month, year});

        if (cursor.getCount() == 0) {
            tv_result.setText("No sales found for the given criteria.");
        } else {
            StringBuilder result = new StringBuilder();
            while (cursor.moveToNext()) {
                String north = cursor.getString(4); // SaleNorthRegion
                String south = cursor.getString(5); // SaleSouthRegion
                String west = cursor.getString(6);  // SaleWestRegion
                String east = cursor.getString(7);  // SaleEastRegion
                String lebanon = cursor.getString(8); // SaleLebanonRegion

                result.append("Sales for ").append(name).append(":\n")
                        .append("North: ").append(north).append(" S.P\n")
                        .append("South: ").append(south).append(" S.P\n")
                        .append("West: ").append(west).append(" S.P\n")
                        .append("East: ").append(east).append(" S.P\n")
                        .append("Lebanon: ").append(lebanon).append(" S.P\n\n");
            }
            tv_result.setText(result.toString());
        }

        cursor.close();
    }
}
