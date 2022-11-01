package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.FileUtils;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.OwnAdsAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityShowOwnAdsBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.UploadAdsDialogBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.AdsStateModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.OwnAdsModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.OwnAdsModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.OwnAdsModelView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowOwnAdsActivity extends AppCompatActivity implements OwnAdsAdapter.OwnAdsListener {

    static String bannerImage, nativeImage, interstitialImage;
    ApiInterface apiInterface;
    List<OwnAdsModel> ownAdsModels;
    OwnAdsModelView ownAdsModelView;
    ActivityShowOwnAdsBinding binding;
    Dialog loadingDialog, adsDialog;
    OwnAdsAdapter ownAdsAdapter;
    String bannerImageTemp, nativeImageTemp, interstitialImageTemp;
    Intent intent;
    UploadAdsDialogBinding uploadAdsDialogBinding;
    Uri uri;

    Map<String, String> map = new HashMap<>();
    Call<MessageModel> call;
    boolean checkSwitch = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowOwnAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.ownAdsRV.setLayoutManager(layoutManager);
        ownAdsAdapter = new OwnAdsAdapter(this, this);
        ownAdsModels = new ArrayList<>();
        binding.ownAdsRV.setAdapter(ownAdsAdapter);
        apiInterface = ApiWebServices.getApiInterface();
        //****Loading Dialog****/
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        loadingDialog.setCancelable(false);
        ownAdsModelView = new ViewModelProvider(this, new OwnAdsModelFactory(this.getApplication(), "Turbo Share19")).get(OwnAdsModelView.class);

        //**Loading Dialog****/
        fetchOwnAds();
        checkSwitch();


    }

    private void checkSwitch() {
        Call<AdsStateModel> stateModelCall = apiInterface.fetchAdsState();
        stateModelCall.enqueue(new Callback<AdsStateModel>() {
            @Override
            public void onResponse(@NonNull Call<AdsStateModel> call, @NonNull Response<AdsStateModel> response) {
                Log.d("ContentValue", Objects.requireNonNull(response.body()).getAds_turn_on_or_off());
                checkSwitch = Boolean.parseBoolean(response.body().getAds_turn_on_or_off());
                if (checkSwitch) {
                    binding.switchButton.setText("Ads ON");
                } else {
                    binding.switchButton.setText(R.string.ads_off);
                }
                binding.switchButton.setChecked(checkSwitch);

            }

            @Override
            public void onFailure(@NonNull Call<AdsStateModel> call, @NonNull Throwable t) {
                Log.d("ContentValueError", t.getMessage());

            }
        });

        binding.switchButton.setOnClickListener(view -> {
            checkSwitch = binding.switchButton.isChecked();
            if (checkSwitch) {
                binding.switchButton.setText("Ads ON");
            } else
                binding.switchButton.setText(R.string.ads_off);

            Call<MessageModel> modelCall = apiInterface.uploadAdsState(String.valueOf(binding.switchButton.isChecked()));
            modelCall.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                    Log.d("ContentValue", Objects.requireNonNull(response.body()).getMessage());
                }

                @Override
                public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                    Log.d("ContentValueError", t.getMessage());
                }
            });
        });

    }

    private void fetchOwnAds() {
        loadingDialog.show();
        ownAdsModels.clear();
        Call<List<OwnAdsModel>> call = apiInterface.fetchOwnAds("Turbo Share19");

        call.enqueue(new Callback<List<OwnAdsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<OwnAdsModel>> call, @NonNull Response<List<OwnAdsModel>> response) {
                if (response.isSuccessful()) {
                    ownAdsModels.addAll(Objects.requireNonNull(response.body()));
                    ownAdsAdapter.updateOwnAdsList(ownAdsModels);
                }
                loadingDialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<List<OwnAdsModel>> call, @NonNull Throwable t) {
                Log.d("onResponse error", t.getMessage());
                loadingDialog.dismiss();

            }
        });


    }

    @Override
    public void ownAdsClicked(OwnAdsModel ownAdsModel, String ban) {

        String[] adsName = new String[]{"Update Ads", " Delete Ads"};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Upload Own Ads").setCancelable(true)
                .setItems(adsName, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            updateOwnAdsDialog(ownAdsModel, ban);
                            break;
                        case 1:
                            map.put("id", ownAdsModel.getId());
                            map.put("appId", ownAdsModel.getAppId());
                            map.put("bannerImg", ownAdsModel.getBannerImg());
                            map.put("nativeImg", ownAdsModel.getNativeImg());
                            map.put("interImg", ownAdsModel.getInterstitialImg());
                            deleteOwnAds(map);
                            break;


                    }
                });

        builder.show();
    }


    private void deleteOwnAds(Map<String, String> mapDelete) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Delete Ads")
                .setMessage("Do you really want to delete this Ad?")
                .setNegativeButton("No", (dialog, which) -> {

                }).setPositiveButton("Yes", (dialog, which) -> {
                    loadingDialog.show();
                    Call<MessageModel> call = apiInterface.deleteAd(mapDelete);
                    call.enqueue(new Callback<MessageModel>() {
                        @Override
                        public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                            if (response.isSuccessful()) {
                                fetchOwnAds();
                                Toast.makeText(ShowOwnAdsActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                            loadingDialog.dismiss();

                        }
                    });

                }).show();
    }

    private void updateOwnAdsDialog(OwnAdsModel ownAdsModel, String ban) {

        adsDialog = new Dialog(this);
        uploadAdsDialogBinding = UploadAdsDialogBinding.inflate(getLayoutInflater());
        adsDialog.setContentView(uploadAdsDialogBinding.getRoot());
        adsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        adsDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(ShowOwnAdsActivity.this, R.drawable.item_bg));
        adsDialog.setCancelable(false);
        adsDialog.show();
        uploadAdsDialogBinding.imageCancel.setOnClickListener(v -> adsDialog.dismiss());

        if (ban.equals("ban")) {
            uploadAdsDialogBinding.bannerUrl.setText(ownAdsModel.getBannerUrl());
            uploadAdsDialogBinding.chooseNativeImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.nativeUrl.setVisibility(View.GONE);
            uploadAdsDialogBinding.chooseInterstitialImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.interstitialIdUrl.setVisibility(View.GONE);
            bannerImage = ownAdsModel.getBannerImg();
            bannerImageTemp = ownAdsModel.getBannerImg();
            switch (Objects.requireNonNull(FilenameUtils.getExtension(ownAdsModel.getBannerImg()))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getBannerImg()).into(uploadAdsDialogBinding.chooseBannerImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getBannerImg()).into(uploadAdsDialogBinding.chooseBannerImage);
                    break;
            }

        } else if (ban.equals("nat")) {
            uploadAdsDialogBinding.nativeUrl.setText(ownAdsModel.getNativeUrl());
            uploadAdsDialogBinding.chooseBannerImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.bannerUrl.setVisibility(View.GONE);
            uploadAdsDialogBinding.chooseInterstitialImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.interstitialIdUrl.setVisibility(View.GONE);
            nativeImage = ownAdsModel.getNativeImg();
            nativeImageTemp = ownAdsModel.getNativeImg();
            switch (Objects.requireNonNull(FilenameUtils.getExtension(ownAdsModel.getNativeImg()))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getNativeImg()).into(uploadAdsDialogBinding.chooseNativeImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getNativeImg()).into(uploadAdsDialogBinding.chooseNativeImage);
                    break;
            }

        } else if (ban.equals("inter")) {
            uploadAdsDialogBinding.chooseNativeImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.chooseBannerImage.setVisibility(View.GONE);
            uploadAdsDialogBinding.bannerUrl.setVisibility(View.GONE);
            uploadAdsDialogBinding.nativeUrl.setVisibility(View.GONE);
            interstitialImage = ownAdsModel.getInterstitialImg();
            interstitialImageTemp = ownAdsModel.getInterstitialImg();
            uploadAdsDialogBinding.interstitialIdUrl.setText(ownAdsModel.getInterstitialUrl());
            switch (Objects.requireNonNull(FilenameUtils.getExtension(ownAdsModel.getInterstitialImg()))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getInterstitialImg()).into(uploadAdsDialogBinding.chooseInterstitialImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModel.getInterstitialImg()).into(uploadAdsDialogBinding.chooseInterstitialImage);
                    break;
            }

        }


        uploadAdsDialogBinding.chooseBannerImage.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        });
        uploadAdsDialogBinding.chooseNativeImage.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 102);
        });
        uploadAdsDialogBinding.chooseInterstitialImage.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 103);
        });

        uploadAdsDialogBinding.uploadAdsBtn.setOnClickListener(v -> {

            switch (ban) {
                case "ban":
                    String bannerUrl = uploadAdsDialogBinding.bannerUrl.getText().toString().trim();
                    if (TextUtils.isEmpty(bannerUrl)) {
                        uploadAdsDialogBinding.bannerUrl.setError("Url required!");
                        uploadAdsDialogBinding.bannerUrl.requestFocus();
                    } else {
                        if (!Objects.equals(bannerImageTemp, bannerImage)) {
                            File bannerFile = new File(Uri.parse(bannerImage).getPath());
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bannerFile);
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", bannerFile.getName(), requestBody);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", bannerImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", bannerUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "0");
                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);


                        } else {
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", bannerImage);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", bannerImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", bannerUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "1");
                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);

                        }
                        updateOwnAds(call);
                    }

                    break;
                case "nat":
                    String nativeUrl = uploadAdsDialogBinding.nativeUrl.getText().toString().trim();
                    if (TextUtils.isEmpty(nativeUrl)) {
                        uploadAdsDialogBinding.nativeUrl.setError("Url required!");
                        uploadAdsDialogBinding.nativeUrl.requestFocus();
                    } else {
                        if (!Objects.equals(nativeImageTemp, nativeImage)) {
                            File imgFile = new File(Uri.parse(nativeImage).getPath());
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", imgFile.getName(), requestBody);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", nativeImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", nativeUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "0");
                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);


                        } else {
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", nativeImage);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", nativeImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", nativeUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "1");
                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);


                        }
                        updateOwnAds(call);

                    }

                    break;
                case "inter":
                    String interstitialUrl = uploadAdsDialogBinding.interstitialIdUrl.getText().toString().trim();
                    if (TextUtils.isEmpty(interstitialUrl)) {
                        uploadAdsDialogBinding.interstitialIdUrl.setError("Url required!");
                        uploadAdsDialogBinding.interstitialIdUrl.requestFocus();
                    } else {
                        if (!Objects.equals(interstitialImageTemp, interstitialImage)) {
                            File imgFile = new File(Uri.parse(interstitialImage).getPath());
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", imgFile.getName(), requestBody);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", interstitialImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", interstitialUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "0");

                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);


                        } else {
                            MultipartBody.Part imgPart = MultipartBody.Part.createFormData("img", interstitialImage);
                            MultipartBody.Part tempImgPart = MultipartBody.Part.createFormData("tempImg", interstitialImageTemp);
                            MultipartBody.Part urlPart = MultipartBody.Part.createFormData("url", interstitialUrl);
                            MultipartBody.Part idPart = MultipartBody.Part.createFormData("id", ownAdsModel.getId());
                            MultipartBody.Part keyPart = MultipartBody.Part.createFormData("key", "1");

                            MultipartBody.Part adPart = MultipartBody.Part.createFormData("adKey", ban);
                            call = apiInterface.updateOwnAds(imgPart, tempImgPart, urlPart, idPart, keyPart, adPart);

                        }
                        updateOwnAds(call);

                    }

                    break;
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            bannerImage = FileUtils.getPath(this, uri);
            switch (Objects.requireNonNull(FilenameUtils.getExtension(bannerImage))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseBannerImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseBannerImage);
                    break;
            }
        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            nativeImage = FileUtils.getPath(this, uri);
            switch (Objects.requireNonNull(FilenameUtils.getExtension(nativeImage))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseNativeImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseNativeImage);
                    break;

            }

        } else if (requestCode == 103 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            interstitialImage = FileUtils.getPath(this, uri);
            switch (Objects.requireNonNull(FilenameUtils.getExtension(interstitialImage))) {
                case "jpeg":
                case "jpg":
                case "png":
                    Glide.with(this).load(uri).into(uploadAdsDialogBinding.chooseInterstitialImage);
                    break;
                case "gif":
                    Glide.with(this).asGif().load(uri).into(uploadAdsDialogBinding.chooseInterstitialImage);
                    break;
            }
        }


    }

    private void updateOwnAds(Call<MessageModel> call) {
        loadingDialog.show();
        Log.d("ContentValue", bannerImage + "   " + bannerImageTemp + "\n" + nativeImage + "   " + nativeImageTemp + "\n" + interstitialImage + "   " + interstitialImageTemp);

        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    adsDialog.dismiss();
                    fetchOwnAds();

                    Log.d("ContentValue", Objects.requireNonNull(response.body()).getMessage());

                    Toast.makeText(ShowOwnAdsActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(ShowOwnAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();

            }
        });


    }

}