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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Do You Really Want To Exit?\nAlso Rate Us 5 Star.")
                .setNeutralButton("CANCEL", (dialog, which) -> {
                });


        builder.setNegativeButton("RATE APP", (dialog, which) -> CommonMethods.rateApp(getApplicationContext()))
                .setPositiveButton("OK!!", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    moveTaskToBack(true);
                    System.exit(0);
                });
        builder.show();
    }
}