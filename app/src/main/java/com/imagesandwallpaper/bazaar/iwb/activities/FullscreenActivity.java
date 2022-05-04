package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FullscreenActivity extends AppCompatActivity implements ImageItemClickInterface {
    ActivityFullscreenBinding binding;
    Dialog loadImageDialog, setImageDialog;
    ImageView backIcon, favIcon, downloadIcon, shareIcon;
    ViewPager fullImageViewPager;
    String imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        backIcon = binding.backIcon;
        favIcon = binding.favoriteIcon;
        downloadIcon = binding.downloadIcon;
        shareIcon = binding.shareIcon;
        fullImageViewPager = binding.fullScreenItemViewPager;
        backIcon.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.setBtn.setOnClickListener(view -> {
            loadImageDialog();
        });
        ArrayList<ImageItemModel> myList = (ArrayList<ImageItemModel>) getIntent().getSerializableExtra("myList");
        Log.d("myList",myList.toString());
    }



    public void loadImageDialog() {
        loadImageDialog = new Dialog(FullscreenActivity.this);
        loadImageDialog.setContentView(R.layout.load_image_dialog);
        loadImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadImageDialog.setCancelable(false);
        loadImageDialog.show();

        ImageView cancelBtn = loadImageDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            loadImageDialog.dismiss();
            setImageDialog();
        });

    }

    public void setImageDialog() {
        setImageDialog = new Dialog(FullscreenActivity.this);
        setImageDialog.setContentView(R.layout.set_image_dialog);
        setImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setImageDialog.setCancelable(false);
        setImageDialog.show();

        ImageView cancelBtn = setImageDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            setImageDialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {
    }
}