package com.imagesandwallpaper.bazaar.iwb.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.iwb.adapters.CatItemImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.adapters.SubCatImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityCatItemsBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModel;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelList;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageViewModel;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelList;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatImageModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatImageModelList;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatImageViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatItemsActivity extends AppCompatActivity implements SubCatImageClickInterface, CatItemImageClickInterface {
    SubCatImageViewModel subCatImageViewModel;
    CatItemImageViewModel catItemImageViewModel;
    ActivityCatItemsBinding binding;
    CatItemImageAdapter catItemImageAdapter;
    SubCatImageAdapter subCatImageAdapter;
    RecyclerView catItemsRecyclerView;
    ApiInterface apiInterface;
    String id,title,type;

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
        catItemsRecyclerView.setLayoutManager(new GridLayoutManager(CatItemsActivity.this, 3));
        catItemsRecyclerView.setHasFixedSize(true);

        if (type.equals("CatFragment")){
            setCatImages();
        }else if (type.equals("SubCatItem")){
            setSubCatImages();
        }
    }

    private void setSubCatImages() {
        subCatImageAdapter = new SubCatImageAdapter(CatItemsActivity.this,this);
        catItemsRecyclerView.setAdapter(subCatImageAdapter);
        subCatImageViewModel = new ViewModelProvider(CatItemsActivity.this,
                new SubCatImageModelFactory(getApplication(),id)).get(SubCatImageViewModel.class);
        subCatImageViewModel.getSubCatImageItems().observe(this, subCatImageModelList -> {
            if (!subCatImageModelList.getData().isEmpty()){
                subCatImageAdapter.updateList(subCatImageModelList.getData());
            }
        });
    }

    private void setCatImages() {
        catItemImageAdapter = new CatItemImageAdapter(CatItemsActivity.this, this);
        catItemsRecyclerView.setAdapter(catItemImageAdapter);

        catItemImageViewModel = new ViewModelProvider(CatItemsActivity.this,
                new CatItemImageModelFactory(getApplication(),id)).get(CatItemImageViewModel.class);

        catItemImageViewModel.getCatItemImages().observe(this, catItemImageModelList -> {
            if (!catItemImageModelList.getData().isEmpty()){
                catItemImageAdapter.updateList(catItemImageModelList.getData());
            }
        });

//        Call<CatItemImageModelList> call = apiInterface.getCatItemImages(id);
//        call.enqueue(new Callback<CatItemImageModelList>() {
//            @Override
//            public void onResponse(@NonNull Call<CatItemImageModelList> call, @NonNull Response<CatItemImageModelList> response) {
//                if (response.isSuccessful()){
//                    assert response.body() != null;
//                    if (response.body().getData() != null){
//                        catItemImageAdapter.updateList(response.body().getData());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<CatItemImageModelList> call, @NonNull Throwable t) {
//
//            }
//        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {

    }

    @Override
    public void onClicked(CatItemImageModel imageItemModel) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}