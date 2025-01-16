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
    //declaring variables
    EditText editRollno, editName, editDate;
    Spinner spinnerRegion;
    ImageView imgPhoto;
    Button btnAdd, btnDelete, btnModify, btnView, btnViewAll,btnChoosePhoto;
    SQLiteDatabase db;
    //declaring variables for image
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
        // Choosing Image using method to open the gallery and select an image then compress it into a byte array
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri); //open media gallery and select image
                            imageBytes = compressImage(bitmap); // Compress the image
                            imgPhoto.setImageBitmap(bitmap); //setting image to the imageView
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        // creating employee table
        db = openOrCreateDatabase("EmployeeDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS employees (" +
                "Rollno NVARCHAR PRIMARY KEY, " +
                "Name NVARCHAR UNIQUE, " +
                "Region NVARCHAR, " +
                "JoinDate NVARCHAR, " +
                "Photo BLOB)");
        //choose photo button start method to choose photo from database
        btnChoosePhoto.setOnClickListener(view -> pickImage());
        //add button start method to insert data into database
        btnAdd.setOnClickListener(this::onAddClick);
        //delete button start method to delete data from database
        btnDelete.setOnClickListener(this::onDeleteClick);
        //modify button start method to modify data from database
        btnModify.setOnClickListener(this::onModifyClick);
        //edit date button start method open date view to select date
        editDate.setOnClickListener(this::onDateClick);
        //view button start method to view data from database
        btnView.setOnClickListener(this::onViewClick);
        //view all button start method to view all data from database
        btnViewAll.setOnClickListener(this::onViewAllClick);
        //image view to default image
        imgPhoto.setImageResource(R.drawable.ic_default_photo);
    }
    //Method to compress image as cursor can not display image with high quality because of limit constraints
    private byte[] compressImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(); //bit array class to store image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); // Compress image to 50% of its actual size and quality
        return stream.toByteArray(); //return byte array of image
    }
    //Method to open gallery and select image
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    // الدالة الخاصة بإضافة موظف
    //Method for adding employee data
    private void onAddClick(View view) {
        //checking entry validity
        if (editRollno.getText().toString().trim().isEmpty() ||
                editName.getText().toString().trim().isEmpty() ||
                editDate.getText().toString().trim().isEmpty()) {
            showMessage("Error", "Please fill all fields.");
            return;
        }
        //extracting entered data and storing in variables
        String rollno = editRollno.getText().toString().trim();
        String name = editName.getText().toString().trim().toLowerCase(); //case sensitive for names
        String region = spinnerRegion.getSelectedItem().toString();
        String joinDate = editDate.getText().toString().trim();
        if (imageBytes == null || imageBytes.length == 0) {
            showMessage("Error", "Please select an image.");
            return;
        }
        //SQL statement to insert data into the database
        try {
            db.execSQL("INSERT INTO employees (Rollno, Name, Region, JoinDate, Photo) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{rollno, name, region, joinDate, imageBytes});
            showMessage("Success", "Employee added successfully!");
            clearFields();
        } catch (Exception e) {
            showMessage("Error", "Failed to add employee: " + e.getMessage());
        }
    }
    //method to delete employee data
    private void onDeleteClick(View view) {
        //extract the Rollno entered to delete
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            showMessage("Error", "Please enter the Roll Number to delete.");
            return;
        }
        //SQL statement to delete data from the database
        db.execSQL("DELETE FROM employees WHERE Rollno = ?", new Object[]{rollno});
        showMessage("Success", "Employee deleted successfully!");
        clearFields();
    }
    //method to modify employee data
    private void onModifyClick(View view) {
        //extract the Rollno entered to modify
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            showMessage("Error", "Please enter the Roll Number to modify.");
            return;
        }
        //extract the new data to modify and storing in variables
        String name = editName.getText().toString().trim().toLowerCase(); //case sensitive for names
        String region = spinnerRegion.getSelectedItem().toString();
        String joinDate = editDate.getText().toString().trim();
        if (imageBytes == null || imageBytes.length == 0) {
            showMessage("Error", "Please select an image.");
            return;
        }
        //SQL statement to modify data in the database
        try {
        db.execSQL("UPDATE employees SET Name = ?, Region = ?, JoinDate = ?, Photo = ? WHERE Rollno = ?",
                new Object[]{name, region, joinDate,imageBytes, rollno});
        showMessage("Success", "Employee modified successfully!");
        clearFields();
        } catch (Exception e) {
            showMessage("Error", "Failed to Modify employee: " + e.getMessage());
        }
    }
    //method to view employee data
    private void onViewClick(View view) {
        //extract the Rollno entered to view
        String rollno = editRollno.getText().toString().trim();
        if (rollno.isEmpty()) {
            showMessage("Error", "Please enter the Roll Number to view.");
            return;
        }
        //SQL statement to retrieve data from the database
        Cursor cursor = db.rawQuery("SELECT * FROM employees WHERE Rollno = ?", new String[]{rollno});
        if (cursor.moveToFirst()) {
            //extract the retrieved data and storing in variables
            String name = cursor.getString(1);
            String region = cursor.getString(2);
            String joinDate = cursor.getString(3);
            byte[] photo = cursor.getBlob(4);
            //display the retrieved data in message
            showMessage("Employee Details",
                    "Roll Number: " + rollno +
                            "\nName: " + name +
                            "\nRegion: " + region +
                            "\nJoin Date: " + joinDate);
            //display the retrieved image in imageView
            if (photo != null && photo.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);//convert byte array to bitmap
                if (bitmap != null) {
                    imgPhoto.setImageBitmap(bitmap);//setting image to the imageView
                } else {
                    imgPhoto.setImageResource(R.drawable.ic_default_photo);//setting default image to the imageView
                }
            } else {
                imgPhoto.setImageResource(R.drawable.ic_default_photo);//setting default image to the imageView
            }
        } else {
            showMessage("Error", "No employee found with Roll Number: " + rollno);//display error message if no employee found
        }
        cursor.close();
    }
    //method to view all employee data
    private void onViewAllClick(View view) {
        //SQL statement to retrieve all data from the database
        Cursor cursor = db.rawQuery("SELECT * FROM employees", null);
        if (cursor.getCount() == 0) {
            showMessage("Error", "No employees found.");
            return;
        }
        //display the retrieved data in message
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
    //method to open date view to select date
    private void onDateClick(View view) {
        //open date view to select date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //open date view to select date
        DatePickerDialog datePicker = new DatePickerDialog(this, (datePicker1, y, m, d) -> {
            editDate.setText(y + "-" + (m + 1) + "-" + d);
        }, year, month, day);
        datePicker.show();
    }
    //Method to show popup message
    private void showMessage(String title, String message) {
        //popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.show();
    }

    //Clear Method to clear fields
    private void clearFields() {
        editRollno.setText("");
        editName.setText("");
        editDate.setText("");
        imgPhoto.setImageResource(R.drawable.ic_default_photo);
    }
}
