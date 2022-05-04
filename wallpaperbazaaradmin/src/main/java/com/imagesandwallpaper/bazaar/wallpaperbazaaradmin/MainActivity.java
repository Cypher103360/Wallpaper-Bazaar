package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityMainBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();
    ImageView chooseImage, categoryImage;
    Bitmap bitmap;
    String encodedImage;
    Dialog loadingDialog, imageDialog, catDialog;
    ApiInterface apiInterface;

    public static String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = CommonMethods.loadingDialog(MainActivity.this);
        apiInterface = ApiWebServices.getApiInterface();
        // upload_popular_images.php

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                if (chooseImage != null) {
                    Glide.with(this).load(result).into(chooseImage);
                } else {
                    Glide.with(this).load(result).into(categoryImage);
                }
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(result);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = imageStore(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.popImgBtn.setOnClickListener(view -> {
            uploadImage("Popular_Images");
        });

        binding.newImgBtn.setOnClickListener(view -> {
            uploadImage("Premium_Images");
        });
        binding.categoryBtn.setOnClickListener(view -> {

            uploadCategory();
        });
        binding.showDataBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, ShowDataActivity.class));
        });
        binding.uploadBannerBtn.setOnClickListener(view -> {
            uploadBannerImage();
        });
    }

    private void uploadBannerImage() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Upload Banner")
                .setIcon(R.drawable.ic_baseline_add_alert_24)
                .setMessage("Upload banner in Home or Premium?")
                .setNeutralButton("Cancel", (dialogInterface, i) -> {

                }).setNegativeButton("Home", ((dialogInterface, i) -> {
                    map.put("tableName","home_banner");
                    uploadBanner(map);

        })).setPositiveButton("Premium", ((dialogInterface, i) -> {

        })).show();
    }

    private void uploadBanner(Map<String, String> map) {

    }


    private void uploadCategory() {
        catDialog = new Dialog(MainActivity.this);
        catDialog.setContentView(R.layout.category_dialog);
        catDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        catDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        catDialog.setCancelable(false);
        catDialog.show();

        categoryImage = catDialog.findViewById(R.id.choose_cat_imageView);
        EditText catTitle = catDialog.findViewById(R.id.cat_title);
        Button uploadCatBtn = catDialog.findViewById(R.id.upload_cat_btn);
        Button cancelBtn = catDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> catDialog.dismiss());
        categoryImage.setOnClickListener(view -> {
            launcher.launch("image/*");
        });

        uploadCatBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String cTitle = catTitle.getText().toString().trim();

            if (encodedImage == null) {
                loadingDialog.dismiss();
                Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(cTitle)) {
                catTitle.setError("Name Required");
                catTitle.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("img", encodedImage);
                map.put("title", cTitle);
                uploadCatData(map);
            }
        });
    }

    private void uploadCatData(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Category Uploaded", Toast.LENGTH_SHORT).show();
                    catDialog.dismiss();
                } else {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void uploadImage(String type) {
        imageDialog = new Dialog(MainActivity.this);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText(type);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setOnClickListener(view -> {
            launcher.launch("image/*");
        });
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            if (encodedImage == null) {
                Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            } else {
                map.put("tableName", type);
                map.put("img", encodedImage);
                uploadPopularImage(map);
            }
        });


    }

    private void uploadPopularImage(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadPopularImages(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageDialog.dismiss();
                } else {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }
}