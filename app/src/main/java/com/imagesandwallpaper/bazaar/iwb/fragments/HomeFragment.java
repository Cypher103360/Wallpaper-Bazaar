package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentHomeBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModel;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModelList;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ImageItemClickInterface {
    ImageItemViewModel imageItemViewModel;
    FragmentHomeBinding binding;
    RecyclerView imageItemRecyclerView;
    ImageItemAdapter imageItemAdapter;
    MaterialButtonToggleGroup materialButtonToggleGroup;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialButton popularBtn, newBtn;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    Map<String, String> banMap = new HashMap<>();
    Dialog loading;
    String banImage, banUrl;
    List<ImageItemModel> imageItemModels;


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        materialButtonToggleGroup = binding.materialButtonToggleGroup;
        popularBtn = binding.popBtn;
        newBtn = binding.newImgBtn;
        swipeRefreshLayout = binding.homeSwipeRefresh;
        imageItemModels = new ArrayList<>();

        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(requireActivity());

        imageItemRecyclerView = binding.imageItemRecyclerview;
        imageItemRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        imageItemRecyclerView.setHasFixedSize(true);

        map.put("tableName", "Popular_Images");
        setImageData(requireActivity(), map);
        banMap.put("tableName", "home_banner");
        setBannerImage(banMap);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            materialButtonToggleGroup.check(R.id.pop_btn);

            map.put("tableName", "Popular_Images");
            setImageData(requireActivity(), map);

            banMap.put("tableName", "home_banner");
            setBannerImage(banMap);
            swipeRefreshLayout.setRefreshing(false);
        });


        materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.pop_btn:
                        loading.show();
                        map.put("tableName", "Popular_Images");
                        setImageData(requireActivity(), map);
                        break;
                    case R.id.new_img_btn:
                        loading.show();
                        map.put("tableName", "New_Images");
                        setImageData(requireActivity(), map);
                        break;
                }
            }
        });

        return binding.getRoot();
    }

    private void setBannerImage(Map<String, String> banMap) {
        Call<BannerModelList> call = apiInterface.getBanners(banMap);
        call.enqueue(new Callback<BannerModelList>() {
            @Override
            public void onResponse(@NonNull Call<BannerModelList> call, @NonNull Response<BannerModelList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    for (BannerModel bannerModel : response.body().getData()) {
                        banImage = bannerModel.getImage();
                        banUrl = bannerModel.getUrl();

                        Glide.with(requireActivity())
                                .load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + banImage)
                                .into(binding.homeBannerImage);

                    }
                }
                binding.homeBannerImage.setOnClickListener(view -> {
                    openWebPage(banUrl);
                    Toast.makeText(requireActivity(), "Home banner", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(@NonNull Call<BannerModelList> call, @NonNull Throwable t) {

            }
        });
    }

    private void setImageData(Activity context, Map<String, String> map) {
        imageItemAdapter = new ImageItemAdapter(context, this);
        imageItemRecyclerView.setAdapter(imageItemAdapter);
        imageItemViewModel = new ViewModelProvider(requireActivity(),
                new ImageItemModelFactory(requireActivity().getApplication(), map)).get(ImageItemViewModel.class);

        imageItemViewModel.getImageItems().observe(requireActivity(), imageItemModelList -> {
            if (!imageItemModelList.getData().isEmpty()) {
                imageItemModels.clear();
                imageItemModels.addAll(imageItemModelList.getData());
                imageItemAdapter.updateList(imageItemModels);
            }
            loading.dismiss();
        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {
        //Intent intent = new Intent(requireActivity(), FullscreenActivity.class);
//        List<ImageItemModel> itemModels = new ArrayList<>();
//        itemModels.add(imageItemModel);
//        intent.putExtra("myList", (Serializable) itemModels);
        // startActivity(intent);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}