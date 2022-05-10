package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.adapters.CatItemImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityCatItemsBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageViewModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatImageModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatImageViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatItemsActivity extends AppCompatActivity implements SubCatImageClickInterface, CatItemImageClickInterface {
    public static List<ImageItemModel> imageItemModels;
    SubCatImageViewModel subCatImageViewModel;
    ActivityCatItemsBinding binding;
    CatItemImageViewModel catItemImageViewModel;
    CatItemImageAdapter catItemImageAdapter;
    SubCatImageAdapter subCatImageAdapter;
    RecyclerView catItemsRecyclerView;
    ApiInterface apiInterface;
    String id, title, type;
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.activityTitle.setText(title);
        imageItemModels = new ArrayList<>();
        apiInterface = ApiWebServices.getApiInterface();
        catItemsRecyclerView = binding.catItemsRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        catItemsRecyclerView.setLayoutManager(layoutManager);
        getLifecycle().addObserver(ads);
        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);

        catItemImageAdapter = new CatItemImageAdapter(this, this);
        catItemsRecyclerView.setAdapter(catItemImageAdapter);

        catItemImageViewModel = new ViewModelProvider(this,
                new CatItemImageModelFactory(getApplication(), id)).get(CatItemImageViewModel.class);


        subCatImageAdapter = new SubCatImageAdapter(this, this);
        catItemsRecyclerView.setAdapter(subCatImageAdapter);
        subCatImageViewModel = new ViewModelProvider(this,
                new SubCatImageModelFactory(getApplication(), id)).get(SubCatImageViewModel.class);


        if (type.equals("CatFragment")) {
            setCatImages();
        } else if (type.equals("SubCatItem")) {
            setSubCatImages();
        }
        binding.catItemSwipeRefresh.setOnRefreshListener(() -> {
            ads.showTopBanner(this, binding.adViewTop);
            ads.showBottomBanner(this, binding.adViewBottom);

            if (type.equals("CatFragment")) {
                setCatImages();
            } else if (type.equals("SubCatItem")) {
                setSubCatImages();
            }
            binding.catItemSwipeRefresh.setRefreshing(false);
        });
    }

    private void setSubCatImages() {
        subCatImageViewModel.getSubCatImageItems().observe(this, subCatImageModelList -> {
            if (subCatImageModelList.getData()!=null) {
                imageItemModels.clear();
                imageItemModels.addAll(subCatImageModelList.getData());

                subCatImageAdapter.updateList(imageItemModels);
            }
        });
    }

    private void setCatImages() {
          catItemImageViewModel.getCatItemImages().observe(this, catItemImageModelList -> {
            if (!catItemImageModelList.getData().isEmpty()) {
                imageItemModels.clear();
                imageItemModels.addAll(catItemImageModelList.getData());
                catItemImageAdapter.updateList(imageItemModels);
            }
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
        intent.putExtra("key", "catItem");
        startActivity(intent);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Cat Item Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Cat_Items", bundle);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ads.destroyBanner();
        finish();
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