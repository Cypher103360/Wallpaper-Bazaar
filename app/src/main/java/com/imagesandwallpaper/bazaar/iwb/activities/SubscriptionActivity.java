package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySubscriptionBinding;
import com.imagesandwallpaper.bazaar.iwb.db.CoinsDatabase;
import com.imagesandwallpaper.bazaar.iwb.db.entity.Coins;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.MessageModel;
import com.imagesandwallpaper.bazaar.iwb.models.UserData.UserDataModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.UserData.UserDataViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionActivity extends AppCompatActivity {
    public static int totalCoins;
    ActivitySubscriptionBinding binding;
    CoinsDatabase coinsDatabase;
    TextView allCoins;
    MaterialCardView watchAdsCoins, referEarnCoins, days7Coins, days14Coins, days21Coins, oneMonthCoins;
    String getAllCoins, userCoins;
    ApiInterface apiInterface;
    UserDataViewModel userDataViewModel;
    Map<String, String> map = new HashMap<>();
    Dialog loading;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        account = GoogleSignIn.getLastSignedInAccount(this);
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(this);
        initViews();
        getAllCoins = getIntent().getStringExtra("coins");


        // Room Database
        coinsDatabase = Room.databaseBuilder(
                        SubscriptionActivity.this,
                        CoinsDatabase.class,
                        "CoinsDB")
                .allowMainThreadQueries()
                .build();

    }

    private void initViews() {
        allCoins = binding.allCoins;
        watchAdsCoins = binding.watchAdsBtn;
        referEarnCoins = binding.referAndEarnBtn;
        days7Coins = binding.freeFor7DaysBtn;
        days14Coins = binding.freeFor14DaysBtn;
        days21Coins = binding.freeFor21DaysBtn;
        oneMonthCoins = binding.freeFor1monthBtn;
        binding.backIcon.setOnClickListener(v -> {
            onBackPressed();
        });

        watchAdsCoins.setOnClickListener(v -> {
            loading.show();
            totalCoins = Integer.parseInt(getAllCoins) + 10;

            if (account != null) {
                map.put("email", account.getEmail());
                map.put("coins", String.valueOf(totalCoins));
                updateUserData(map, account.getEmail());
            } else {
                coinsDatabase.getCoinsDAO().updateCoins(new Coins(1, String.valueOf(totalCoins)));
                Paper.book().write(Prevalent.coinsId, totalCoins);
                allCoins.setText(coinsDatabase.getCoinsDAO().getCoins());
                getAllCoins = coinsDatabase.getCoinsDAO().getCoins();
                loading.dismiss();
            }

            Toast.makeText(this, String.valueOf(totalCoins), Toast.LENGTH_SHORT).show();
        });

        referEarnCoins.setOnClickListener(v -> {
            if (account !=null){

            }else {
                ShowDialog(getAllCoins);
            }
        });



    }

    private void ShowDialog(String coins) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("LogIn Or SignUp");
        builder.setMessage("You need to LogIn or SignUp to save your coins.");
        builder.setPositiveButton("Log In", (dialog, which) -> {

            Intent intent = new Intent(SubscriptionActivity.this,SignupActivity.class);
            intent.putExtra("key","subs");
            intent.putExtra("coins",coins);
            startActivity(intent);

        }).setNegativeButton("Cancel", (dialog, which) -> {

        });
        builder.show();
    }

    private void updateUserData(Map<String, String> map, String email) {
        Call<MessageModel> call = apiInterface.updateUserData(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                // Toast.makeText(SubscriptionActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                fetchUserData(email);
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void fetchUserData(String email) {
        loading.show();

        userDataViewModel = new ViewModelProvider(this, new UserDataModelFactory(this.getApplication(), email))
                .get(UserDataViewModel.class);
        userDataViewModel.getAllUserData().observe(this, userDataModel -> {

            if (userDataModel != null) {
                userCoins = userDataModel.getCoins();
            }
            getAllCoins = userCoins;
            allCoins.setText(userCoins);
            loading.dismiss();

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        allCoins.setText(getAllCoins);
    }
}