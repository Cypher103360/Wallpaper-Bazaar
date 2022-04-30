package com.imagesandwallpaper.bazaar.iwb.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityCatItemsBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatItemsActivity extends AppCompatActivity implements ImageItemClickInterface {
    ActivityCatItemsBinding binding;
    ImageItemAdapter imageItemAdapter;
    RecyclerView catItemsRecyclerView;
    ApiInterface apiInterface;
    String id,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        binding.backIcon.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.activityTitle.setText(title);
        apiInterface = ApiWebServices.getApiInterface();
        catItemsRecyclerView = binding.catItemsRecyclerView;
        catItemsRecyclerView.setLayoutManager(new GridLayoutManager(CatItemsActivity.this, 3));
        catItemsRecyclerView.setHasFixedSize(true);

        setData();
    }

    private void setData() {
        imageItemAdapter = new ImageItemAdapter(CatItemsActivity.this,this);
        catItemsRecyclerView.setAdapter(imageItemAdapter);
        Call<ImageItemModelList> call = apiInterface.getCatItemImages(id);
        call.enqueue(new Callback<ImageItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<ImageItemModelList> call, @NonNull Response<ImageItemModelList> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (response.body().getData() != null){
                        imageItemAdapter.updateList(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageItemModelList> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}