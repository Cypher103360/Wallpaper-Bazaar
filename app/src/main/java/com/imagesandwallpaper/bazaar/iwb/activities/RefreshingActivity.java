package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.imagesandwallpaper.bazaar.iwb.R;

public class RefreshingActivity extends AppCompatActivity {

    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refreshing);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        });

    }
}