package com.example.ite_bmp601_hw_s24;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declaring buttons
        Button btnAddEmployee = findViewById(R.id.btn_add_employee);
        Button btnAddSales = findViewById(R.id.btn_add_sales);
        Button btnSearchSales = findViewById(R.id.btn_search_sales);
        Button btnSearchCommission = findViewById(R.id.btn_search_commission);

        //add employee button to start Add_employee activity
        btnAddEmployee.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Add_employee.class);
            startActivity(intent);
        });
        //add sales button to start AddSaleActivity
        btnAddSales.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
            startActivity(intent);
        });
        //search sales button to start SearchSalesActivity
        btnSearchSales.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchSalesActivity.class);
            startActivity(intent);
        });
        //search commission button to start SearchCommissionActivity
        btnSearchCommission.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchCommissionActivity.class);
            startActivity(intent);
        });


    }
}
