package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySubCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatModel;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

public class SubCategoryActivity extends AppCompatActivity implements SubCatClickInterface {
    ActivitySubCategoryBinding binding;
    SubCatViewModel subCatViewModel;
    SubCategoryAdapter subCategoryAdapter;
    ApiInterface apiInterface;
    RecyclerView subCatItemsRecyclerview;
    String catId, activityTitle;
    Dialog loading;
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading = CommonMethods.loadingDialog(SubCategoryActivity.this);
        catId = getIntent().getStringExtra("id");
        activityTitle = getIntent().getStringExtra("title");
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.activityTitle.setText(activityTitle);

        apiInterface = ApiWebServices.getApiInterface();
        subCatItemsRecyclerview = binding.subCatRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        subCatItemsRecyclerview.setLayoutManager(layoutManager);
        subCatItemsRecyclerview.setHasFixedSize(true);
        getLifecycle().addObserver(ads);
        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);


        setData();
        binding.subCatSwipeRefresh.setOnRefreshListener(() -> {
            setData();
            ads.showTopBanner(this, binding.adViewTop);
            ads.showBottomBanner(this, binding.adViewBottom);

            binding.subCatSwipeRefresh.setRefreshing(false);
        });
    }

    private void setData() {
        loading.show();
        subCategoryAdapter = new SubCategoryAdapter(SubCategoryActivity.this, this);
        subCatItemsRecyclerview.setAdapter(subCategoryAdapter);

        subCatViewModel = new ViewModelProvider(SubCategoryActivity.this,
                new SubCatModelFactory(this.getApplication(), catId)).get(SubCatViewModel.class);

        subCatViewModel.getSubCategories().observe(this, subCatModelList -> {
            if (!subCatModelList.data.isEmpty()) {
                subCategoryAdapter.updateList(subCatModelList.getData());
                loading.dismiss();
            }
        });
    }

    @Override
    public void onClicked(SubCatModel subCatModel, int position) {

        ads.showInterstitialAds(this);
        ads.destroyBanner();
        Intent intent = new Intent(SubCategoryActivity.this, CatItemsActivity.class);
        intent.putExtra("type", "SubCatItem");
        intent.putExtra("id", subCatModel.getId());
        intent.putExtra("title", subCatModel.getTitle());
        startActivity(intent);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, subCatModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/category_images/" + subCatModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Sub Category");
        mFirebaseAnalytics.logEvent("Clicked_On_Sub_Category", bundle);

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