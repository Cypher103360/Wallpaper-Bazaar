package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.CategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.CatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CatClickInterface {
    FragmentCategoryBinding binding;
    RecyclerView catRecyclerView;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater,container,false);
        catRecyclerView = binding.categoryRecyclerView;
        catRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        catRecyclerView.setHasFixedSize(true);

        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryModelList.add(new CategoryModel(R.drawable.ic_launcher_foreground,"132","Abstract"));
        categoryAdapter = new CategoryAdapter(requireActivity(),this);
        categoryAdapter.updateList(categoryModelList);
        catRecyclerView.setAdapter(categoryAdapter);



        return binding.getRoot();
    }

    @Override
    public void onClicked(CategoryModel categoryModel) {

    }
}