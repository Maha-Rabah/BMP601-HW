package com.maha.homework_bmp601;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maha.homework_bmp601.Adapters.DelegateAdapter;
import com.maha.homework_bmp601.DataModel.Delegate;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    EditText edtName, edtNumber, edtPhoto;
    Spinner spinnerArea;
    Button btnAdd, btnUpdate, btnDelete, btnViewAll;
    ListView listView;

    DatabaseHelper databaseHelper;
    DelegateAdapter adapter;
    ArrayList<Delegate> delegateList;

    int selectedDelegateId = -1; // To track the selected delegate for update or delete.
    String selectedArea = ""; // To track the selected area from Spinner.
    int selectedRegionId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Initialize views
        edtName = findViewById(R.id.edtName);
        edtNumber = findViewById(R.id.edtNumber);
        edtPhoto = findViewById(R.id.edtPhoto);
        spinnerArea = findViewById(R.id.spinnerArea);

        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewAll = findViewById(R.id.btnViewAll);

        listView = findViewById(R.id.listView);

        databaseHelper = new DatabaseHelper(this);
        delegateList = new ArrayList<>();

        // Set up the Spinner with area options

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this,
                R.array.area_options, // Array defined in strings.xml
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapterSpinner);

        // Handle Spinner selection
        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedArea = parent.getItemAtPosition(position).toString();
                {
                    // تعيين ID المنطقة بناءً على الاختيار
                    selectedRegionId = position + 1; // ID يبدأ من 1
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                selectedArea = ""; // Reset if nothing is selected
                selectedRegionId = -1; //
            }
        });

        // Add a new delegate
        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String number = edtNumber.getText().toString();
            String photo = edtPhoto.getText().toString();

            if (name.isEmpty() || number.isEmpty() || photo.isEmpty() || selectedArea.isEmpty() || selectedRegionId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = databaseHelper.insertDelegate(name, number, photo, selectedRegionId);
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

            if (name.isEmpty() || number.isEmpty() || photo.isEmpty() || selectedArea.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = databaseHelper.updateDelegate(selectedDelegateId, name, number, photo, selectedRegionId);
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

        // Set the spinner to the selected area
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerArea.getAdapter();
        int position = adapter.getPosition(delegate.getRegion());
        spinnerArea.setSelection(position);
    }

    // Clear input fields
    private void clearFields() {
        edtName.setText("");
        edtNumber.setText("");
        edtPhoto.setText("");
        spinnerArea.setSelection(0);
        selectedDelegateId = -1;
    }
}