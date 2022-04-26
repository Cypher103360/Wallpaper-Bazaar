package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.activities.FullscreenActivity;
import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentHomeBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ImageItemClickInterface {
    FragmentHomeBinding binding;
    RecyclerView imageItemRecyclerView;
    ImageItemAdapter imageItemAdapter;
    List<ImageItemModel> imageItemModelList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        imageItemRecyclerView = binding.imageItemRecyclerview;
        imageItemRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        imageItemRecyclerView.setHasFixedSize(true);


        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemModelList.add(new ImageItemModel(R.drawable.login_bg));
        imageItemAdapter = new ImageItemAdapter(requireActivity(),this);
        imageItemAdapter.updateList(imageItemModelList);
        imageItemRecyclerView.setAdapter(imageItemAdapter);

        return binding.getRoot();
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {
        startActivity(new Intent(requireActivity(), FullscreenActivity.class));
    }
}