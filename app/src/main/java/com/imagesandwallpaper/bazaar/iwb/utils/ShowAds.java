package com.imagesandwallpaper.bazaar.iwb.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.applovin.mediation.ads.MaxInterstitialAd;

import java.util.Objects;

import io.paperdb.Paper;

public class ShowAds implements LifecycleObserver {

    Ads ads = new Ads();
    RelativeLayout topAdview, bottomAdview;
    Activity context;
    String networkName, bannerId;
    String nativeAds, nativeNetwork;

    public ShowAds(Activity activity, RelativeLayout AdViewTop, RelativeLayout AdViewBottom) {
        context = activity;
        topAdview = AdViewTop;
        bottomAdview = AdViewBottom;
    }

    public ShowAds() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {

        if (topAdview != null && bottomAdview != null) {
            showTopBanner(context, bottomAdview);
        } else if (topAdview != null) {
            showTopBanner(context, topAdview);

        } else if (bottomAdview != null) {
            showBottomBanner(context, bottomAdview);

        }
        Log.d(TAG, "onStart");
    }

    public void showInterstitialAds(Activity context) {
        if ("AdmobWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "AdmobWithMeta");
            ads.admobInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));

        } else if ("IronSourceWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "IronSourceWithMeta");
            ads.ironSourceInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));


        } else if ("AppLovinWithMeta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {
            Log.d(TAG, "AppLovinWithMeta");
            MaxInterstitialAd maxInterstitialAd = ads.appLovinAdInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));

            new Handler().postDelayed(() -> {
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                    Log.d(TAG, "ads ready");
                } else {
                    ads.appLovinAdInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));
                }
            }, 3000);
        } else if ("Meta".equals(Paper.book().read(Prevalent.interstitialNetwork))) {

            ads.metaInterstitialAd(context, Paper.book().read(Prevalent.interstitialAds));
        }

    }

    public void showTopBanner(Activity context, RelativeLayout topAdView) {
        networkName = Paper.book().read(Prevalent.bannerTopNetworkName);
        bannerId = Paper.book().read(Prevalent.bannerTop);
        ads.showBannerAd(context, topAdView, networkName, bannerId);

    }

    public void showBottomBanner(Activity context, RelativeLayout bottomAdView) {
        networkName = Paper.book().read(Prevalent.bannerBottomNetworkName);
        bannerId = Paper.book().read(Prevalent.bannerBottom);
        ads.showBannerAd(context, bottomAdView, networkName, bannerId);

    }

    public void showNativeAds(Activity context, FrameLayout frameLayout) {
        nativeAds = Paper.book().read(Prevalent.nativeAds);
        nativeNetwork = Paper.book().read(Prevalent.nativeAdsNetworkName);

        switch (Objects.requireNonNull(nativeNetwork)) {
            case "AdmobWithMeta":
                ads.loadadmobNativeAd(context, frameLayout);
                break;
            case "IronSourceWithMeta":
                ads.IronSourceMRECBanner(context, frameLayout);

                break;
            case "AppLovinWithMeta":
                ads.appLovinMRECBanner(context, frameLayout);

                break;
            case "Meta":
                ads.metaRectangleBanner(context, frameLayout);
                break;
        }
    }


}