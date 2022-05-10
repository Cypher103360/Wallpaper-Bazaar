package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.textfield.TextInputLayout;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.PopAndPremiumActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UpdateAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UserDataActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityMainBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModelList;
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
    Dialog loadingDialog, imageDialog, catDialog, bannerImgDialog;
    ApiInterface apiInterface;
    String id, image2,proWallUrl,proWallId;
    EditText choseImgQuality;

    public static String imageStore(Bitmap bitmap, int imageQuality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream);
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
                    if (choseImgQuality != null) {
                        String imgQuality = choseImgQuality.getText().toString();
                        InputStream inputStream = this.getContentResolver().openInputStream(result);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = imageStore(bitmap, Integer.parseInt(imgQuality));
                        Toast.makeText(this, "Image quality is "+ imgQuality, Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        fetchProWallUrl();

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
            updateBannerImage();
        });
        binding.popularBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PopAndPremiumActivity.class);
            intent.putExtra("type","popular");
            startActivity(intent);
        });
        binding.premiumBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PopAndPremiumActivity.class);
            intent.putExtra("type","premium");
            startActivity(intent);
        });
        binding.adsIdBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UpdateAdsActivity.class);
            startActivity(intent);
        });
        binding.userDataBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UserDataActivity.class);
            startActivity(intent);
        });

        binding.proWallUrlBtn.setOnClickListener(view -> {
            proWallUrlDialog();
        });
    }

    private void updateBannerImage() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Update Banner")
                .setIcon(R.drawable.ic_baseline_add_alert_24)
                .setMessage("Update banner in Home or Premium?")
                .setNeutralButton("Cancel", (dialogInterface, i) -> {

                }).setNegativeButton("Home", ((dialogInterface, i) -> {
            map.put("tableName", "home_banner");
            updateBanner(map);

        })).setPositiveButton("Premium", ((dialogInterface, i) -> {
            map.put("tableName", "premium_banner");
            updateBanner(map);

        })).show();
    }

    private void updateBanner(Map<String, String> map) {
        bannerImgDialog = new Dialog(this);
        bannerImgDialog.setContentView(R.layout.upload_banner_image_layout);
        bannerImgDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        bannerImgDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bannerImgDialog.setCancelable(false);
        bannerImgDialog.show();
        loadingDialog.show();

        categoryImage = bannerImgDialog.findViewById(R.id.choose_banner_imageView);
        EditText urlEdt = bannerImgDialog.findViewById(R.id.url);
        Button updateBanBtn = bannerImgDialog.findViewById(R.id.upload_banner_image_btn);
        Button cancelBtn = bannerImgDialog.findViewById(R.id.cancel_banner_btn);
        choseImgQuality = bannerImgDialog.findViewById(R.id.img_quality);

        cancelBtn.setOnClickListener(view -> {
            bannerImgDialog.dismiss();
            encodedImage = "";
        });
        categoryImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(quality)>=10){

                launcher.launch("image/*");
            }else{
                choseImgQuality.setError("Minimum Quality must be 10.");
            }

        });

        Call<BannerModelList> call = apiInterface.fetchBanner(map);
        call.enqueue(new Callback<BannerModelList>() {
            @Override
            public void onResponse(@NonNull Call<BannerModelList> call, @NonNull Response<BannerModelList> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {

                        for (BannerModel ban : response.body().getData()) {
                            Glide.with(MainActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + ban.getImage()).into(categoryImage);
                            urlEdt.setText(ban.getUrl());
                            id = ban.getId();
                            image2 = ban.getImage();
                            encodedImage = image2;
                            loadingDialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerModelList> call, @NonNull Throwable t) {

            }
        });

        updateBanBtn.setOnClickListener(view -> {
            loadingDialog.show();

            String url = urlEdt.getText().toString().trim();
            if (encodedImage.length() <= 100) {

                map.put("id", id);
                map.put("img", encodedImage);
                map.put("url", url);
                map.put("deleteImg", image2);
                map.put("imgKey", "0");

                updateBannerData(map);
            } else {

                map.put("id", id);
                map.put("img", encodedImage);
                map.put("url", url);
                map.put("deleteImg", image2);
                map.put("imgKey", "1");

                updateBannerData(map);

            }
        });


    }

    private void updateBannerData(Map<String, String> map) {

        Log.d("CheckMap", map.toString());
        Call<MessageModel> call = apiInterface.updateBanner(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Banner Uploaded", Toast.LENGTH_SHORT).show();
                    bannerImgDialog.dismiss();
                } else {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Check Error", t.getMessage());

                loadingDialog.dismiss();
            }
        });
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
        choseImgQuality = catDialog.findViewById(R.id.img_quality);

        Button cancelBtn = catDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> catDialog.dismiss());
        categoryImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(quality)>=10){

                launcher.launch("image/*");
            }else{
                choseImgQuality.setError("Minimum Quality must be 10.");
            }

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
        choseImgQuality = imageDialog.findViewById(R.id.img_quality);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText(type);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());

        chooseImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(quality)>=10){

                launcher.launch("image/*");
            }else{
                choseImgQuality.setError("Minimum Quality must be 10.");
            }
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

    private void proWallUrlDialog() {
        imageDialog = new Dialog(MainActivity.this);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        EditText proUrlEditText = imageDialog.findViewById(R.id.img_quality);
        TextInputLayout textInputLayout = imageDialog.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("Pro Wallpaper Url");
        proUrlEditText.setHint("Enter Pro Wallpaper Url");
        proUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText("Update Pro Wallpaper Url");
        proUrlEditText.setText(proWallUrl);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setVisibility(View.GONE);

        uploadImageBtn.setText("Upload Url");
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String proUrl =  proUrlEditText.getText().toString().trim();

            if (TextUtils.isEmpty(proUrl)) {
                proUrlEditText.setError("Url Required");
                proUrlEditText.requestFocus();
                loadingDialog.dismiss();
            }else {
                map.put("id",proWallId);
                map.put("url", proUrl);
                updateProWallUrl(map);
            }
        });


    }

    private void updateProWallUrl(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateProWallUrl(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchProWallUrl();
                    loadingDialog.dismiss();
                    imageDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void fetchProWallUrl() {
        Call<ProWallModelList> call = apiInterface.fetchProWallUrl();
        call.enqueue(new Callback<ProWallModelList>() {
            @Override
            public void onResponse(@NonNull Call<ProWallModelList> call, @NonNull Response<ProWallModelList> response) {

                assert response.body() != null;
                for (ProWallModel proWallModel : response.body().getData()) {
                    proWallId = proWallModel.getId();
                    proWallUrl = proWallModel.getUrl();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ProWallModelList> call, @NonNull Throwable t) {
                Log.d("ggggggggg", t.getMessage());
            }
        });
    }
}