package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.imagesandwallpaper.bazaar.iwb.adapters.CatItemImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFavoriteBinding;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoriteActivity extends AppCompatActivity implements CatItemImageClickInterface {
    CatItemImageAdapter catItemImageAdapter;

    RecyclerView catItemsRecyclerView;
    ActivityFavoriteBinding binding;
    public static List<ImageItemModel> imageItemModels;
    FavoriteAppDatabase favoriteAppDatabase;
    ShowAds ads = new ShowAds();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        catItemsRecyclerView = binding.catItemsRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        catItemsRecyclerView.setLayoutManager(layoutManager);
        catItemsRecyclerView.setHasFixedSize(true);
        catItemImageAdapter = new CatItemImageAdapter(this, this);
        catItemsRecyclerView.setAdapter(catItemImageAdapter);
        binding.backIcon.setOnClickListener(view -> onBackPressed());
        getLifecycle().addObserver(ads);
        imageItemModels = new ArrayList<>();

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            // Background work
            ads.showTopBanner(this, binding.adViewTop);
            ads.showBottomBanner(this, binding.adViewBottom);
        });

        binding.catItemSwipeRefresh.setOnRefreshListener(() -> {
            displayAllContactInBackground();
            binding.catItemSwipeRefresh.setRefreshing(false);
        });

    }

    private void displayAllContactInBackground() {
        imageItemModels.clear();

        favoriteAppDatabase = Room.databaseBuilder(
                this,
                FavoriteAppDatabase.class
                , "FavoriteDB")
                .build();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(() -> {
            // Background work
            for (Favorite f : favoriteAppDatabase.getFavoriteDao().getAllFavorite()) {
                imageItemModels.add(new ImageItemModel(String.valueOf(f.getId()), f.getCatId(), f.getImage()));
            }

            //Executed after background work had finished
            handler.post(() -> catItemImageAdapter.updateList(imageItemModels));
        });
    }


    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {
        ads.showInterstitialAds(this);
        Ads.destroyBanner();
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "fav");
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayAllContactInBackground();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Ads.destroyBanner();

    }
}