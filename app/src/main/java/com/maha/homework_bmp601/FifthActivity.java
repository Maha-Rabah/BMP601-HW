package com.maha.homework_bmp601;

import android.os.Bundle;
import android.widget.EditText;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.maha.homework_bmp601.Adapters.CommissionAdapter;
import com.maha.homework_bmp601.DataModel.Commission;

import java.util.ArrayList;

public class FifthActivity extends AppCompatActivity{
    EditText edtDelegateID;
    TextView textView;
    Spinner spinnerMonth, spinnerYear;
    Button btnSearchCommission;
    ListView listView;
    DatabaseHelper databaseHelper;
    ArrayList<Commission> commissionList;
    CommissionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        // Initialize views
        edtDelegateID = findViewById(R.id.edtDelegateID);
        textView = findViewById(R.id.textView);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        btnSearchCommission = findViewById(R.id.btnSearchCommission);
        listView = findViewById(R.id.listView);

        databaseHelper = new DatabaseHelper(this);
        commissionList = new ArrayList<>();

    }


}
