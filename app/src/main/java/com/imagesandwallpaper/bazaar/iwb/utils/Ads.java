package com.imagesandwallpaper.bazaar.iwb.utils;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class Ads implements MaxAdViewAdListener, LifecycleObserver {


    public static IronSourceBannerLayout banner;
    public static InterstitialAd mInterstitialAd;
    static boolean checkAdLoad = false;
    private static MaxAd nativeAd;
    public NativeAd nativeAds;
    MaxAdView maxAdView;
    MaxInterstitialAd interstitialAd;
    private int retryAttempt;

    public static void destroyBanner() {
        if (Objects.equals(Paper.book().read(Prevalent.bannerBottomNetworkName), "IronSourceWithMeta")) {
            IronSource.destroyBanner(banner);
            Log.d("destroy", "banner");
        } else if (Objects.equals(Paper.book().read(Prevalent.bannerTopNetworkName), "IronSourceWithMeta")) {
            IronSource.destroyBanner(banner);
            Log.d("destroy", "banner");
        }
    }

    public void showBannerAd(Activity context, RelativeLayout container, String networkName, String bannerAdId) {

        switch (networkName) {
            case "AdmobWithMeta":
                MobileAds.initialize(context);
                AudienceNetworkAds.initialize(context);

                AdRequest adRequest = new AdRequest.Builder().build();
                AdView adView = new AdView(context);
                container.addView(adView);
                adView.setAdUnitId(bannerAdId);
                adView.setAdSize(AdSize.BANNER);
                adView.loadAd(adRequest);
                container.setVisibility(View.VISIBLE);

                break;
            case "IronSourceWithMeta":

                IronSource.init(context, bannerAdId);
                AudienceNetworkAds.initialize(context);
                IronSource.getAdvertiserId(context);
                IronSource.shouldTrackNetworkState(context, true);

                IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
                banner = IronSource.createBanner(context, ISBannerSize.BANNER);
                container.addView(banner);

                if (banner != null) {
                    // set the banner listener
                    banner.setBannerListener(new BannerListener() {
                        @Override
                        public void onBannerAdLoaded() {
                            Log.d(TAG, "onBannerAdLoaded");
                            container.setVisibility(View.VISIBLE);
                            // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                        }

                        @Override
                        public void onBannerAdLoadFailed(IronSourceError error) {
                            Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                            context.runOnUiThread(container::removeAllViews);
                        }

                        @Override
                        public void onBannerAdClicked() {
                            Log.d(TAG, "onBannerAdClicked");
                        }

                        @Override
                        public void onBannerAdScreenPresented() {
                            Log.d(TAG, "onBannerAdScreenPresented");
                        }

                        @Override
                        public void onBannerAdScreenDismissed() {
                            Log.d(TAG, "onBannerAdScreenDismissed");
                        }

                        @Override
                        public void onBannerAdLeftApplication() {
                            Log.d(TAG, "onBannerAdLeftApplication");
                        }
                    });

                    // load ad into the created banner
                    IronSource.loadBanner(banner);
                } else {
                    Toast.makeText(context, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
                }

                break;
            case "AppLovinWithMeta":
                AudienceNetworkAds.initialize(context);
                AppLovinSdk.getInstance(context).setMediationProvider("max");
                AppLovinSdk.initializeSdk(context);

                maxAdView = new MaxAdView(bannerAdId, context);
                maxAdView.setListener(this);
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = context.getResources().getDimensionPixelSize(R.dimen.banner_height);
                maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                maxAdView.setBackgroundColor(Color.WHITE);
                container.addView(maxAdView);
                maxAdView.loadAd();
                //To show a banner, make the following calls:
                maxAdView.startAutoRefresh();

                break;

            case "Meta":
                AudienceNetworkAds.initialize(context);
                com.facebook.ads.AdView adView1 = new com.facebook.ads.AdView(context, bannerAdId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                container.addView(adView1);
                adView1.loadAd();
                com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                    @Override
                    public void onError(Ad ad, AdError adError) {
// Ad error callback
                        Toast.makeText(
                                context,
                                "Error: " + adError.getErrorMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                        Log.d("Error: ", adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
// Ad loaded callback
                        Log.d("adLoaded: ", ad.getPlacementId());

                    }

                    @Override
                    public void onAdClicked(Ad ad) {
// Ad clicked callback
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
// Ad impression logged callback
                    }
                };
                break;
        }
    }

    public void admobInterstitialAd(Activity context, String interstitialId) {
        MobileAds.initialize(context);
        AudienceNetworkAds.initialize(context);
        // Initialize an InterstitialAd.
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, interstitialId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(context);
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    public MaxInterstitialAd appLovinAdInterstitialAd(Activity context, String interstitialId) {
        AppLovinSdk.getInstance(context).setMediationProvider("max");
        AppLovinSdk.initializeSdk(context);
        AudienceNetworkAds.initialize(context);


        interstitialAd = new MaxInterstitialAd(interstitialId, context);
        interstitialAd.setListener(this);
        // Load the first ad
        interstitialAd.loadAd();
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
            Toast.makeText(context, "ads  ready", Toast.LENGTH_SHORT).show();

        }
        return interstitialAd;
    }

    public void ironSourceInterstitialAd(Activity context, String interstitialId) {
        IronSource.init(context, interstitialId);
        IronSource.getAdvertiserId(context);
        //Network Connectivity Status
        IronSource.shouldTrackNetworkState(context, true);
        AudienceNetworkAds.initialize(context);

        IronSource.loadInterstitial();
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        IronSource.showInterstitial();
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                IronSource.showInterstitial();

            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                Log.d(TAG, ironSourceError.getErrorMessage());
            }

            @Override
            public void onInterstitialAdOpened() {
            }

            @Override
            public void onInterstitialAdClosed() {

            }

            @Override
            public void onInterstitialAdShowSucceeded() {

            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

            }

            @Override
            public void onInterstitialAdClicked() {

            }
        });
    }

    public void metaInterstitialAd(Activity context, String interstitialId) {
        AudienceNetworkAds.initialize(context);
        com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, interstitialId);
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }


    //  Admob Native Ads
    public void loadadmobNativeAd(final Activity context, final FrameLayout frameLayout) {
        refreshAd(context, frameLayout);
    }

    public void refreshAd(final Activity context, final FrameLayout frameLayout) {

        AdLoader.Builder builder = new AdLoader.Builder(context, Objects.requireNonNull(Paper.book().read(Prevalent.nativeAds)));

        builder.forNativeAd(nativeAd -> {


            if (nativeAds != null) {
                nativeAds.destroy();
            }
            nativeAds = nativeAd;
            NativeAdView adView = (NativeAdView) context.getLayoutInflater()
                    .inflate(R.layout.ad_unified, null);
            populateUnifiedNativeAdView(nativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

//        AdLoader adLoader = builder.withAdListener(new AdListener() {
//            @Override
//            /**      * @deprecated (when, why, refactoring advice...)      */
//            @Deprecated
//            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(context, errorCode+"", Toast.LENGTH_SHORT).show();
//            }
//        }).build();
        AdLoader adLoader = builder.withAdListener(new AdListener() {

            @Override
            @Deprecated
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
//                Toast.makeText(context, loadAdError.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        }).build();
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        adLoader.loadAd(adRequest);

    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {


        com.google.android.gms.ads.nativead.MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);


        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());


        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }

    public void IronSourceMRECBanner(Activity context, final FrameLayout container) {
        IronSource.getAdvertiserId(context);
        IronSource.shouldTrackNetworkState(context, true);
        AudienceNetworkAds.initialize(context);
        IronSource.init(context, Paper.book().read(Prevalent.nativeAds));
        IronSource.setMetaData("Facebook_IS_CacheFlag", "IMAGE");
        banner = IronSource.createBanner(context, ISBannerSize.RECTANGLE);
        container.addView(banner);

        if (banner != null) {
            // set the banner listener
            banner.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    Log.d(TAG, "onBannerAdLoaded");
                    container.setVisibility(View.VISIBLE);
                    // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                }

                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {
                    Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                    context.runOnUiThread(container::removeAllViews);
                }

                @Override
                public void onBannerAdClicked() {
                    Log.d(TAG, "onBannerAdClicked");
                }

                @Override
                public void onBannerAdScreenPresented() {
                    Log.d(TAG, "onBannerAdScreenPresented");
                }

                @Override
                public void onBannerAdScreenDismissed() {
                    Log.d(TAG, "onBannerAdScreenDismissed");
                }

                @Override
                public void onBannerAdLeftApplication() {
                    Log.d(TAG, "onBannerAdLeftApplication");
                }
            });

            // load ad into the created banner
            IronSource.loadBanner(banner);
        } else {
            Toast.makeText(context, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
        }
    }

    public void appLovinMRECBanner(Activity context, FrameLayout frameLayout) {
        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Objects.requireNonNull(Paper.book().read(Prevalent.nativeAds)), context);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
                // Cleanup any pre-existing native ad to prevent memory leaks.
                if (nativeAd != null) {
                    nativeAdLoader.destroy(nativeAd);
                }

                // Save ad for cleanup.
                nativeAd = maxAd;

                // Add ad view to view.
                frameLayout.removeAllViews();
                frameLayout.addView(maxNativeAdView);
                super.onNativeAdLoaded(maxNativeAdView, maxAd);

            }

            @Override
            public void onNativeAdLoadFailed(String s, MaxError maxError) {
                super.onNativeAdLoadFailed(s, maxError);
                Log.e("aaaaaaaerror", maxError.getMessage());

            }

            @Override
            public void onNativeAdClicked(MaxAd maxAd) {
                super.onNativeAdClicked(maxAd);
            }
        });


        nativeAdLoader.loadAd();


    }

    public void metaRectangleBanner(Activity context, FrameLayout container) {

        AudienceNetworkAds.initialize(context);
        com.facebook.ads.AdView adView1 = new com.facebook.ads.AdView(context, Paper.book().read(Prevalent.nativeAds), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
        container.addView(adView1);
        adView1.loadAd();
    }

    // maxAds
    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        retryAttempt = 0;
        maxAdView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.d(TAG, "applovin" + error.getMessage());
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd != null) {

                    interstitialAd.loadAd();
                }
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        if (interstitialAd != null) {
            interstitialAd.loadAd();
        }
    }

}
