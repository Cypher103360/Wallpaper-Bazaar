package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityPopAndPremiumBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ImageItemModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ImageItemViewModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PopAndPremiumActivity extends AppCompatActivity implements ImageItemClickInterface {
    ActivityPopAndPremiumBinding binding;
    RecyclerView popPremiumRV;
    Dialog loading;
    ApiInterface apiInterface;
    List<ImageItemModel> imageItemModels;
    String type, itemId, itemImage;
    ImageItemAdapter imageItemAdapter;
    ImageItemViewModel imageItemViewModel;
    Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPopAndPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading = CommonMethods.loadingDialog(this);
        popPremiumRV = binding.popPremiumRecyclerView;
        apiInterface = ApiWebServices.getApiInterface();
        type = getIntent().getStringExtra("type");
        popPremiumRV.setLayoutManager(new GridLayoutManager(this, 3));
        popPremiumRV.setHasFixedSize(true);
        imageItemModels = new ArrayList<>();

        switch (type) {
            case "popular":
                loading.show();
                map.put("tableName", "Popular_Images");
                fetchPopular(this, map);
                break;
            case "premium":
                loading.show();
                map.put("tableName", "Premium_Images");
                fetchPremium(this, map);
                break;
            case "live":
                loading.show();
                map.put("tableName", "live_wallpaper");
                fetchPremium(this, map);
                break;
        }

    }

    private void fetchPopular(Activity context, Map<String, String> map) {
        imageItemAdapter = new ImageItemAdapter(context, this);
        popPremiumRV.setAdapter(imageItemAdapter);
        imageItemViewModel = new ViewModelProvider(this,
                new ImageItemModelFactory(this.getApplication(), map)).get(ImageItemViewModel.class);

        imageItemViewModel.getImageItems().observe(this, imageItemModelList -> {
            if (!imageItemModelList.getData().isEmpty()) {
                imageItemModels.clear();
                imageItemModels.addAll(imageItemModelList.getData());
                imageItemAdapter.updateList(imageItemModels);
            }
            loading.dismiss();
        });
    }

    private void fetchPremium(Activity context, Map<String, String> map) {
        loading.show();
        imageItemAdapter = new ImageItemAdapter(context, this);
        popPremiumRV.setAdapter(imageItemAdapter);
        imageItemViewModel = new ViewModelProvider(this,
                new ImageItemModelFactory(this.getApplication(), map)).get(ImageItemViewModel.class);

        imageItemViewModel.getPremiumImageItems().observe(this, premiumModelList -> {
            if (!premiumModelList.getData().isEmpty()) {
                imageItemModels.clear();
                imageItemModels.addAll(premiumModelList.getData());
                imageItemAdapter.updateList(imageItemModels);
            }
            loading.dismiss();
        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {
        switch (type) {
            case "popular": {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Delete this Item?")
                        .setNegativeButton("Cancel", (dialog1, which1) -> {

                        })
                        .setPositiveButton("Delete", (dialog12, which12) -> {
                            loading.show();
                            itemId = imageItemModel.getId();
                            itemImage = imageItemModel.getImage();
                            map.put("id", itemId);
                            map.put("title", "popular");
                            map.put("path", "all_images/" + itemImage);
                            deletePopPremiumImages(map, "popular");
                        }).show();
                break;
            }
            case "premium": {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Delete this Item?")
                        .setNegativeButton("Cancel", (dialog1, which1) -> {

                        })
                        .setPositiveButton("Delete", (dialog12, which12) -> {
                            loading.show();
                            itemId = imageItemModel.getId();
                            itemImage = imageItemModel.getImage();
                            map.put("id", itemId);
                            map.put("title", "premium");
                            map.put("path", "all_images/" + itemImage);
                            deletePopPremiumImages(map, "premium");
                        }).show();
                break;
            }
            case "live": {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Delete this Item?")
                        .setNegativeButton("Cancel", (dialog1, which1) -> {

                        })
                        .setPositiveButton("Delete", (dialog12, which12) -> {
                            loading.show();
                            itemId = imageItemModel.getId();
                            itemImage = imageItemModel.getImage();
                            map.put("id", itemId);
                            map.put("title", "live");
                            map.put("path", "live_wallpapers/" + itemImage);
                            deletePopPremiumImages(map, "live");
                        }).show();
                break;
            }
        }
    }

    private void deletePopPremiumImages(Map<String, String> map, String type) {
        Call<MessageModel> call = apiInterface.deleteCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(PopAndPremiumActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (type.equals("popular")) {
                        loading.show();
                        map.put("tableName", "Popular_Images");
                        fetchPopular(PopAndPremiumActivity.this, map);
                    } else if (type.equals("premium")) {
                        loading.show();
                        map.put("tableName", "Premium_Images");
                        fetchPremium(PopAndPremiumActivity.this, map);
                    } else if (type.equals("live")) {
                        loading.show();
                        map.put("tableName", "live_wallpaper");
                        fetchPremium(PopAndPremiumActivity.this, map);
                    }
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }
}