package com.imagesandwallpaper.bazaar.iwb.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityMainBinding;
import com.imagesandwallpaper.bazaar.iwb.models.RandomImage;
import com.imagesandwallpaper.bazaar.iwb.models.RandomImgDatabase;
import com.imagesandwallpaper.bazaar.iwb.utils.MyReceiver;
import com.ironsource.mediationsdk.IronSource;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // 4d2242b4-ec1e-4e4e-9969-5bf6571c8b4d
    // Google Client Id
    // 592357180684-khldqgeopsjv00jjiq6ap18d18b325ru.apps.googleusercontent.com

    public static final String BroadCastStringForAction = "checkingInternet";
    public RandomImage r;
    int REQUEST_CODE = 11;
    int count = 1;
    ActivityMainBinding binding;
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                if (intent.getStringExtra("online_status").equals("true")) {
                    Set_Visibility_ON();
                    count++;
                } else {
                    Set_Visibility_OFF();
                }
            }
        }
    };
    FirebaseAnalytics firebaseAnalytics;
    IntentFilter intentFilter;
    RandomImgDatabase randomImgDatabase;
    List<RandomImage> randomImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        inAppUpdate();

        randomImgDatabase = Room.databaseBuilder(
                        this,
                        RandomImgDatabase.class
                        , "RandomImgDB")
                .allowMainThreadQueries()
                .build();
        binding.img.setScaleType(ImageView.ScaleType.FIT_XY);
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, MyReceiver.class);
        startService(serviceIntent);
        if (isOnline(getApplicationContext())) {
            Set_Visibility_ON();
            fetchRandomImage();
        } else {
            Set_Visibility_OFF();
        }




    }



    private void fetchRandomImage() {

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(() -> {
            // Background work
            randomImages.addAll(randomImgDatabase.getRandomDao().getAllRandomImages());

            if (randomImages != null && randomImages.size() > 0) {
                int pos = new Random().nextInt(randomImages.size());
                r = randomImgDatabase.getRandomDao().getRandom(pos);

                handler.post(() -> {
                    if (r != null) {
                        switch (FilenameUtils.getExtension(r.getImage())) {
                            case "jpeg":
                            case "jpg":
                            case "png":
                                binding.img.setScaleType(ImageView.ScaleType.FIT_XY);
                                Glide.with(this).load(r.getImage()).into(binding.img);

                                break;
                            case "gif":
//                                fetchRandomImage();
                                Glide.with(this).asGif().load(r.getImage()).into(binding.img);
                                break;
                        }
                    } else {
                        Log.d("contentValue", "r = null" + randomImages.size());
                    }
                });
            } else {
                Log.d("contentValue", "list is empty");
            }

            //Executed after background work had finished
        });

    }

    private void Set_Visibility_OFF() {
        binding.lottieLoading.setVisibility(View.GONE);
//        binding.myText.setVisibility(View.GONE);
        binding.lottieNoInternet.setVisibility(View.VISIBLE);
        binding.tvNotConnected.setVisibility(View.VISIBLE);
//        binding.mainBackground.setBackgroundColor(0);

    }

    private void Set_Visibility_ON() {
        binding.lottieLoading.setVisibility(View.VISIBLE);
//        binding.myText.setVisibility(View.VISIBLE);
        binding.lottieNoInternet.setVisibility(View.GONE);
        binding.tvNotConnected.setVisibility(View.GONE);
//        binding.mainBackground.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_color));

        if (count == 2) {
            new Handler().postDelayed(() -> {
                // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                startActivity(new Intent(MainActivity.this, RefreshingActivity.class));

//                if (account != null) {
//                    startActivity(new Intent(MainActivity.this, RefreshingActivity.class));
//                } else {
//                    startActivity(new Intent(MainActivity.this, SignupActivity.class));
//                }
                // finish();
            }, 3000);
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(MainActivity.this, "Downloading Started", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d("TAG", "Downloading Failed" + resultCode);
            }
        }
    }

    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.IMMEDIATE, MainActivity.this, REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        if (auth.getCurrentUser() != null | account != null) {
//            startActivity(new Intent(MainActivity.this, HomeActivity.class));
//        } else {
//            startActivity(new Intent(MainActivity.this, SignupActivity.class));
//        }
//        finish();
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
        unregisterReceiver(receiver);
    }


}