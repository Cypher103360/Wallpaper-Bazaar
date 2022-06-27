package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRefreshingBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.AutoStartKt;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.util.Objects;

import io.paperdb.Paper;

public class RefreshingActivity extends AppCompatActivity {

    ActivityRefreshingBinding binding;
    Button startBtn;
    ShowAds showAds;
    FirebaseAnalytics mFirebaseAnalytics;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        startBtn = findViewById(R.id.startBtn);
        showAds = new ShowAds();
        dialog = CommonMethods.loadingDialog(this);
        getLifecycle().addObserver(showAds);
        new Handler().postDelayed(() -> {
            showAds.showTopBanner(this, binding.adViewTop);
            showAds.showBottomBanner(this, binding.adViewBottom);
        }, 1000);

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
//        AutoStartKt.autoStart(this);


        startBtn.setVisibility(View.VISIBLE);
        startBtn.startAnimation(myAnim);
        startBtn.setEnabled(false);

        new Handler().postDelayed(() -> startBtn.setEnabled(true), 3000);

        startBtn.setOnClickListener(view -> {
            dialog.show();
            new Handler().postDelayed(() ->
            {
                if (Objects.equals(Paper.book().read(Prevalent.openAds), "null")) {
                    showAds.showInterstitialAds(this);
                }
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

    }

    @Override
    public void onBackPressed() {
        ShowExitDialog();
    }

    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    private void ShowExitDialog() {
        Dialog exitDialog = new Dialog(RefreshingActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_layout);
        exitDialog.getWindow().setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT);
        exitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        exitDialog.setCancelable(false);
        exitDialog.show();

        TextView rateNow = exitDialog.findViewById(R.id.rate_now);
        TextView okBtn = exitDialog.findViewById(R.id.ok);
        ImageView cancelBtn = exitDialog.findViewById(R.id.dismiss_btn);

        cancelBtn.setOnClickListener(v -> {
            exitDialog.dismiss();
        });
        okBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            moveTaskToBack(true);
            System.exit(0);
        });

        rateNow.setOnClickListener(v -> {
            CommonMethods.rateApp(getApplicationContext());
        });


    }

}