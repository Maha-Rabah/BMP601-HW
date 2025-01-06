package com.maha.homework_bmp601;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnGoToSecondActivity = findViewById(R.id.btnGoToSecondActivity);
        Button btnGoToThirdActivity = findViewById(R.id.btnGoToThirdActivity);
        Button btnGoToFourthActivity = findViewById(R.id.btnGoToFourthActivity);
        Button btnGoToFifthActivity = findViewById(R.id.btnGoToFifthActivity);

        // الانتقال إلى الأنشطة الأخرى
        btnGoToSecondActivity.setOnClickListener(v -> {
           Intent intent = new Intent(MainActivity.this, SecondActivity.class);
           startActivity(intent);
        });

        btnGoToThirdActivity.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
//            startActivity(intent);
        });

        btnGoToFourthActivity.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, FourthActivity.class);
//            startActivity(intent);
        });

        btnGoToFifthActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FifthActivity.class);
            startActivity(intent);
        });
    }
}



