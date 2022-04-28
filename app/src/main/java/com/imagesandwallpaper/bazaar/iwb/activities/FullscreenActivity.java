package com.imagesandwallpaper.bazaar.iwb.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FullscreenActivity extends AppCompatActivity {
    ActivityFullscreenBinding binding;
    Dialog loadImageDialog, setImageDialog;
    ImageView backIcon, favIcon, downloadIcon, shareIcon, fullImage;
    String imgUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        backIcon = binding.backIcon;
        favIcon = binding.favoriteIcon;
        downloadIcon = binding.downloadIcon;
        shareIcon = binding.shareIcon;
        fullImage = binding.fullImage;
        backIcon.setOnClickListener(view -> {
            onBackPressed();
        });

        imgUrl = getIntent().getStringExtra("img");
        Glide.with(FullscreenActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/popular_images/" + imgUrl).into(fullImage);

        downloadIcon.setOnClickListener(view -> {
//            if (checkPermissionForReadExternalStorage()) {
//                saveImage(imgUrl,view);
//            } else {
//                try {
//                    requestPermissionForReadExternalStorage();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        });
        binding.setBtn.setOnClickListener(view -> {
            loadImageDialog();
        });
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


    private void saveImage(String imgUrl, View view) {
        String dirPath = "/Wallpaper Images";
        String fileName = "images.jpg";
        File file = new File(dirPath, fileName);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(imgUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(file))
                .setMimeType(getMimeType(uri));
        downloadManager.enqueue(request);
        Snackbar.make(view, "Downloading Started", Snackbar.LENGTH_LONG).show();
    }

    private String getMimeType(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}