package com.imagesandwallpaper.bazaar.iwb.activities;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityPrivacyPolicyBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webView;
    ShowAds ads = new ShowAds();
    ActivityPrivacyPolicyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        webView = findViewById(R.id.policy);
        webView.loadUrl("file:///android_asset/privacy.html");
        getLifecycle().addObserver(ads);
        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Ads.destroyBanner();

    }
}