package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.imagesandwallpaper.bazaar.iwb.activities.CatItemsActivity;
import com.imagesandwallpaper.bazaar.iwb.activities.SubCategoryActivity;
import com.imagesandwallpaper.bazaar.iwb.adapters.CategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatViewModel;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CategoryFragment extends Fragment implements CatClickInterface {
    FragmentCategoryBinding binding;
    RecyclerView catRecyclerView;
    CategoryAdapter categoryAdapter;
    ApiInterface apiInterface;
    CatViewModel catViewModel;
    Dialog loading;
    SwipeRefreshLayout swipeRefreshLayout;
    List<CategoryModel> categoryModels;
    ShowAds ads = new ShowAds();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(requireActivity());
        loading.show();
        categoryModels = new ArrayList<>();
        swipeRefreshLayout = binding.categorySwipeRefreshLayout;
        catRecyclerView = binding.categoryRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        catRecyclerView.setLayoutManager(layoutManager);
        catRecyclerView.setHasFixedSize(true);
        getLifecycle().addObserver(ads);

        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);

        } else {
            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }
        categoryAdapter = new CategoryAdapter(requireActivity(), this);
        catRecyclerView.setAdapter(categoryAdapter);

        setCategoryData(requireActivity());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setCategoryData(requireActivity());
            swipeRefreshLayout.setRefreshing(false);
        });

        return binding.getRoot();
    }

    private void setCategoryData(FragmentActivity context) {
        catViewModel = new ViewModelProvider(context).get(CatViewModel.class);
        catViewModel.getCategories().observe(requireActivity(), catModelList -> {
            if (!catModelList.getData().isEmpty()) {
                categoryModels.clear();
                categoryModels.addAll(catModelList.getData());
                categoryAdapter.updateList(categoryModels);
            }
            loading.dismiss();
        });
    }

    @Override
    public void onClicked(CategoryModel categoryModel, int position) {
        ads.showInterstitialAds(requireActivity());
        Ads.destroyBanner();

        if (categoryModel.getSubCat().equals("true")) {
            Intent intent = new Intent(requireActivity(), SubCategoryActivity.class);
            intent.putExtra("id", categoryModel.getId());
            intent.putExtra("title", categoryModel.getTitle());
            startActivity(intent);
        } else if (categoryModel.getItem().equals("true")) {
            Intent intent = new Intent(requireActivity(), CatItemsActivity.class);
            intent.putExtra("type", "CatFragment");
            intent.putExtra("id", categoryModel.getId());
            intent.putExtra("title", categoryModel.getTitle());
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), "No wallpapers available", Toast.LENGTH_SHORT).show();
        }
    }
}