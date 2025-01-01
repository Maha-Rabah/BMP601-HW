package com.maha.homework_bmp601;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
public class SecondActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    EditText edtName, edtPhone, edtRegion;
    Button btnAdd, btnViewAll;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // ربط العناصر من واجهة المستخدم إلى الكود
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtRegion = findViewById(R.id.edtRegion);
        btnAdd = findViewById(R.id.btnAdd);
        btnViewAll = findViewById(R.id.btnViewAll);
        listView = findViewById(R.id.listView);

        databaseHelper = new DatabaseHelper(this);

        // زر الإضافة
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                String region = edtRegion.getText().toString();

                if (name.isEmpty() || phone.isEmpty() || region.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = databaseHelper.insertEmployee(name, phone, region);

                if (isInserted) {
                    Toast.makeText(SecondActivity.this, "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SecondActivity.this, "حدثت مشكلة أثناء الإضافة", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // زر عرض جميع الموظفين
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Employee> employees = databaseHelper.getAllEmployees();

                if (employees.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "لا توجد بيانات", Toast.LENGTH_SHORT).show();
                } else {
                    EmployeeAdapter adapter = new EmployeeAdapter(SecondActivity.this, employees);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
}