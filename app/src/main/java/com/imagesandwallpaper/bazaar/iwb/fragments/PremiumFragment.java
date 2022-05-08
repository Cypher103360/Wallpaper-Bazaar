package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.activities.FullscreenActivity;
import com.imagesandwallpaper.bazaar.iwb.adapters.PremiumAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentPremiumBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModel;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModelList;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumFragment extends Fragment implements PremiumClickInterface {
    public static List<ImageItemModel> premiumModels;
    PremiumViewModel premiumViewModel;
    RecyclerView premiumRecyclerView;
    PremiumAdapter premiumAdapter;
    Dialog loading;
    FragmentPremiumBinding binding;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    Map<String, String> banMap = new HashMap<>();
    String banImage, banUrl;
    ShowAds ads = new ShowAds();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPremiumBinding.inflate(inflater, container, false);
        loading = CommonMethods.loadingDialog(requireActivity());
        apiInterface = ApiWebServices.getApiInterface();

        premiumRecyclerView = binding.premiumRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        premiumRecyclerView.setLayoutManager(layoutManager);
//        premiumRecyclerView.setHasFixedSize(true);


        getLifecycle().addObserver(ads);
//        ads.showTopBanner(requireActivity(), binding.adViewTop);

        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);

        } else {
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }
        premiumModels = new ArrayList<>();

        return binding.getRoot();

    }


    private void setImageData(Activity context, Map<String, String> map) {
        loading.show();
        premiumAdapter = new PremiumAdapter(context, this);
        premiumRecyclerView.setAdapter(premiumAdapter);
        premiumViewModel = new ViewModelProvider(requireActivity(),
                new PremiumModelFactory(requireActivity().getApplication(), map)).get(PremiumViewModel.class);

        premiumViewModel.getPremiumImages().observe(requireActivity(), premiumModelList -> {
            if (!premiumModelList.getData().isEmpty()) {
                premiumModels.clear();
                premiumModels.addAll(premiumModelList.getData());
                premiumAdapter.updateList(premiumModels);
            }
            loading.dismiss();
        });
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
                                .into(binding.premiumBannerImage);
                    }

                    binding.premiumBannerImage.setOnClickListener(view -> {
                        openWebPage(banUrl);
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call<BannerModelList> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClicked(ImageItemModel premiumModel, int position) {
        ads.showInterstitialAds(requireActivity());
        ads.destroyBanner();
        Intent intent = new Intent(requireActivity(), FullscreenActivity.class);
        intent.putExtra("id", premiumModel.getId());
        intent.putExtra("catId", premiumModel.getCatId());
        intent.putExtra("img", premiumModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "premium");
        startActivity(intent);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + premiumModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Home Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Premium_Images", bundle);

    }

    @Override
    public void onStart() {
        super.onStart();
        map.put("tableName", "Premium_Images");
        setImageData(requireActivity(), map);

        banMap.put("tableName", "premium_banner");
        setBannerImage(banMap);

        binding.premiumSwipeRefreshLayout.setOnRefreshListener(() -> {
            map.put("tableName", "Premium_Images");
            setImageData(requireActivity(), map);

            banMap.put("tableName", "premium_banner");
            setBannerImage(banMap);

            binding.premiumSwipeRefreshLayout.setRefreshing(false);
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IronSource.onResume(requireActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        IronSource.onPause(requireActivity());
    }
}