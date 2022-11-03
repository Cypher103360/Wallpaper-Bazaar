package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import static android.content.ContentValues.TAG;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.FileShareAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.FileShareEditActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.PopAndPremiumActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.ShowFeaturedActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.ShowOwnAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UpdateAdsActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities.UserDataActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityMainBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.NewsCardLayoutBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.UploadAdsDialogBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.UploadBannerImageLayoutBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.UploadLiveWallpaperLayoutBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.BannerModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ProWallModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UrlModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    int bannerClick, interstitialClick, nativeClick;
    ActivityMainBinding binding;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();
    ImageView chooseImage, categoryImage,selectImage;
    Bitmap bitmap;
    String encodedImage, liveWallImg, checkImage;
    String bannerImage, nativeImage, interstitialImage;
    Dialog loadingDialog, sliderDialog, imageDialog, catDialog, bannerImgDialog, liveWallDialog,
            textAndUrlsDialog, fileShareDetailsDialog, adsDialog;
    ApiInterface apiInterface;
    String id, image2, proWallUrl, proWallId, getWallId, getWallUrl, textAndUrls, textAndUrlsId;
    String fileShareUrl, fileShareId, key, fsTitletxt, fsURLTxt, fsDescTxt;
    EditText choseImgQuality;
    UploadLiveWallpaperLayoutBinding uploadLiveWallpaperLayoutBinding;
    UploadBannerImageLayoutBinding uploadBannerImageLayoutBinding;
    Intent intent;
    Call<MessageModel> call;
    List<BannerModel> bannerModels = new ArrayList<>();
    List<SlideModel> slideModels = new ArrayList<>();
    Uri uri,bannerUri,nativeUri,interstitialUri;
    NewsCardLayoutBinding cardLayoutBinding;
    UploadAdsDialogBinding uploadAdsDialogBinding;

    public MainActivity() {
    }

    public static String imageStore(Bitmap bitmap, int imageQuality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream);

        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static String decodeEmoji(String message) {
        try {
            return URLDecoder.decode(
                    message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }

    public static String encodeEmoji(String message) {
        try {
            return URLEncoder.encode(message,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = CommonMethods.loadingDialog(MainActivity.this);
        apiInterface = ApiWebServices.getApiInterface();
        requestPermission();
        // upload_popular_images.php

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                if (chooseImage != null) {
                    Glide.with(this).load(result).into(chooseImage);
                } else if (categoryImage != null) {
                    Glide.with(this).load(result).into(categoryImage);
                } else if(selectImage!=null) {
                    Glide.with(this).load(result).into(selectImage);
                }else if (bannerClick == 103) {
                    bannerClick=0;
                    Toast.makeText(this, "103", Toast.LENGTH_SHORT).show();
                    bannerUri = result;
                    Log.d("imageViewUri", bannerUri.toString());
                    bannerImage = FileUtils.getPath(this, bannerUri);
                    switch (Objects.requireNonNull(FilenameUtils.getExtension(bannerImage))) {
                        case "jpeg":
                        case "jpg":
                        case "png":
                            Glide.with(this).load(bannerUri).into(uploadAdsDialogBinding.chooseBannerImage);
                            break;
                        case "gif":
                            Glide.with(this).asGif().load(bannerUri).into(uploadAdsDialogBinding.chooseBannerImage);
                            break;
                    }
                }
                else if (nativeClick == 104) {
                    nativeClick=0;
                    Toast.makeText(this, "104", Toast.LENGTH_SHORT).show();
                    nativeUri = result;
                    nativeImage = FileUtils.getPath(this, nativeUri);
                    switch (Objects.requireNonNull(FilenameUtils.getExtension(nativeImage))) {
                        case "jpeg":
                        case "jpg":
                        case "png":
                            Glide.with(this).load(nativeUri).into(uploadAdsDialogBinding.chooseNativeImage);
                            break;
                        case "gif":
                            Glide.with(this).asGif().load(nativeUri).into(uploadAdsDialogBinding.chooseNativeImage);
                            break;
                    }

                }
                else if (interstitialClick == 105) {
                    interstitialClick=0;
                    Toast.makeText(this, "105", Toast.LENGTH_SHORT).show();
                    interstitialUri = result;
                    interstitialImage = FileUtils.getPath(this, interstitialUri);
                    switch (Objects.requireNonNull(FilenameUtils.getExtension(interstitialImage))) {
                        case "jpeg":
                        case "jpg":
                        case "png":
                            Glide.with(this).load(interstitialUri).into(uploadAdsDialogBinding.chooseInterstitialImage);
                            break;
                        case "gif":
                            Glide.with(this).asGif().load(interstitialUri).into(uploadAdsDialogBinding.chooseInterstitialImage);
                            break;
                    }
                }
                try {
                    if (choseImgQuality != null) {
                        String imgQuality = choseImgQuality.getText().toString();
                        InputStream inputStream = this.getContentResolver().openInputStream(result);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = imageStore(bitmap, Integer.parseInt(imgQuality));
                        Toast.makeText(this, "Image quality is " + imgQuality, Toast.LENGTH_SHORT).show();
                    } else if (Objects.equals(key, "news")) {

                        InputStream inputStream = this.getContentResolver().openInputStream(result);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = imageStore(bitmap, 80);
                    } else {
                        InputStream inputStream = this.getContentResolver().openInputStream(result);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = imageStore(bitmap, 80);
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
            uploadCategory("category");
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
            intent.putExtra("key", "Wallpaper Bazaar");
            startActivity(intent);
        });

        binding.hdWallpaperAds.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UpdateAdsActivity.class);
            intent.putExtra("key", "HD Wallpaper");
            startActivity(intent);
        });

        binding.turboAdsBtn.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, UpdateAdsActivity.class);
//            intent.putExtra("key", "turbo");
//            startActivity(intent);

            Intent intent = new Intent(MainActivity.this, FileShareAdsActivity.class);
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

        binding.uploadFeaturedItems.setOnClickListener(v -> {
            uploadCategory("featured");
        });
        binding.showFeaturedItems.setOnClickListener(v -> {
            startActivity(new Intent(this, ShowFeaturedActivity.class));
        });

        binding.updateTextUrls.setOnClickListener(v -> {
            showUrlAndTextDialog();
        });

        binding.FSNewsReviewsDetails.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.MyTheme);
            String[] fsItemsList = new String[]{
                    "Upload News Detail",
                    "Upload Reviews Detail"
            };
            builder.setTitle("Choose your option").setCancelable(true).setItems(fsItemsList, (dialog, which) -> {
                switch (which) {
                    case 0:
                        showDetailsDialog("News Details");
                        break;
                    case 1:
                        showDetailsDialog("Review Details");
                        break;
                    default:
                }
            }).show();
        });

        binding.showFSNewsReviewsDetails.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.MyTheme);
            String[] fsItemsList = new String[]{
                    "Show News Detail",
                    "Show Reviews Detail"
            };
            builder.setTitle("Choose your option").setCancelable(true).setItems(fsItemsList, (dialog, which) -> {
                Intent intent = new Intent(MainActivity.this, FileShareEditActivity.class);
                switch (which) {
                    case 0:
                        intent.putExtra("key", "News");
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("key", "Reviews");
                        startActivity(intent);
                        break;
                    default:
                }
            }).show();
        });

        binding.uploadOwnAds.setOnClickListener(v -> UploadOwnAdsDialog());

        binding.showOwnAds.setOnClickListener(v -> startActivity(new Intent(this, ShowOwnAdsActivity.class)));
    }

    private void UploadOwnAdsDialog() {
        adsDialog = new Dialog(this);
        uploadAdsDialogBinding = UploadAdsDialogBinding.inflate(getLayoutInflater());
        adsDialog.setContentView(uploadAdsDialogBinding.getRoot());
        adsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        adsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.item_bg));
        adsDialog.setCancelable(false);
        adsDialog.show();
        uploadAdsDialogBinding.imageCancel.setOnClickListener(v -> adsDialog.dismiss());

        uploadAdsDialogBinding.chooseBannerImage.setOnClickListener(v -> {
            bannerClick = 103;
            launcher.launch("image/*");
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 103);
        });
        uploadAdsDialogBinding.chooseNativeImage.setOnClickListener(v -> {
            nativeClick = 104;
            launcher.launch("image/*");
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 104);
        });
        uploadAdsDialogBinding.chooseInterstitialImage.setOnClickListener(v -> {
            interstitialClick = 105;
            launcher.launch("image/*");
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 105);
        });

        uploadAdsDialogBinding.uploadAdsBtn.setOnClickListener(v -> {
            String bannerUrl = uploadAdsDialogBinding.bannerUrl.getText().toString().trim();
            String nativeUrl = uploadAdsDialogBinding.nativeUrl.getText().toString().trim();
            String interstitialUrl = uploadAdsDialogBinding.interstitialIdUrl.getText().toString().trim();

            if (bannerImage == null) {
                Toast.makeText(this, "Please select Banner Image", Toast.LENGTH_LONG).show();
            } else if (nativeImage == null) {
                Toast.makeText(this, "Please select Native Image", Toast.LENGTH_LONG).show();
            } else if (interstitialImage == null) {
                Toast.makeText(this, "Please select Interstitial Image", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(bannerUrl)) {
                uploadAdsDialogBinding.bannerUrl.setError("Url required!");
                uploadAdsDialogBinding.bannerUrl.requestFocus();
            } else if (TextUtils.isEmpty(nativeUrl)) {
                uploadAdsDialogBinding.nativeUrl.setError("Url required!");
                uploadAdsDialogBinding.nativeUrl.requestFocus();
            } else if (TextUtils.isEmpty(interstitialUrl)) {
                uploadAdsDialogBinding.interstitialIdUrl.setError("Url required!");
                uploadAdsDialogBinding.interstitialIdUrl.requestFocus();
            } else {

                File bannerFile = new File(Uri.parse(bannerImage).getPath());
                File nativeFile = new File(Uri.parse(nativeImage).getPath());
                File interstitialFile = new File(Uri.parse(interstitialImage).getPath());
                RequestBody bannerRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bannerFile);
                RequestBody nativeRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), nativeFile);
                RequestBody interstitialRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), interstitialFile);
                MultipartBody.Part bannerPart = MultipartBody.Part.createFormData("banImg", bannerFile.getName(), bannerRequestBody);
                MultipartBody.Part nativePart = MultipartBody.Part.createFormData("nativeImg", nativeFile.getName(), nativeRequestBody);
                MultipartBody.Part interstitialPart = MultipartBody.Part.createFormData("interImg", interstitialFile.getName(), interstitialRequestBody);
                MultipartBody.Part banUrlPart = MultipartBody.Part.createFormData("banUrl", bannerUrl);
                MultipartBody.Part nativeUrlPart = MultipartBody.Part.createFormData("nativeUrl", nativeUrl);
                MultipartBody.Part interstitialUrlPart = MultipartBody.Part.createFormData("interstitialUrl", interstitialUrl);
                MultipartBody.Part appIdPart = MultipartBody.Part.createFormData("appId", "Turbo Share19");

//                map.put("banUrl", bannerUrl);
//                map.put("nativeUrl", nativeUrl);
//                map.put("interstitialUrl", interstitialUrl);
//                map.put("appId", "PM Kisan All Yojana");
                Call<ResponseBody> call = apiInterface.uploadOwnAds(bannerPart, nativePart, interstitialPart, banUrlPart, nativeUrlPart, interstitialUrlPart, appIdPart);
                UploadOwnAds(call);
            }
        });


    }

    private void UploadOwnAds(Call<ResponseBody> call) {
        loadingDialog.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    adsDialog.dismiss();

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

    private void showDetailsDialog(String dialogName) {

        fileShareDetailsDialog = new Dialog(this);
        cardLayoutBinding = NewsCardLayoutBinding.inflate(getLayoutInflater());
        fileShareDetailsDialog.setContentView(cardLayoutBinding.getRoot());
        fileShareDetailsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fileShareDetailsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.item_bg));
        fileShareDetailsDialog.setCancelable(false);
        fileShareDetailsDialog.show();
        selectImage=cardLayoutBinding.selectImage;

        selectImage.setOnClickListener(v -> {
            launcher.launch("image/*");
            key = "news";
        });
        cardLayoutBinding.backBtn.setOnClickListener(view -> fileShareDetailsDialog.cancel());

        cardLayoutBinding.title.setText(dialogName);

        cardLayoutBinding.okBtn.setOnClickListener(view -> {

            fsTitletxt = cardLayoutBinding.titleEdt.getText().toString();
            fsURLTxt = cardLayoutBinding.urlEdt.getText().toString();
            fsDescTxt = cardLayoutBinding.descEdt.getText().toString();

            if (encodedImage == null) {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();

            } else if (TextUtils.isEmpty(fsTitletxt)) {
                cardLayoutBinding.titleEdt.setError("Title required!");

            } else if (TextUtils.isEmpty(fsURLTxt)) {
                cardLayoutBinding.urlEdt.setError("Title required!");

            } else if (TextUtils.isEmpty(fsDescTxt)) {
                cardLayoutBinding.descEdt.setError("Field required!");
            } else {
                loadingDialog.show();
                map.put("img", encodedImage);
                map.put("title", fsTitletxt);
                map.put("url", fsURLTxt);
                map.put("desc", fsDescTxt);
                uploadData(map, dialogName);
            }
        });


    }

    private void uploadData(Map<String, String> map, String key) {
        switch (key) {
            case "News Details":
                call = apiInterface.uploadNewsDetails(map);
                break;
            case "Review Details":
                call = apiInterface.uploadReviewDetails(map);
                break;
            default:
        }
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    loadingDialog.dismiss();
                    fileShareDetailsDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void showUrlAndTextDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this, R.style.MyTheme);
        String[] textUrlItems = new String[]{
                "Update Share Text",
                "Update Whatsapp Text",
                "Website Url", "memes Url", "success quotes Url"};
        builder.setTitle("Update your texts and Urls").setCancelable(true).setItems(textUrlItems, (dialogInterface, which) -> {
            switch (which) {
                case 0:
                    fetchTextAndUrls("share");
                    break;

                case 1:
                    fetchTextAndUrls("whatsapp");
                    break;

                case 2:
                    fetchTextAndUrls("website");
                    break;
                case 3:
                    fetchTextAndUrls("memes");
                    break;
                case 4:
                    fetchTextAndUrls("web");
                    break;
                default:
            }
        });

        builder.show();
    }

    private void fetchTextAndUrls(String key) {
        textAndUrlsDialog = new Dialog(this);
        textAndUrlsDialog.setContentView(R.layout.text_and_urls_layout);
        textAndUrlsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textAndUrlsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        textAndUrlsDialog.setCancelable(false);
        textAndUrlsDialog.show();

        EditText textAndUrlsEdt = textAndUrlsDialog.findViewById(R.id.text_and_urls_edt);
        Button uploadButton = textAndUrlsDialog.findViewById(R.id.upload_text_urls);
        Button cancelBtn = textAndUrlsDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(v -> {
            textAndUrlsDialog.dismiss();
        });

        Call<UrlModel> call = apiInterface.getUrls(key);
        call.enqueue(new Callback<UrlModel>() {
            @Override
            public void onResponse(@NonNull Call<UrlModel> call, @NonNull Response<UrlModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    textAndUrlsId = response.body().getId();
                    textAndUrls = decodeEmoji(response.body().getUrl());
                    textAndUrlsEdt.setText(textAndUrls);
                    Log.d("urlsText", textAndUrlsId + " " + textAndUrls);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UrlModel> call, @NonNull Throwable t) {

            }
        });

        uploadButton.setOnClickListener(v -> {
            loadingDialog.show();
            String url = textAndUrlsEdt.getText().toString().trim();

            if (TextUtils.isEmpty(url)) {
                textAndUrlsEdt.setError("Url Required");
                textAndUrlsEdt.requestFocus();
                loadingDialog.dismiss();
            } else {
                map.put("id", textAndUrlsId);
                map.put("url", encodeEmoji(url));

                updateTextAndUrls(map);

            }
        });
    }

    private void updateTextAndUrls(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateUrls(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    textAndUrlsDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, Objects.requireNonNull(response.body()).getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Log.d("responseError", t.getMessage());
                loadingDialog.dismiss();
            }
        });
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
            Glide.with(this).load(uri).into(uploadBannerImageLayoutBinding.chooseBannerImageView);
        }
//        else if (requestCode == 103 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            Log.d("imageViewUri", uri.toString());
//            bannerImage = FileUtils.getPath(this, uri);
//            switch (Objects.requireNonNull(FilenameUtils.getExtension(bannerImage))) {
//                case "jpeg":
//                case "jpg":
//                case "png":
//                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseBannerImage);
//                    break;
//                case "gif":
//                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseBannerImage);
//                    break;
//            }
//        } else if (requestCode == 104 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            nativeImage = FileUtils.getPath(this, uri);
//            switch (Objects.requireNonNull(FilenameUtils.getExtension(nativeImage))) {
//                case "jpeg":
//                case "jpg":
//                case "png":
//                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseNativeImage);
//                    break;
//                case "gif":
//                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseNativeImage);
//                    break;
//            }
//
//        } else if (requestCode == 105 && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            interstitialImage = FileUtils.getPath(this, uri);
//            switch (Objects.requireNonNull(FilenameUtils.getExtension(interstitialImage))) {
//                case "jpeg":
//                case "jpg":
//                case "png":
//                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseInterstitialImage);
//                    break;
//                case "gif":
//                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseInterstitialImage);
//                    break;
//            }
//        }
    }


    private void updateBannerImage() {
        String[] items = new String[]{"Home", "Premium", "Pro Home", "Pro Premium", "Upload FileShare Banners"
                , "Show File Share Banners"};
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
                        case 4:
                            updateBanner("fileShare_banners");
                            break;
                        case 5:
                            showFileShareBanners();
                            break;
                        default:
                    }
                }).show();
    }

    private void showFileShareBanners() {
        sliderDialog = new Dialog(MainActivity.this);
        sliderDialog.setContentView(R.layout.banner_slider_layout);
        sliderDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        sliderDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sliderDialog.setCancelable(true);
        sliderDialog.show();

        ImageSlider slider;

        slider = sliderDialog.findViewById(R.id.image_slider);


        Call<List<BannerModel>> call = apiInterface.fetchBanners();

        call.enqueue(new Callback<List<BannerModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<BannerModel>> call, @NonNull Response<List<BannerModel>> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    bannerModels.clear();
                    bannerModels.addAll(response.body());

                    for (BannerModel b : bannerModels) {
                        Log.e("myBanner", b.getImage() + "===" + b.getId() + "===" + b.getUrl());
                        slideModels.add(new SlideModel("https://gedgetsworld.in/Wallpaper_Bazaar/banner_images/"
                                + b.getImage(), ScaleTypes.FIT));
                    }
                    slider.setImageList(slideModels);

                    slider.setItemClickListener(i -> {
                        Toast.makeText(MainActivity.this, bannerModels.get(i).getImage(), Toast.LENGTH_SHORT).show();
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                        builder.setTitle("Delete Banner");
                        builder.setMessage("Do you want to delete this?");
                        builder.setNegativeButton("CANCEL", (dialog, which) -> {

                        }).setPositiveButton("DELETE", (dialog, which) -> {
                            loadingDialog.show();
                            map.put("id", bannerModels.get(i).getId());
                            map.put("title", "shareFiles");
                            map.put("path", "banner_images/" + bannerModels.get(i).getImage());
                            deleteBanner(map);
                        }).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<BannerModel>> call, @NonNull Throwable t) {
                Log.e("onErrorResponse", t.getMessage());
            }
        });

    }

    private void deleteBanner(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.deleteCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    sliderDialog.dismiss();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    private void updateBanner(String key) {
        bannerImgDialog = new Dialog(this);
        uploadBannerImageLayoutBinding = UploadBannerImageLayoutBinding.inflate(getLayoutInflater());
        bannerImgDialog.setContentView(uploadBannerImageLayoutBinding.getRoot());
        bannerImgDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        bannerImgDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bannerImgDialog.setCancelable(false);
        bannerImgDialog.show();
        if (!key.equals("fileShare_banners")) {
            loadingDialog.show();
        }

        categoryImage = uploadBannerImageLayoutBinding.chooseBannerImageView;
        EditText urlEdt = uploadBannerImageLayoutBinding.url;
        Button updateBanBtn = uploadBannerImageLayoutBinding.uploadBannerImageBtn;
        Button cancelBtn = uploadBannerImageLayoutBinding.cancelBannerBtn;

        choseImgQuality = uploadBannerImageLayoutBinding.imgQuality;

        cancelBtn.setOnClickListener(view -> {
            bannerImgDialog.dismiss();
            encodedImage = "";
        });

        if (!key.equals("fileShare_banners")) {

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
        } else {
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
        }
        if (!key.equals("fileShare_banners")) {
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
        }

        updateBanBtn.setOnClickListener(view -> {
            loadingDialog.show();
            Log.d("checkEncodedImg", encodedImage);
            String url = urlEdt.getText().toString().trim();

            if (!key.equals("fileShare_banners")) {
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
            } else {
                if (encodedImage == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(url)) {
                    urlEdt.setError("Url Required");
                    urlEdt.requestFocus();
                    loadingDialog.dismiss();
                } else {

                    map.put("img", encodedImage);
                    map.put("url", url);
                    call = apiInterface.uploadFileShareBanners(map);
                    updateBannerData(call);

                }
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

    private void uploadCategory(String key) {
        catDialog = new Dialog(MainActivity.this);
        catDialog.setContentView(R.layout.category_dialog);
        catDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        catDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        catDialog.setCancelable(false);
        catDialog.show();

        categoryImage = catDialog.findViewById(R.id.choose_cat_imageView);
        EditText catTitle = catDialog.findViewById(R.id.cat_title);
        TextInputLayout textInputLayout = catDialog.findViewById(R.id.textInputLayout);
        if (key.equals("featured")) {
            textInputLayout.setHint("Enter Url");
        } else {
            textInputLayout.setHint("Enter Title");
        }
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
                if (key.equals("featured")) {
                    catTitle.setError("Url Required");
                } else {
                    catTitle.setError("Name Required");
                }
                catTitle.requestFocus();
                loadingDialog.dismiss();
            } else {
                if (key.equals("featured")) {
                    map.put("img", encodedImage);
                    map.put("url", cTitle);
                    map.put("tableName", "Featured_Images");
                    uploadFeatured(map);
                } else {
                    map.put("img", encodedImage);
                    map.put("title", cTitle);
                    uploadCatData(map);
                }
            }
        });
    }

    private void uploadFeatured(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadFeatured(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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

    private void uploadCatData(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
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