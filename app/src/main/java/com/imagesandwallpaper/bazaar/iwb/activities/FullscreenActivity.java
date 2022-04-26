package com.imagesandwallpaper.bazaar.iwb.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;

public class FullscreenActivity extends AppCompatActivity {
    ActivityFullscreenBinding binding;
    Dialog loadImageDialog,setImageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setBtn.setOnClickListener(view -> {
            loadImageDialog();
        });
    }

    public void loadImageDialog(){
        loadImageDialog = new Dialog(FullscreenActivity.this);
        loadImageDialog.setContentView(R.layout.load_image_dialog);
        loadImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadImageDialog.setCancelable(false);
        loadImageDialog.show();

        ImageView cancelBtn = loadImageDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            loadImageDialog.dismiss();
            setImageDialog();
        });

    }
    public void setImageDialog(){
        setImageDialog = new Dialog(FullscreenActivity.this);
        setImageDialog.setContentView(R.layout.set_image_dialog);
        setImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        setImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setImageDialog.setCancelable(false);
        setImageDialog.show();

        ImageView cancelBtn = setImageDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            setImageDialog.dismiss();
        });
    }
}