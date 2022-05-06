package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CatItemClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CatItemsAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityShowImageBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemsViewModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowImageActivity extends AppCompatActivity implements CatItemClickInterface {

    CatItemsViewModel catItemsViewModel;
    List<CatItemModel> catItemModels;
    CatItemsAdapter adapter;
    GridLayoutManager layoutManager;
    ApiInterface apiInterface;
    Dialog loadingDialog;
    ActivityShowImageBinding binding;
    String catId, catKey, itemId, itemImage;
    ItemTouchHelper.SimpleCallback simpleCallback;
    Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        catId = getIntent().getStringExtra("catId");
        catKey = getIntent().getStringExtra("catKey");
        catItemsViewModel = new ViewModelProvider(this, new CatItemModelFactory(this.getApplication(), catId)).get(CatItemsViewModel.class);
        catItemModels = new ArrayList<>();
        adapter = new CatItemsAdapter(this, this);
        layoutManager = new GridLayoutManager(this, 3);
        loadingDialog = CommonMethods.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();

        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.showRV.setLayoutManager(layoutManager);
        binding.showRV.setAdapter(adapter);
        if (catKey.equals("subCat")) {
            fetchSubCatItems();
        } else if (catKey.equals("cat")) {
            fetchCatItems();
        }

    }

    private void fetchCatItems() {
        loadingDialog.show();
        catItemsViewModel.getCatItems().observe(this, catItemModelList -> {
            if (catItemModelList.getData() != null) {
                catItemModels.clear();
                catItemModels.addAll(catItemModelList.getData());
                adapter.updateList(catItemModels);
                Log.d("TAG", catItemModels.toString());
            }
            loadingDialog.dismiss();
        });

        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String itemId = catItemModels.get(viewHolder.getAdapterPosition()).getId();
                String imgPath = catItemModels.get(viewHolder.getAdapterPosition()).getImage();
            }
        };
    }

    private void fetchSubCatItems() {
        loadingDialog.show();
        catItemsViewModel.getSubCatItems().observe(this, catItemModelList -> {
            if (catItemModelList.getData() != null) {
                catItemModels.clear();
                catItemModels.addAll(catItemModelList.getData());
                adapter.updateList(catItemModels);
            }
            loadingDialog.dismiss();
        });
    }

    @Override
    public void onClicked(CatItemModel catItemModel) {
        if (catKey.equals("subCat")) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Delete this Item?")
                    .setNegativeButton("Cancel", (dialog1, which1) -> {

                    }).setPositiveButton("Delete", (dialog12, which12) -> {
                loadingDialog.show();
                itemId = catItemModel.getId();
                itemImage = catItemModel.getImage();
                map.put("id", itemId);
                map.put("title", "subCatItem");
                map.put("path", itemImage);
                deleteCategoryItems(map, "subCat");
            }).show();
        } else if (catKey.equals("cat")) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Delete this Item?")
                    .setNegativeButton("Cancel", (dialog1, which1) -> {

                    }).setPositiveButton("Delete", (dialog12, which12) -> {
                loadingDialog.show();
                itemId = catItemModel.getId();
                itemImage = catItemModel.getImage();
                map.put("id", itemId);
                map.put("title", "catItem");
                map.put("path", itemImage);
                deleteCategoryItems(map, "CatItem");
            }).show();
        }
    }

    private void deleteCategoryItems(Map<String, String> map, String type) {
        Call<MessageModel> call = apiInterface.deleteCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(ShowImageActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (type.equals("subCat")) {
                        fetchSubCatItems();
                    } else if (type.equals("CatItem")) {
                        fetchCatItems();
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }
}