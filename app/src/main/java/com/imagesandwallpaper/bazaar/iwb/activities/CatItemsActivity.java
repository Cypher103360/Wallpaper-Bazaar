package com.imagesandwallpaper.bazaar.iwb.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

import java.util.ArrayList;
import java.util.List;

public class CatItemsActivity extends AppCompatActivity implements SubCatImageClickInterface, CatItemImageClickInterface {
    public static List<ImageItemModel> imageItemModels = new ArrayList<>();
    SubCatImageViewModel subCatImageViewModel;
    CatItemImageViewModel catItemImageViewModel;
    ActivityCatItemsBinding binding;
    CatItemImageAdapter catItemImageAdapter;
    SubCatImageAdapter subCatImageAdapter;
    RecyclerView catItemsRecyclerView;
    ApiInterface apiInterface;
    String id, title, type;
    ShowAds ads = new ShowAds();

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
        apiInterface = ApiWebServices.getApiInterface();
        catItemsRecyclerView = binding.catItemsRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        catItemsRecyclerView.setLayoutManager(layoutManager);
        catItemsRecyclerView.setHasFixedSize(true);

        getLifecycle().addObserver(ads);

        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);

        if (type.equals("CatFragment")) {
            setCatImages();
        } else if (type.equals("SubCatItem")) {
            setSubCatImages();
        }
        binding.catItemSwipeRefresh.setOnRefreshListener(() -> {
            if (type.equals("CatFragment")) {
                setCatImages();
            } else if (type.equals("SubCatItem")) {
                setSubCatImages();
            }
            binding.catItemSwipeRefresh.setRefreshing(false);
        });
    }

    private void setSubCatImages() {
        subCatImageAdapter = new SubCatImageAdapter(CatItemsActivity.this, this);
        catItemsRecyclerView.setAdapter(subCatImageAdapter);
        subCatImageViewModel = new ViewModelProvider(CatItemsActivity.this,
                new SubCatImageModelFactory(getApplication(), id)).get(SubCatImageViewModel.class);
        subCatImageViewModel.getSubCatImageItems().observe(this, subCatImageModelList -> {
            if (!subCatImageModelList.getData().isEmpty()) {
                subCatImageAdapter.updateList(subCatImageModelList.getData());
            }
        });
    }

    private void setCatImages() {
        catItemImageAdapter = new CatItemImageAdapter(CatItemsActivity.this, this);
        catItemsRecyclerView.setAdapter(catItemImageAdapter);

        catItemImageViewModel = new ViewModelProvider(CatItemsActivity.this,
                new CatItemImageModelFactory(getApplication(), id)).get(CatItemImageViewModel.class);

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
        Ads.destroyBanner();
        Intent intent = new Intent(this, FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "catItem");
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Ads.destroyBanner();
        finish();
    }
}