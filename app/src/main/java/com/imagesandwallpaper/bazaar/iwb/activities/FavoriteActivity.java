package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.adapters.CatItemImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFavoriteBinding;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteActivity extends AppCompatActivity implements CatItemImageClickInterface {
    public static List<ImageItemModel> imageItemModels;
    CatItemImageAdapter catItemImageAdapter;
    RecyclerView catItemsRecyclerView;
    ActivityFavoriteBinding binding;
    FavoriteAppDatabase favoriteAppDatabase;
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        catItemsRecyclerView = binding.catItemsRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        catItemsRecyclerView.setLayoutManager(layoutManager);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        binding.backIcon.setOnClickListener(view -> {
            if (preferences.getString("action", "").equals("")) {
                onBackPressed();
            } else {
                preferences.edit().clear().apply();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);

            }
        });

        getLifecycle().addObserver(ads);
        imageItemModels = new ArrayList<>();

        binding.catItemSwipeRefresh.setOnRefreshListener(() -> {
            displayAllContactInBackground();
            binding.catItemSwipeRefresh.setRefreshing(false);
        });

    }

    private void displayAllContactInBackground() {

        favoriteAppDatabase = Room.databaseBuilder(
                        this,
                        FavoriteAppDatabase.class
                        , "FavoriteDB")
                .build();
        catItemImageAdapter = new CatItemImageAdapter(this, this);
        catItemsRecyclerView.setAdapter(catItemImageAdapter);
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(() -> {
            // Background work
            imageItemModels.clear();
            for (Favorite f : favoriteAppDatabase.getFavoriteDao().getAllFavorite()) {
                Log.d("favorite", f.getCatId() + "  " + f.getId() + "  " + f.getImage());
                imageItemModels.add(new ImageItemModel(String.valueOf(f.getId()), f.getCatId(), f.getImage()));
            }

            //Executed after background work had finished
            handler.post(() -> catItemImageAdapter.updateList(imageItemModels));
        });
    }


    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {
        ads.showInterstitialAds(this);
        ads.destroyBanner();
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "fav");
        startActivity(intent);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Favorite Images");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        mFirebaseAnalytics.logEvent("Clicked_On_Favorite_Items", bundle);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayAllContactInBackground();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ads.destroyBanner();

    }

    protected void onResume() {
        super.onResume();

        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);

        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}