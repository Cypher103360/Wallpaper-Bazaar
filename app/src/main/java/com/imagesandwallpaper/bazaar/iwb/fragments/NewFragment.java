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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewFragment extends Fragment implements ImageItemClickInterface, CatItemImageClickInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<ImageItemModel> imageItemModels;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView imageItemRecyclerView;
    FragmentNewBinding binding;
    Map<String, String> map = new HashMap<>();
    CatItemImageViewModel catItemImageViewModel;
    Dialog loading;
    CatItemImageAdapter catItemImageAdapter;
    String id;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ShowAds ads =  new ShowAds();

    public NewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewFragment newInstance(String param1, String param2) {
        NewFragment fragment = new NewFragment();
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