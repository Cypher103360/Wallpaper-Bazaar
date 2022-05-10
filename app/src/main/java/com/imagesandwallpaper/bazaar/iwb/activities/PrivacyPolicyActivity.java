package com.imagesandwallpaper.bazaar.iwb.activities;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityPrivacyPolicyBinding;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webView;
    ShowAds ads = new ShowAds();
    ActivityPrivacyPolicyBinding binding;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        webView = findViewById(R.id.policy);

        key = getIntent().getStringExtra("key");
        if (key.equals("terms")) {
            webView.loadUrl("file:///android_asset/terms_and_conditions.html");
        } else if (key.equals("policy")) {
            webView.loadUrl("file:///android_asset/privacy_policy.html");
        }

//        getLifecycle().addObserver(ads);
//        ads.showTopBanner(this, binding.adViewTop);
//        ads.showBottomBanner(this, binding.adViewBottom);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Ads.destroyBanner();

    }
}