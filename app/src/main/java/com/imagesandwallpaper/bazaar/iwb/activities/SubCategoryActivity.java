package com.imagesandwallpaper.bazaar.iwb.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySubCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatModel;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

public class SubCategoryActivity extends AppCompatActivity implements SubCatClickInterface {
    ActivitySubCategoryBinding binding;
    SubCatViewModel subCatViewModel;
    SubCategoryAdapter subCategoryAdapter;
    ApiInterface apiInterface;
    RecyclerView subCatItemsRecyclerview;
    String catId, activityTitle;
    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
        subCatItemsRecyclerview.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this, 3));
        subCatItemsRecyclerview.setHasFixedSize(true);

        setData();
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
    public void onClicked(SubCatModel subCatModel) {
        Intent intent = new Intent(SubCategoryActivity.this, CatItemsActivity.class);
        intent.putExtra("type","SubCatItem");
        intent.putExtra("id", subCatModel.getCatId());
        intent.putExtra("title", subCatModel.getTitle());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}