package com.imagesandwallpaper.bazaar.iwb.utils;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.imagesandwallpaper.bazaar.iwb.activities.FavoriteActivity;
import com.imagesandwallpaper.bazaar.iwb.activities.HomeActivity;
import com.imagesandwallpaper.bazaar.iwb.activities.RefreshingActivity;
import com.imagesandwallpaper.bazaar.iwb.db.CoinsDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.AdsModel;
import com.imagesandwallpaper.bazaar.iwb.models.AdsModelList;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "4d2242b4-ec1e-4e4e-9969-5bf6571c8b4d";

    public static MyApp mInstance;
    ApiInterface apiInterface;
    SharedPreferences.Editor editor;

    public MyApp() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Paper.init(mInstance);
        ExecutorService service = Executors.newSingleThreadExecutor();
        // Background work
        editor = PreferenceManager.getDefaultSharedPreferences(mInstance).edit();
        service.execute(this::fetchAds);
    }

    public void intent() {
        new Handler().postDelayed(() -> {

            if (!AppOpenManager.isIsShowingAd) {

                Intent intent = new Intent(getApplicationContext(), RefreshingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                AppOpenManager.isIsShowingAd = false;

            }

        }, 2400);
    }

    private void fetchAds() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler());
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        apiInterface = ApiWebServices.getApiInterface();
        Call<AdsModelList> call = apiInterface.fetchAds("Wallpaper Bazaar");
        call.enqueue(new Callback<AdsModelList>() {
            @Override
            public void onResponse(@NonNull Call<AdsModelList> call, @NonNull Response<AdsModelList> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getData() != null) {
                        for (AdsModel ads : response.body().getData()) {
                            Log.d("checkIds",
                                    ads.getId()
                                            + "\n" + ads.getAppId()
                                            + "\n" + ads.getAppLovinSdkKey()
                                            + "\n" + ads.getBannerTop()
                                            + "\n" + ads.getBannerTopNetworkName()
                                            + "\n" + ads.getBannerBottom()
                                            + "\n" + ads.getBannerBottomNetworkName()
                                            + "\n" + ads.getInterstitialAds()
                                            + "\n" + ads.getInterstitialNetwork()
                                            + "\n" + ads.getNativeAds()
                                            + "\n" + ads.getNativeAdsNetworkName()
                                            + "\n" + ads.getOpenAds()
                                            + "\n" + ads.getOpenAdsNetworkName()
                            );

                            Paper.book().write(Prevalent.id, ads.getId());
                            Paper.book().write(Prevalent.appId, ads.getAppId());
                            Paper.book().write(Prevalent.appLovinId, ads.getAppLovinSdkKey());
                            Paper.book().write(Prevalent.bannerTop, ads.getBannerTop());
                            Paper.book().write(Prevalent.bannerTopNetworkName, ads.getBannerTopNetworkName());
                            Paper.book().write(Prevalent.bannerBottom, ads.getBannerBottom());
                            Paper.book().write(Prevalent.bannerBottomNetworkName, ads.getBannerBottomNetworkName());
                            Paper.book().write(Prevalent.interstitialAds, ads.getInterstitialAds());
                            Paper.book().write(Prevalent.interstitialNetwork, ads.getInterstitialNetwork());
                            Paper.book().write(Prevalent.nativeAds, ads.getNativeAds());
                            Paper.book().write(Prevalent.nativeAdsNetworkName, ads.getNativeAdsNetworkName());
                            Paper.book().write(Prevalent.openAds, ads.getOpenAds());
                            Paper.book().write(Prevalent.openAdsNetworkName, ads.getOpenAdsNetworkName());

                            try {
                                ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                Bundle bundle = ai.metaData;
                                String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                Log.d(TAG, "Name Found: " + myApiKey);
                                ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", Paper.book().read(Prevalent.appId));//you can replace your key APPLICATION_ID here
                                String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                Log.d(TAG, "ReNamed Found: " + ApiKey);
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                            } catch (NullPointerException e) {
                                Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                            }

                            try {
                                ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                Bundle bundle = ai.metaData;
                                String myApiKey = bundle.getString("applovin.sdk.key");
                                Log.d(TAG, "Name Found: " + myApiKey);
                                ai.metaData.putString("applovin.sdk.key", Paper.book().read(Prevalent.appLovinId));     //you can replace your key APPLICATION_ID here
                                String ApiKey = bundle.getString("applovin.sdk.key");
                                Log.d(TAG, "ReNamed Found: " + ApiKey);
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                            } catch (NullPointerException e) {
                                Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                            }
                        }

                        if (!Objects.equals(Paper.book().read(Prevalent.openAds), "null")) {
                            new AppOpenManager(mInstance, Paper.book().read(Prevalent.openAds), getApplicationContext());
                        }
//                        else {
//                            new Handler().postDelayed(() -> {
//
//                                Intent intent = new Intent(getApplicationContext(), RefreshingActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//
//                            }, 2000);
//
//                        }
                    }
                } else {
                    Log.e("adsError", response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<AdsModelList> call, @NonNull Throwable t) {
                Log.d("adsError", t.getMessage());
            }
        });
    }

    private class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            JSONObject data = result.getNotification().getAdditionalData();
            String activityToBeOpened, imgPos, cat_pos, cat_item_pos;

            if (data != null) {
                activityToBeOpened = data.optString("action", null);
                imgPos = data.optString("pos", null);
                cat_pos = data.optString("cat_pos", null);
                cat_item_pos = data.optString("cat_item_pos", null);
                editor.putString("pos", imgPos);
                editor.putString("action", activityToBeOpened);
                editor.putString("cat_pos", cat_pos);
                editor.putString("cat_item_pos", cat_item_pos);
                editor.apply();
                switch (activityToBeOpened) {
                    case "home":
                    case "cat":
                    case "pop":
                    case "new":
                    case "pre": {
                        Intent intent = new Intent(MyApp.this, HomeActivity.class);
                        intent.putExtra("action", activityToBeOpened);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    }
                    case "fav": {
                        Intent intent = new Intent(MyApp.this, FavoriteActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        break;
                    }
                }
            } else {

                Intent intent = new Intent(MyApp.this, RefreshingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
    }
}
