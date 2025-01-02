package com.maha.homework_bmp601;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    EditText edtName, edtNumber, edtPhoto, edtArea;
    Button btnAdd, btnUpdate, btnDelete, btnViewAll;
    ListView listView;

    DatabaseHelper databaseHelper;
    DelegateAdapter adapter;
    ArrayList<Delegate> delegateList;

    int selectedDelegateId = -1; // To track the selected delegate for update or delete.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtNumber = findViewById(R.id.edtNumber);
        edtPhoto = findViewById(R.id.edtPhoto);
        edtArea = findViewById(R.id.edtArea);

        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewAll = findViewById(R.id.btnViewAll);

        listView = findViewById(R.id.listView);

        databaseHelper = new DatabaseHelper(this);
        delegateList = new ArrayList<>();

        // Add a new delegate
        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String number = edtNumber.getText().toString();
            String photo = edtPhoto.getText().toString();
            String area = edtArea.getText().toString();

            if (name.isEmpty() || number.isEmpty() || photo.isEmpty() || area.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = databaseHelper.insertDelegate(name, number, photo, area);
            if (result) {
                Toast.makeText(this, "Delegate added successfully", Toast.LENGTH_SHORT).show();
                clearFields();
                loadDelegates();
            } else {
                Toast.makeText(this, "Failed to add delegate", Toast.LENGTH_SHORT).show();
            }
        });

        // Update a delegate
        btnUpdate.setOnClickListener(v -> {
            if (selectedDelegateId == -1) {
                Toast.makeText(this, "Select a delegate to update", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = edtName.getText().toString();
            String number = edtNumber.getText().toString();
            String photo = edtPhoto.getText().toString();
            String area = edtArea.getText().toString();

            if (name.isEmpty() || number.isEmpty() || photo.isEmpty() || area.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = databaseHelper.updateDelegate(selectedDelegateId, name, number, photo, area);
            if (result) {
                Toast.makeText(this, "Delegate updated successfully", Toast.LENGTH_SHORT).show();
                clearFields();
                loadDelegates();
            } else {
                Toast.makeText(this, "Failed to update delegate", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete a delegate
        btnDelete.setOnClickListener(v -> {
            if (selectedDelegateId == -1) {
                Toast.makeText(this, "Select a delegate to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = databaseHelper.deleteDelegate(selectedDelegateId);
            if (result) {
                Toast.makeText(this, "Delegate deleted successfully", Toast.LENGTH_SHORT).show();
                clearFields();
                loadDelegates();
            } else {
                Toast.makeText(this, "Failed to delete delegate", Toast.LENGTH_SHORT).show();
            }
        });

        // View all delegates
        btnViewAll.setOnClickListener(v -> loadDelegates());
    }

    // Load delegates into the list
    private void loadDelegates() {
        delegateList.clear();
        Cursor cursor = databaseHelper.getAllDelegates();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No delegates found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            delegateList.add(new Delegate(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
            ));
        }

        adapter = new DelegateAdapter(this, delegateList, this::selectDelegate);
        listView.setAdapter(adapter);
    }

    // Select a delegate
    private void selectDelegate(Delegate delegate) {
        selectedDelegateId = delegate.getId();
        edtName.setText(delegate.getName());
        edtNumber.setText(delegate.getPhone());
        edtPhoto.setText(delegate.getPhoto());
        edtArea.setText(delegate.getRegion());
    }

    // Clear input fields
    private void clearFields() {
        edtName.setText("");
        edtNumber.setText("");
        edtPhoto.setText("");
        edtArea.setText("");
        selectedDelegateId = -1;
    }
}