package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySignupBinding;
import com.imagesandwallpaper.bazaar.iwb.db.CoinsDatabase;
import com.imagesandwallpaper.bazaar.iwb.db.entity.Coins;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CoinsModel;
import com.imagesandwallpaper.bazaar.iwb.models.CoinsModelList;
import com.imagesandwallpaper.bazaar.iwb.models.MessageModel;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    int RC_SIGN_IN = 1000;
    FirebaseAnalytics mFirebaseAnalytics;
    CoinsDatabase coinsDatabase;
    SharedPreferences.Editor editor;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    String coinsId, allCoins, key;
    private GoogleSignInAccount account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(binding.getRoot());
        account = GoogleSignIn.getLastSignedInAccount(this);

        apiInterface = ApiWebServices.getApiInterface();
        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        getAllCoins();

        key = getIntent().getStringExtra("key");
        Log.d("dataSubs", allCoins + " " + key);

        // Google SignIn
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToNextActivity();
        }

        // test link span
        TextView tv = findViewById(R.id.terms_text);
        Spannable span = Spannable.Factory.getInstance().newSpannable(
                "By continuing, you agree to Wallpaper Bazaar's Terms of Service and acknowledge that you've read our Privacy Policy");
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("key", "terms");
                startActivity(intent);
            }
        }, 47, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // All the rest will have the same spannable.
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, PrivacyPolicyActivity.class);
                intent.putExtra("key", "policy");
                startActivity(intent);
            }
        };

        // set the "test " spannable.
        span.setSpan(cs, 101, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(span);

        tv.setMovementMethod(LinkMovementMethod.getInstance());

        binding.withGoogle.setOnClickListener(view -> {
            signIn();
        });

        // Room Database
        coinsDatabase = Room.databaseBuilder(
                        SignupActivity.this,
                        CoinsDatabase.class,
                        "CoinsDB")
                .allowMainThreadQueries()
                .build();

        if (key != null && key.equals("subs")) {
            binding.skipBtn.setVisibility(View.GONE);
        }

        binding.skipBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, RefreshingActivity.class));
            editor.putBoolean("skip", true);
            editor.apply();
            coinsDatabase.getCoinsDAO().addCoins(new Coins(0, "30"));
        });

    }

    private void getAllCoins() {
        Call<CoinsModelList> call = apiInterface.getAllCoins();
        call.enqueue(new Callback<CoinsModelList>() {
            @Override
            public void onResponse(@NonNull Call<CoinsModelList> call, @NonNull Response<CoinsModelList> response) {
                for (CoinsModel coins : response.body().getData()) {
                    coinsId = coins.getId();
                    allCoins = coins.getCoins();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CoinsModelList> call, @NonNull Throwable t) {

            }
        });
    }


    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToNextActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void navigateToNextActivity() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Google Sign in");
        mFirebaseAnalytics.logEvent("Clicked_On_Google_SignIn", bundle);

        Intent intent = new Intent(SignupActivity.this, RefreshingActivity.class);
        startActivity(intent);
        finish();

        if (key.equals("subs")) {
            allCoins = getIntent().getStringExtra("coins");
            map.put("id", coinsId);
            map.put("coins", allCoins);
            Log.d("myAll", key);
        } else {
            map.put("id", coinsId);
            map.put("coins", "50");
        }
        updateCoins(map);
    }

    private void updateCoins(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateCoins(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                Toast.makeText(SignupActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {

            }
        });
    }
}