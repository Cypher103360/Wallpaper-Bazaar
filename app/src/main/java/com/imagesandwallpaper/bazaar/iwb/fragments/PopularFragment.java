package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentPopularBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularFragment extends Fragment implements ImageItemClickInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<ImageItemModel> imageItemModels;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView imageItemRecyclerView;
    ImageItemAdapter imageItemAdapter;
    FragmentPopularBinding binding;
    Map<String, String> map = new HashMap<>();
    ImageItemViewModel imageItemViewModel;
    Dialog loading;
    ShowAds ads = new ShowAds();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PopularFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularFragment newInstance(String param1, String param2) {
        PopularFragment fragment = new PopularFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPopularBinding.inflate(inflater, container, false);
        loading = CommonMethods.loadingDialog(requireActivity());

        swipeRefreshLayout = binding.categorySwipeRefreshLayout;
        imageItemModels = new ArrayList<>();
        imageItemRecyclerView = binding.categoryRecyclerView;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        imageItemRecyclerView.setLayoutManager(layoutManager);
        imageItemRecyclerView.setHasFixedSize(true);
        map.put("tableName", "Popular_Images");

        imageItemAdapter = new ImageItemAdapter(requireActivity(), this);
        imageItemRecyclerView.setAdapter(imageItemAdapter);
        imageItemViewModel = new ViewModelProvider(requireActivity(),
                new ImageItemModelFactory(requireActivity().getApplication(), map)).get(ImageItemViewModel.class);

        setImageData();
        setImageData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setImageData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return binding.getRoot();
    }

    private void setImageData() {

        imageItemViewModel.getImageItems().observe(requireActivity(), imageItemModelList -> {
            if (!imageItemModelList.getData().isEmpty()) {
                imageItemModels.clear();
//                Log.d("ContentValue", String.valueOf(imageItemModelList.getData()));

                imageItemModels.addAll(imageItemModelList.getData());
                imageItemAdapter.updateList(imageItemModels);
            }
            loading.dismiss();
        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Popular Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Popular_Images", bundle);

        Intent intent = new Intent(requireActivity(), FullscreenActivity.class);
        intent.putExtra("id", imageItemModel.getId());
        intent.putExtra("catId", imageItemModel.getCatId());
        intent.putExtra("img", imageItemModel.getImage());
        intent.putExtra("pos", String.valueOf(position));
        intent.putExtra("key", "pop");
        startActivity(intent);
        ads.showInterstitialAds(requireActivity());
//        ads.destroyBanner();
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