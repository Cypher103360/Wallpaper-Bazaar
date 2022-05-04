package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CatItemClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CatItemsAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityShowImageBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemsViewModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class ShowImageActivity extends AppCompatActivity implements CatItemClickInterface {

    CatItemsViewModel catItemsViewModel;
    List<CatItemModel> catItemModels;
    CatItemsAdapter adapter;
    GridLayoutManager layoutManager;
    ApiInterface apiInterface;
    Dialog loadingDialog;
    ActivityShowImageBinding binding;
    String catId, catKey;

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
        if (catKey.equals("subCat")){
            fetchSubCatItems();
        }else if (catKey.equals("cat")){
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
    }private void fetchSubCatItems() {
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
    public void onClicked(CatItemModel categoryModel) {



    }
}