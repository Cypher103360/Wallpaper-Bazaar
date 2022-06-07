package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityUpdateAdsBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads.AdsModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads.AdsModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAdsActivity extends AppCompatActivity {
    ActivityUpdateAdsBinding binding;
    String[] items = new String[]{"AdmobWithMeta", "IronSourceWithMeta", "AppLovinWithMeta", "Meta"};
    AutoCompleteTextView BannerTopNetworkName, BannerBottomNetworkName, InterstitialNetwork,
            NativeAdsNetworkName, OpenAdsNetworkName;
    EditText AppId, AppLovinSdkKey, BannerTop, BannerBottom, InterstitialAds, NativeAds, OpenAds;
    ApiInterface apiInterface;
    Button UploadAdsBtn;
    String key;
    Dialog loading;
    Map<String, String> map = new HashMap<>();
    String appId, appLovinSdkKey, bannerTopNetworkName, bannerTop, bannerBottomNetworkName,
            bannerBottom, interstitialNetwork, interstitialAds, nativeAdsNetworkName,
            nativeAds, openAds, openAdsNetworkName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        key = getIntent().getStringExtra("key");
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(UpdateAdsActivity.this);
        BannerTopNetworkName = binding.bannerTopNetworkName;
        BannerBottomNetworkName = binding.bannerBottomNetworkName;
        InterstitialNetwork = binding.interstitialNetwork;
        NativeAdsNetworkName = binding.nativeAdsNetworkName;
        OpenAdsNetworkName = binding.openAdsNetworkName;
        UploadAdsBtn = binding.uploadAdsBtn;

        AppId = binding.appId;
        AppLovinSdkKey = binding.appLovinSdkKey;
        BannerTop = binding.bannerTop;
        BannerBottom = binding.bannerBottom;
        InterstitialAds = binding.interstitialAds;
        NativeAds = binding.nativeAds;
        OpenAds = binding.openAds;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateAdsActivity.this,
                R.layout.dropdown_items, items);
        BannerTopNetworkName.setAdapter(arrayAdapter);
        BannerBottomNetworkName.setAdapter(arrayAdapter);
        InterstitialNetwork.setAdapter(arrayAdapter);
        NativeAdsNetworkName.setAdapter(arrayAdapter);
        OpenAdsNetworkName.setAdapter(arrayAdapter);
        switch (key) {
            case "wall":
                fetchAds("Wallpaper Bazaar");
                break;
            case "turbo":
                fetchAds("Turbo Share");
                break;
            case "HDWall":
                fetchAds("HD Wallpaper");
                break;
        }

        UploadAdsBtn.setOnClickListener(view -> {
            loading.show();

            appId = AppId.getText().toString().trim();
            appLovinSdkKey = AppLovinSdkKey.getText().toString().trim();
            bannerTopNetworkName = BannerTopNetworkName.getText().toString().trim();
            bannerTop = BannerTop.getText().toString().trim();
            bannerBottomNetworkName = BannerBottomNetworkName.getText().toString().trim();
            bannerBottom = BannerBottom.getText().toString().trim();
            interstitialNetwork = InterstitialNetwork.getText().toString().trim();
            interstitialAds = InterstitialAds.getText().toString().trim();
            nativeAdsNetworkName = NativeAdsNetworkName.getText().toString().trim();
            nativeAds = NativeAds.getText().toString().trim();
            openAds = OpenAds.getText().toString().trim();
            openAdsNetworkName = OpenAdsNetworkName.getText().toString();


            if (TextUtils.isEmpty(appId)) {
                AppId.setError("App id is required");
                AppId.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(appLovinSdkKey)) {
                AppLovinSdkKey.setError("AppLovinSdkKey is required");
                AppLovinSdkKey.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerTopNetworkName)) {
                BannerTopNetworkName.setError("BannerTopNetworkName is required");
                BannerTopNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerTop)) {
                BannerTop.setError("BannerTop is required");
                BannerTop.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerBottomNetworkName)) {
                BannerBottomNetworkName.setError("BannerBottomNetworkName is required");
                BannerBottomNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(bannerBottom)) {
                BannerBottom.setError("BannerBottom is required");
                BannerBottom.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(interstitialNetwork)) {
                InterstitialNetwork.setError("InterstitialNetwork is required");
                InterstitialNetwork.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(interstitialAds)) {
                InterstitialAds.setError("InterstitialAds is required");
                InterstitialAds.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(nativeAdsNetworkName)) {
                NativeAdsNetworkName.setError("NativeAdsNetworkName is required");
                NativeAdsNetworkName.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(nativeAds)) {
                NativeAds.setError("NativeAds is required");
                NativeAds.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(openAds)) {
                OpenAds.setError("OpenAds is required");
                OpenAds.requestFocus();
                loading.dismiss();
            } else if (TextUtils.isEmpty(openAdsNetworkName)) {
                OpenAdsNetworkName.setError("OpenAdsNetworkName is required");
                OpenAdsNetworkName.requestFocus();
                loading.dismiss();
            } else {
                switch (key) {
                    case "wall":
                        map.put("id", "Wallpaper Bazaar");
                        break;
                    case "turbo":
                        map.put("id", "Turbo Share");
                        break;
                    case "HDWall":
                        map.put("id", "HD Wallpaper");
                        break;
                }
                map.put("appId", appId);
                map.put("appLovinSdkKey", appLovinSdkKey);
                map.put("bannerTop", bannerTop);
                map.put("bannerTopNetworkName", bannerTopNetworkName);
                map.put("bannerBottom", bannerBottom);
                map.put("bannerBottomNetworkName", bannerBottomNetworkName);
                map.put("interstitialAds", interstitialAds);
                map.put("interstitialNetwork", interstitialNetwork);
                map.put("nativeAds", nativeAds);
                map.put("nativeAdsNetworkName", nativeAdsNetworkName);
                map.put("openAdsNetworkName", openAdsNetworkName);
                map.put("openAds", openAds);
                updateAdIds(map);
            }

        });

    }

    private void fetchAds(String id) {
        loading.show();
        apiInterface = ApiWebServices.getApiInterface();
        Call<AdsModelList> call = apiInterface.fetchAds(id);
        call.enqueue(new Callback<AdsModelList>() {
            @Override
            public void onResponse(@NonNull Call<AdsModelList> call, @NonNull Response<AdsModelList> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getData() != null) {
                        for (AdsModel ads : response.body().getData()) {
                            AppId.setText(ads.getAppId());
                            AppLovinSdkKey.setText(ads.getAppLovinSdkKey());
                            BannerTopNetworkName.setText(ads.getBannerTopNetworkName());
                            BannerTop.setText(ads.getBannerTop());
                            BannerBottomNetworkName.setText(ads.getBannerBottomNetworkName());
                            BannerBottom.setText(ads.getBannerBottom());
                            InterstitialNetwork.setText(ads.getInterstitialNetwork());
                            InterstitialAds.setText(ads.getInterstitialAds());
                            NativeAdsNetworkName.setText(ads.getNativeAdsNetworkName());
                            NativeAds.setText(ads.getNativeAds());
                            OpenAds.setText(ads.getOpenAds());
                            OpenAdsNetworkName.setText(ads.getOpenAdsNetworkName());

                        }
                        loading.dismiss();
                    }
                } else {
                    Log.e("adsError", response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<AdsModelList> call, @NonNull Throwable t) {
                Log.d("adsError", t.getMessage());
                loading.dismiss();
            }
        });
    }

    private void updateAdIds(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateAdsId(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(UpdateAdsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    fetchAds(map.get("id"));
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(UpdateAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }
        });
    }
}