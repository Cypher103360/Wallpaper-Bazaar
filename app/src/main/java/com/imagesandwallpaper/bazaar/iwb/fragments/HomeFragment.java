package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.activities.FullscreenActivity;
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
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ImageItemClickInterface {
    public static List<ImageItemModel> imageItemModels;
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
    ShowAds ads = new ShowAds();

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

        getLifecycle().addObserver(ads);
//        ads.showTopBanner(requireActivity(), binding.adViewTop);

        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);

        } else {
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        }
        imageItemRecyclerView = binding.imageItemRecyclerview;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        imageItemRecyclerView.setLayoutManager(layoutManager);
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popularBtn.setStrokeColor(requireActivity().getColorStateList(R.color.btn_color));
                            newBtn.setStrokeColor(requireActivity().getColorStateList(R.color.white));

                        }
                        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
                            binding.adViewTop.setVisibility(View.GONE);

                        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
                            binding.adViewBottom.setVisibility(View.GONE);

                        } else {
                            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

                        }
                        map.put("tableName", "Popular_Images");
                        setImageData(requireActivity(), map);
                        break;
                    case R.id.new_img_btn:
                        loading.show();
                        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
                            binding.adViewTop.setVisibility(View.GONE);

                        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
                            binding.adViewBottom.setVisibility(View.GONE);

                        } else {
                            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            newBtn.setStrokeColor(requireActivity().getColorStateList(R.color.btn_color));
                            popularBtn.setStrokeColor(requireActivity().getColorStateList(R.color.white));

                        }
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
    public void onClicked(ImageItemModel imageItemModel, int position) {
        ads.showInterstitialAds(requireActivity());
        Ads.destroyBanner();
        Intent intent = new Intent(requireActivity(), FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "home");
        startActivity(intent);
    }


    @Override
    public void onShareImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

    }

    @Override
    public void onDownloadImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

    }

    @Override
    public void onFavoriteImg(ImageItemModel imageItemModel, int position, ImageView favoriteIcon) {

    }

    @Override
    public void onSetImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

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