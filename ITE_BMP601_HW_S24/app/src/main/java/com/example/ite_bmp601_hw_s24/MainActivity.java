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

        Button btnAddEmployee = findViewById(R.id.btn_add_employee);
        Button btnAddSales = findViewById(R.id.btn_add_sales);
        Button btnSearchSales = findViewById(R.id.btn_search_sales);
        Button btnSearchCommission = findViewById(R.id.btn_search_commission);
        Button btnMainPage = findViewById(R.id.btn_main);

        btnAddEmployee.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Add_employee.class);
            startActivity(intent);
        });

        btnAddSales.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddSaleActivity.class);
            startActivity(intent);
        });

        btnSearchSales.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchSalesActivity.class);
            startActivity(intent);
        });

        btnSearchCommission.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchCommissionActivity.class);
            startActivity(intent);
        });

        btnMainPage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
