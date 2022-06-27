package com.imagesandwallpaper.bazaar.iwb.fragments;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.adapters.PopNewPagerAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentHomeBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModel;
import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModelList;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    ApiInterface apiInterface;

    Map<String, String> banMap = new HashMap<>();
    Dialog loading;
    String banImage, banUrl;
    ShowAds ads = new ShowAds();
    PopNewPagerAdapter popNewPagerAdapter;
    String action;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(requireActivity());

//        ads.showTopBanner(requireActivity(), binding.adViewTop);

        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);
//            ads.showTopBanner(requireActivity(), binding.adViewTop);

        } else {
//            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }
        popNewPagerAdapter = new PopNewPagerAdapter(getChildFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        popNewPagerAdapter.addFragments(new PopularFragment(), "Popular");
        popNewPagerAdapter.addFragments(new NewFragment(), "New");

        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(popNewPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);

        action = requireActivity().getIntent().getStringExtra("action");

        banMap.put("tableName", "home_banner");
        setBannerImage(banMap);

        new Handler().postDelayed(() -> {
            if (action != null) {
                Log.d("ContentValueForPref", action);
                switch (action) {
                    case "pop":
                        binding.viewPager.setCurrentItem(0);
                        action = null;
                        break;
                    case "new":
                        binding.viewPager.setCurrentItem(1);
                        action = null;

                        break;
                    default:
                }

            }
        }, 1000);
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

                        switch (FilenameUtils.getExtension(banImage)) {
                            case "jpeg":
                            case "jpg":
                            case "png":
                                Glide.with(requireActivity()).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                                        + banImage).into(binding.homeBannerImage);
                                break;
                            case "gif":
                                Glide.with(requireActivity()).asGif().load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                                        + banImage).into(binding.homeBannerImage);
                                break;
                        }

//                        Glide.with(requireActivity())
//                                .load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + banImage)
//                                .into(binding.homeBannerImage);

                    }
                }
                binding.homeBannerImage.setOnClickListener(view -> {
                    openWebPage(banUrl);
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, banUrl);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/category_images/" + banImage);
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Home Banner");
                    mFirebaseAnalytics.logEvent("Clicked_On_home_banner", bundle);

                });
            }

            @Override
            public void onFailure(@NonNull Call<BannerModelList> call, @NonNull Throwable t) {

            }
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