package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.activities.FullscreenActivity;
import com.imagesandwallpaper.bazaar.iwb.adapters.CatItemImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentNewBinding;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageViewModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFragment extends Fragment implements ImageItemClickInterface, CatItemImageClickInterface {

    public static List<ImageItemModel> imageItemModels;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView imageItemRecyclerView;
    FragmentNewBinding binding;
    Map<String, String> map = new HashMap<>();
    CatItemImageViewModel catItemImageViewModel;
    Dialog loading;
    CatItemImageAdapter catItemImageAdapter;
    String id;
    ShowAds ads =  new ShowAds();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewBinding.inflate(inflater, container, false);

        swipeRefreshLayout = binding.categorySwipeRefreshLayout;
        imageItemModels = new ArrayList<>();
        loading = CommonMethods.loadingDialog(requireActivity());
        imageItemRecyclerView = binding.categoryRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        imageItemRecyclerView.setLayoutManager(layoutManager);
        imageItemRecyclerView.setHasFixedSize(true);

        getLifecycle().addObserver(ads);
        id = "New_Images";
        catItemImageViewModel = new ViewModelProvider(this,
                new CatItemImageModelFactory(requireActivity().getApplication(), id)).get(CatItemImageViewModel.class);

        catItemImageAdapter = new CatItemImageAdapter(requireActivity(), this);
        imageItemRecyclerView.setAdapter(catItemImageAdapter);


        setImageData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            setImageData();
            swipeRefreshLayout.setRefreshing(false);
        });
        return binding.getRoot();
    }

    private void setImageData() {
        catItemImageViewModel.getNewImageItems().observe(requireActivity(), imageItemModelList -> {
            if (!imageItemModelList.getData().isEmpty()) {
                imageItemModels.clear();
                imageItemModels.addAll(imageItemModelList.getData());
                catItemImageAdapter.updateList(imageItemModels);
            }
            loading.dismiss();
        });
    }


    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "New Images");
        mFirebaseAnalytics.logEvent("Clicked_On_New_Images", bundle);

        ads.showInterstitialAds(requireActivity());
        ads.destroyBanner();
        Intent intent = new Intent(requireActivity(), FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "new");
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

    @Override
    public void onClicked() {

    }
}