package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.sdk.AppLovinSdk;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRefreshingBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.integration.IntegrationHelper;

import java.io.UnsupportedEncodingException;

public class RefreshingActivity extends AppCompatActivity {

    ActivityRefreshingBinding binding;
    Button startBtn;
    ShowAds showAds;
    ImageView shareBtn;
    LottieAnimationView whatsappLottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startBtn = findViewById(R.id.startBtn);
        shareBtn = findViewById(R.id.share_icon);
        whatsappLottie = findViewById(R.id.lottie_contact);
        showAds = new ShowAds();
        getLifecycle().addObserver(showAds);
        showAds.showTopBanner(this, binding.adViewTop);
        showAds.showBottomBanner(this, binding.adViewBottom);
//        AppLovinSdk.getInstance( this ).showMediationDebugger();

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        startBtn.startAnimation(myAnim);
        new Handler().postDelayed(() -> startBtn.setVisibility(View.VISIBLE), 5000);

        startBtn.setOnClickListener(view -> {
            showAds.showInterstitialAds(this);
            Ads.destroyBanner();
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();

        });

        shareBtn.setOnClickListener(view -> {
            CommonMethods.shareApp(this);
        });
        whatsappLottie.setOnClickListener(view -> {
            try {
                CommonMethods.whatsApp(this);
            } catch (UnsupportedEncodingException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });



    }
}