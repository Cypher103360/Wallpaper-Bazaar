package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.PopAndPremiumActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UpdateAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UserDataActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityMainBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.UploadLiveWallpaperLayoutBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();
    ImageView chooseImage, categoryImage;
    Bitmap bitmap;
    String encodedImage, liveWallImg, checkImage;
    Dialog loadingDialog, imageDialog, catDialog, bannerImgDialog, liveWallDialog;
    ApiInterface apiInterface;
    String id, image2, proWallUrl, proWallId, fileShareId, fileShareUrl, getWallId, getWallUrl;
    EditText choseImgQuality;
    UploadLiveWallpaperLayoutBinding uploadLiveWallpaperLayoutBinding;
    Intent intent;
    Uri uri;

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
                        Toast.makeText(this, "Image quality is " + imgQuality, Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        fetchGetWallpaperapp();
        fetchProWallUrl();
        fetchProFileShareUrl();

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
            intent.putExtra("type", "popular");
            startActivity(intent);
        });
        binding.premiumBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PopAndPremiumActivity.class);
            intent.putExtra("type", "premium");
            startActivity(intent);
        });
        binding.liveBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PopAndPremiumActivity.class);
            intent.putExtra("type", "live");
            startActivity(intent);
        });
        binding.adsIdBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UpdateAdsActivity.class);
            intent.putExtra("key", "wall");
            startActivity(intent);
        });

        binding.turboAdsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UpdateAdsActivity.class);
            intent.putExtra("key", "turbo");
            startActivity(intent);
        });

        binding.userDataBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UserDataActivity.class);
            startActivity(intent);
        });

        binding.proWallUrlBtn.setOnClickListener(view -> {
            proWallUrlDialog();

        });
        binding.proFileShareUrlBtn.setOnClickListener(view -> {
            proFileShareUrlDialog();


        });
        binding.getFreeWallBtn.setOnClickListener(view -> {
            getFreeWallpaperDialog();

        });

        binding.uploadLiveWallpaper.setOnClickListener(view -> uploadLiveWallpaperDialog("live_wallpaper"));

    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private void uploadLiveWallpaperDialog(String id) {
        liveWallDialog = new Dialog(this);
        uploadLiveWallpaperLayoutBinding = UploadLiveWallpaperLayoutBinding.inflate(getLayoutInflater());
        liveWallDialog.setContentView(uploadLiveWallpaperLayoutBinding.getRoot());
        liveWallDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        liveWallDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        liveWallDialog.setCancelable(false);
        liveWallDialog.show();


        TextView dialogTitle = uploadLiveWallpaperLayoutBinding.dialogTitle;
        choseImgQuality = uploadLiveWallpaperLayoutBinding.imgQuality;
        chooseImage = uploadLiveWallpaperLayoutBinding.chooseImageView;
        Button cancelBtn = uploadLiveWallpaperLayoutBinding.cancelBtn;
        Button uploadImageBtn = uploadLiveWallpaperLayoutBinding.uploadImageBtn;

        dialogTitle.setText(id);
        cancelBtn.setOnClickListener(view -> liveWallDialog.dismiss());

        choseImgQuality.setVisibility(View.GONE);
        chooseImage.setOnClickListener(view -> {
            requestPermission();
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        });

        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            if (liveWallImg == null) {
                Toast.makeText(MainActivity.this, "Please select a gif file for live wallpaper", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            } else {
                if (FilenameUtils.getExtension(liveWallImg).equals("gif")) {
                    File liveWallFile = new File(Uri.parse(liveWallImg).getPath());
                    RequestBody liveWallRequestBody =
                            RequestBody.create(MediaType.parse("multipart/form-data"), liveWallFile);

                    MultipartBody.Part liveWallPart =
                            MultipartBody.Part.createFormData("img", liveWallFile.getName(), liveWallRequestBody);

                    MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", id);

                    Call<ResponseBody> call = apiInterface.uploadLiveWallpaper(liveWallPart, idPart);
                    UploadLiveWallpaper(call);
                } else {
                    Toast.makeText(this, "Please select a gif file for live wallpaper", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();

                }

            }
        });


    }

    private void UploadLiveWallpaper(Call<ResponseBody> call) {
        loadingDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    liveWallDialog.dismiss();

                    Toast.makeText(MainActivity.this, "Data Upload Successfully", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            liveWallImg = FileUtils.getPath(this, uri);
            Glide.with(this).asGif().load(uri).into(uploadLiveWallpaperLayoutBinding.chooseImageView);
        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            encodedImage = FileUtils.getPath(this, uri);
            Glide.with(this).load(uri).into(categoryImage);
        }
    }


    private void updateBannerImage() {
        String[] items = new String[]{"Home", "Premium", "Pro Home","Pro Premium"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Update Banner")
                .setCancelable(true)
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            updateBanner("home_banner");
                            break;
                        case 1:
                            updateBanner("premium_banner");
                            break;
                        case 2:
                            updateBanner("pro_home");
                            break;
                        case 3:
                            updateBanner("pro_premium");
                            break;
                        default:
                    }
                }).show();
    }

    private void updateBanner(String key) {
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
            } else if (Integer.parseInt(quality) >= 10) {
                requestPermission();
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 102);
                //launcher.launch("image/*");
            } else {
                choseImgQuality.setError("Minimum Quality must be 10.");
            }

        });
        map.put("tableName", key);
        Call<BannerModelList> call = apiInterface.fetchBanner(map);
        call.enqueue(new Callback<BannerModelList>() {
            @Override
            public void onResponse(@NonNull Call<BannerModelList> call, @NonNull Response<BannerModelList> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {

                        for (BannerModel ban : response.body().getData()) {

                            switch (FilenameUtils.getExtension(ban.getImage())) {
                                case "jpeg":
                                case "jpg":
                                case "png":
                                    Glide.with(MainActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                                            + ban.getImage()).into(categoryImage);
                                    break;
                                case "gif":
                                    Glide.with(MainActivity.this).asGif().load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                                            + ban.getImage()).into(categoryImage);
                                    break;
                            }

                            //Glide.with(MainActivity.this).asGif().load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + ban.getImage()).into(categoryImage);
                            urlEdt.setText(ban.getUrl());
                            id = ban.getId();
                            image2 = ban.getImage();
                            checkImage = ban.getImage();
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
            Log.d("checkEncodedImg", encodedImage);

            String url = urlEdt.getText().toString().trim();

            if (encodedImage.equals(checkImage)) {

//                map.put("id", id);
//                map.put("img", encodedImage);
//                map.put("url", url);
//                map.put("deleteImg", image2);
//                map.put("imgKey", "0");

                MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", encodedImage);
                MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", id);
                MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", url);
                MultipartBody.Part deleteImgPart = MultipartBody.Part.createFormData("deleteImg", image2);
                MultipartBody.Part imgKeyPart = MultipartBody.Part.createFormData("imgKey", "0");
                MultipartBody.Part tablePart = MultipartBody.Part.createFormData("tableName", key);

                Call<MessageModel> call1 = apiInterface.updateBanner(imgPart, idPart, urlPart, deleteImgPart, imgKeyPart, tablePart);

                updateBannerData(call1);
            } else {
                File imgFile = new File(Uri.parse(encodedImage).getPath());
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);

                MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", imgFile.getName(), requestBody);

                MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", id);
                MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", url);
                MultipartBody.Part deleteImgPart = MultipartBody.Part.createFormData("deleteImg", image2);
                MultipartBody.Part imgKeyPart = MultipartBody.Part.createFormData("imgKey", "1");
                MultipartBody.Part tablePart = MultipartBody.Part.createFormData("tableName", key);


                Call<MessageModel> call1 = apiInterface.updateBanner(imgPart, idPart, urlPart, deleteImgPart, imgKeyPart, tablePart);
                updateBannerData(call1);

//                map.put("id", id);
//                map.put("img", encodedImage);
//                map.put("url", url);
//                map.put("deleteImg", image2);
//                map.put("imgKey", "1");

            }
        });


    }

    private void updateBannerData(Call<MessageModel> call) {
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    bannerImgDialog.dismiss();
                } else {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
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
            } else if (Integer.parseInt(quality) >= 10) {

                launcher.launch("image/*");
            } else {
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
            } else if (Integer.parseInt(quality) >= 10) {

                launcher.launch("image/*");
            } else {
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
            String proUrl = proUrlEditText.getText().toString().trim();

            if (TextUtils.isEmpty(proUrl)) {
                proUrlEditText.setError("Url Required");
                proUrlEditText.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("id", proWallId);
                map.put("url", proUrl);
                updateProWallUrl(map);
            }
        });


    }

    private void getFreeWallpaperDialog() {
        imageDialog = new Dialog(MainActivity.this);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        EditText proUrlEditText = imageDialog.findViewById(R.id.img_quality);
        TextInputLayout textInputLayout = imageDialog.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("Get Wallpaper App");
        proUrlEditText.setHint("Enter Url");
        proUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText("Get Wallpaper App");
        proUrlEditText.setText(getWallUrl);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setVisibility(View.GONE);

        uploadImageBtn.setText("Upload Url");
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String proUrl = proUrlEditText.getText().toString().trim();

            if (TextUtils.isEmpty(proUrl)) {
                proUrlEditText.setError("Url Required");
                proUrlEditText.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("id", getWallId);
                map.put("url", proUrl);
                updateProWallUrl(map);
            }
        });


    }

    private void proFileShareUrlDialog() {
        imageDialog = new Dialog(MainActivity.this);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        EditText proUrlEditText = imageDialog.findViewById(R.id.img_quality);
        TextInputLayout textInputLayout = imageDialog.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("File Transfer Url");
        proUrlEditText.setHint("Enter Pro Wallpaper Url");
        proUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText("Update File Transfer Url");
        proUrlEditText.setText(fileShareUrl);
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setVisibility(View.GONE);

        uploadImageBtn.setText("Upload Url");
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String proUrl = proUrlEditText.getText().toString().trim();

            if (TextUtils.isEmpty(proUrl)) {
                proUrlEditText.setError("Url Required");
                proUrlEditText.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("id", fileShareId);
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
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchProWallUrl();
                    fetchProFileShareUrl();
                    fetchGetWallpaperapp();
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
//                Log.d("ggggggggg", t.getMessage());
            }
        });
    }

    private void fetchGetWallpaperapp() {
        Call<ProWallModelList> call = apiInterface.fetchGetWallpaperMessage();
        call.enqueue(new Callback<ProWallModelList>() {
            @Override
            public void onResponse(@NonNull Call<ProWallModelList> call, @NonNull Response<ProWallModelList> response) {

                assert response.body() != null;
                for (ProWallModel proWallModel : response.body().getData()) {
                    getWallId = proWallModel.getId();
                    getWallUrl = proWallModel.getUrl();
                    Log.d("ggggggggg", getWallUrl);

                }

            }

            @Override
            public void onFailure(@NonNull Call<ProWallModelList> call, @NonNull Throwable t) {
//                Log.d("ggggggggg", t.getMessage());
            }
        });
    }

    private void fetchProFileShareUrl() {
        Call<ProWallModelList> call = apiInterface.fetchFileTransferUrl();
        call.enqueue(new Callback<ProWallModelList>() {
            @Override
            public void onResponse(@NonNull Call<ProWallModelList> call, @NonNull Response<ProWallModelList> response) {

                assert response.body() != null;
                for (ProWallModel proWallModel : response.body().getData()) {
                    fileShareId = proWallModel.getId();
                    fileShareUrl = proWallModel.getUrl();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ProWallModelList> call, @NonNull Throwable t) {
                Log.d("ggggggggg", t.getMessage());
            }
        });
    }
}