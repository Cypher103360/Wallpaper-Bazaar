package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRefreshingBinding;

public class RefreshingActivity extends AppCompatActivity {

    ActivityRefreshingBinding binding;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        startBtn = findViewById(R.id.startBtn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startBtn.setVisibility(View.VISIBLE);
            }
        },500);

        startBtn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        });

    }
}