package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentOtherBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelFactory;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherFragment extends Fragment implements ImageItemClickInterface {
    ImageItemViewModel imageItemViewModel;
    RecyclerView imageItemRecyclerView;
    ImageItemAdapter imageItemAdapter;
    Dialog loading;
    FragmentOtherBinding binding;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    List<ImageItemModel> imageItemModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtherBinding.inflate(inflater, container, false);
        loading = CommonMethods.loadingDialog(requireActivity());
        apiInterface = ApiWebServices.getApiInterface();

        imageItemRecyclerView = binding.imageItemRecyclerview;
        imageItemRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        imageItemRecyclerView.setHasFixedSize(true);

        imageItemModels = new ArrayList<>();

        return binding.getRoot();

    }


    private void setImageData(Activity context, Map<String, String> map) {
        loading.show();
        imageItemAdapter = new ImageItemAdapter(context, this);
        imageItemRecyclerView.setAdapter(imageItemAdapter);
        imageItemViewModel = new ViewModelProvider(requireActivity(),
                new ImageItemModelFactory(requireActivity().getApplication(), map)).get(ImageItemViewModel.class);

        imageItemViewModel.getPremiumImageItems().observe(requireActivity(), imageItemModelList -> {
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

    }

    @Override
    public void onStart() {
        super.onStart();
        map.put("tableName", "Premium_Images");
        setImageData(requireActivity(), map);
    }
}