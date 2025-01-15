package com.example.ite_bmp601_hw_s24;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Add_employee extends AppCompatActivity {
    EditText editRollno, editName, editDate;
    Spinner spinnerRegion;
    ImageView imgPhoto;
    Button btnAdd, btnDelete, btnModify, btnView, btnViewAll,btnChoosePhoto;
    SQLiteDatabase db;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private byte[] imageBytes;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);


        // Initialize the ActivityResultLauncher


        editRollno = findViewById(R.id.ed_Rollno);
        editName = findViewById(R.id.ed_Name);
        editDate = findViewById(R.id.ed_Date);
        spinnerRegion = findViewById(R.id.spinnerRegion);
        imgPhoto = findViewById(R.id.imgPhoto);

        btnAdd = findViewById(R.id.btn_Add);
        btnModify = findViewById(R.id.btn_Modify);
        btnDelete = findViewById(R.id.btn_Delete);
        btnView = findViewById(R.id.btnView);
        btnViewAll = findViewById(R.id.btnViewAll);

        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);

        //  (Spinner)
        String[] regions = {"North", "South", "West", "East", "Lebanon"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, regions);
        spinnerRegion.setAdapter(adapter);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageBytes = compressImage(bitmap); // Compress the image
                            imgPhoto.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        db = openOrCreateDatabase("EmployeeDB", Context.MODE_PRIVATE, null);
        //db.execSQL("DROP TABLE IF EXISTS employees");
        db.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                "Rollno NVARCHAR PRIMARY KEY, " +
                "Name NVARCHAR UNIQUE, " +
                "Region NVARCHAR, " +
                "JoinDate NVARCHAR, " +
                "Photo BLOB)");


        btnAdd.setOnClickListener(this::onAddClick);
        btnDelete.setOnClickListener(this::onDeleteClick);
        btnModify.setOnClickListener(this::onModifyClick);
        btnView.setOnClickListener(this::onViewClick);
        btnViewAll.setOnClickListener(this::onViewAllClick);

        editDate.setOnClickListener(this::onDateClick);
        btnChoosePhoto.setOnClickListener(view -> pickImage());


        imgPhoto.setImageResource(R.drawable.ic_default_photo);
    }

    private byte[] compressImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); // Adjust quality (0-100)
        return stream.toByteArray();
    }
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }


    // الدالة الخاصة بإضافة موظف
    private void onAddClick(View view) {
        if (editRollno.getText().toString().trim().isEmpty() ||
                editName.getText().toString().trim().isEmpty() ||
                editDate.getText().toString().trim().isEmpty()) {
            showMessage("Error", "Please fill all fields.");
            return;
        }

        String rollno = editRollno.getText().toString().trim();
        String name = editName.getText().toString().trim();
        String region = spinnerRegion.getSelectedItem().toString();
        String joinDate = editDate.getText().toString().trim();

        if (imageBytes == null || imageBytes.length == 0) {
            showMessage("Error", "Please select an image.");
            return;
        }

        try {
            db.execSQL("INSERT INTO employees (Rollno, Name, Region, JoinDate, Photo) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{rollno, name, region, joinDate, imageBytes});
            showMessage("Success", "Employee added successfully!");
            clearFields();
        } catch (Exception e) {
            showMessage("Error", "Failed to add employee: " + e.getMessage());
        }
    }

    private void onDeleteClick(View view) {
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            showMessage("Error", "Please enter the Roll Number to delete.");
            return;
        }

        db.execSQL("DELETE FROM employees WHERE Rollno = ?", new Object[]{rollno});
        showMessage("Success", "Employee deleted successfully!");
        clearFields();
    }

    private void onModifyClick(View view) {
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            showMessage("Error", "Please enter the Roll Number to modify.");
            return;
        }

        String name = editName.getText().toString().trim();
        String region = spinnerRegion.getSelectedItem().toString();
        String joinDate = editDate.getText().toString().trim();

        db.execSQL("UPDATE employees SET Name = ?, Region = ?, JoinDate = ? WHERE Rollno = ?",
                new Object[]{name, region, joinDate, rollno});
        showMessage("Success", "Employee modified successfully!");
        clearFields();
    }

    private void onViewClick(View view) {
        Log.d("DEBUG", "onViewClick called");
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            Log.d("DEBUG", "Roll number is empty");
            showMessage("Error", "Please enter the Roll Number to view.");
            return;
        }

        Log.d("DEBUG", "Querying database for roll number: " + rollno);
        Cursor cursor = db.rawQuery("SELECT * FROM employees WHERE Rollno = ?", new String[]{rollno});
        if (cursor.moveToFirst()) {
            Log.d("DEBUG", "Employee found");
            String name = cursor.getString(1);
            String region = cursor.getString(2);
            String joinDate = cursor.getString(3);
            byte[] photo = cursor.getBlob(4);

            // Debug log to check if the photo byte array is retrieved
            Log.d("DEBUG", "Photo byte array length: " + (photo != null ? photo.length : "null"));

            showMessage("Employee Details",
                    "Roll Number: " + rollno +
                            "\nName: " + name +
                            "\nRegion: " + region +
                            "\nJoin Date: " + joinDate);

            if (photo != null && photo.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                if (bitmap != null) {
                    Log.d("DEBUG", "Bitmap decoded successfully");
                    imgPhoto.setImageBitmap(bitmap);
                } else {
                    Log.d("DEBUG", "Failed to decode bitmap from byte array");
                    imgPhoto.setImageResource(R.drawable.ic_default_photo);
                }
            } else {
                Log.d("DEBUG", "Photo is null or empty");
                imgPhoto.setImageResource(R.drawable.ic_default_photo);
            }
        } else {
            Log.d("DEBUG", "No employee found");
            showMessage("Error", "No employee found with Roll Number: " + rollno);
        }
        cursor.close();
    }

    private void onViewAllClick(View view) {
        Cursor cursor = db.rawQuery("SELECT * FROM employees", null);
        if (cursor.getCount() == 0) {
            showMessage("Error", "No employees found.");
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            String rollno = cursor.getString(0);
            String name = cursor.getString(1);
            String region = cursor.getString(2);
            String joinDate = cursor.getString(3);

            builder.append("Roll Number: ").append(rollno)
                    .append("\nName: ").append(name)
                    .append("\nRegion: ").append(region)
                    .append("\nJoin Date: ").append(joinDate)
                    .append("\n\n");
        }
        cursor.close();
        showMessage("All Employees", builder.toString());
    }

    private void onDateClick(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (datePicker1, y, m, d) -> {
            editDate.setText(y + "-" + (m + 1) + "-" + d);
        }, year, month, day);
        datePicker.show();
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.show();
    }

    // دالة تنظيف الحقول
    private void clearFields() {
        editRollno.setText("");
        editName.setText("");
        editDate.setText("");
        imgPhoto.setImageResource(R.drawable.ic_default_photo);
    }
}
