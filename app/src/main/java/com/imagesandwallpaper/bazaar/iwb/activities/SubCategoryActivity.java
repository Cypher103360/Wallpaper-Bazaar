package com.imagesandwallpaper.bazaar.iwb.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.CategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivitySubCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatModelList;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends AppCompatActivity implements CatClickInterface {
    ActivitySubCategoryBinding binding;
    CategoryAdapter categoryAdapter;
    ApiInterface apiInterface;
    RecyclerView subCatItemsRecyclerview;
    String catId,activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        catId = getIntent().getStringExtra("id");
        activityTitle = getIntent().getStringExtra("title");
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.activityTitle.setText(activityTitle);


        apiInterface = ApiWebServices.getApiInterface();
        subCatItemsRecyclerview = binding.subCatRecyclerView;
        subCatItemsRecyclerview.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,3));
        subCatItemsRecyclerview.setHasFixedSize(true);

        setData();
    }

    private void setData() {
        categoryAdapter = new CategoryAdapter(SubCategoryActivity.this,this);
        subCatItemsRecyclerview.setAdapter(categoryAdapter);
        Call<CatModelList> call = apiInterface.getSubCategories(catId);
        call.enqueue(new Callback<CatModelList>() {
            @Override
            public void onResponse(@NonNull Call<CatModelList> call, @NonNull Response<CatModelList> response) {
                if (response.isSuccessful()){
                    categoryAdapter.updateList(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatModelList> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClicked(CategoryModel categoryModel) {
        Intent intent = new Intent(SubCategoryActivity.this,CatItemsActivity.class);
        intent.putExtra("id",categoryModel.getId());
        intent.putExtra("title",categoryModel.getTitle());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}