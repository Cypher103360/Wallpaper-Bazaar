package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRefreshingBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.io.UnsupportedEncodingException;

public class RefreshingActivity extends AppCompatActivity {

    ActivityRefreshingBinding binding;
    Button startBtn;
    ShowAds showAds;
    ImageView shareBtn;
    LottieAnimationView whatsappLottie;
    FirebaseAnalytics mFirebaseAnalytics;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startBtn = findViewById(R.id.startBtn);
        shareBtn = findViewById(R.id.share_icon);
        whatsappLottie = findViewById(R.id.lottie_contact);
        showAds = new ShowAds();
        dialog = CommonMethods.loadingDialog(this);
        getLifecycle().addObserver(showAds);
        showAds.showTopBanner(this, binding.adViewTop);
        showAds.showBottomBanner(this, binding.adViewBottom);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        startBtn.startAnimation(myAnim);
        new Handler().postDelayed(() -> startBtn.setVisibility(View.VISIBLE), 5000);

        startBtn.setOnClickListener(view -> {
            dialog.show();
            new Handler().postDelayed(() ->
            {
                showAds.showInterstitialAds(this);
                showAds.destroyBanner();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Start");
                mFirebaseAnalytics.logEvent("Clicked_On_Start", bundle);
                dialog.dismiss();

            }, 2000);


        });

        shareBtn.setOnClickListener(view -> {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Share");
            mFirebaseAnalytics.logEvent("Clicked_On_Start_Share", bundle);
            CommonMethods.shareApp(this);
        });
        whatsappLottie.setOnClickListener(view -> {
            try {
                CommonMethods.whatsApp(this);
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Start");
                mFirebaseAnalytics.logEvent("Clicked_On_Start_Whatsapp", bundle);
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

    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}