package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityRefreshingBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CoinsModel;
import com.imagesandwallpaper.bazaar.iwb.models.CoinsModelList;
import com.imagesandwallpaper.bazaar.iwb.models.MessageModel;
import com.imagesandwallpaper.bazaar.iwb.utils.AutoStartKt;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshingActivity extends AppCompatActivity {

    ActivityRefreshingBinding binding;
    Button startBtn;
    ShowAds showAds;
    FirebaseAnalytics mFirebaseAnalytics;
    GoogleSignInAccount account;
    ApiInterface apiInterface;
    Dialog dialog;
    String coinsId,allCoins;
    Map<String,String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRefreshingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiWebServices.getApiInterface();
        account = GoogleSignIn.getLastSignedInAccount(this);
        startBtn = findViewById(R.id.startBtn);
        showAds = new ShowAds();
        dialog = CommonMethods.loadingDialog(this);

        // fetching coins and uploading in user's data
        if (account != null) {
            getAllCoins();
        }


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

    private void getAllCoins() {
        Call<CoinsModelList> call = apiInterface.getAllCoins();
        call.enqueue(new Callback<CoinsModelList>() {
            @Override
            public void onResponse(@NonNull Call<CoinsModelList> call, @NonNull Response<CoinsModelList> response) {
                assert response.body() != null;
                for (CoinsModel coins : response.body().getData()) {
                    coinsId = coins.getId();
                    allCoins = coins.getCoins();
                    Log.d("coins", coinsId + " " + allCoins);
                }

                map.put("name", account.getDisplayName());
                map.put("email", account.getEmail());
                map.put("coins", allCoins);
                uploadUserData(map);
            }

            @Override
            public void onFailure(@NonNull Call<CoinsModelList> call, @NonNull Throwable t) {

            }
        });
    }

    private void uploadUserData(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadUserData(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(RefreshingActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(RefreshingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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