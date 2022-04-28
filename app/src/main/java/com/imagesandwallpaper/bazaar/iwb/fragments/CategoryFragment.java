package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.CategoryAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentCategoryBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.CatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CatModelList;
import com.imagesandwallpaper.bazaar.iwb.models.CatViewModel;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CatClickInterface {
    FragmentCategoryBinding binding;
    RecyclerView catRecyclerView;
    CategoryAdapter categoryAdapter;
    ApiInterface apiInterface;
    CatViewModel catViewModel;
    Dialog loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater,container,false);
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(requireActivity());
        loading.show();
        catRecyclerView = binding.categoryRecyclerView;
        catRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),3));
        catRecyclerView.setHasFixedSize(true);

        categoryAdapter = new CategoryAdapter(requireActivity(),this);
        catRecyclerView.setAdapter(categoryAdapter);
        //categoryAdapter.updateList(categoryModelList);

        setCategoryData();
        return binding.getRoot();
    }

    private void setCategoryData() {
        catViewModel = new ViewModelProvider(requireActivity()).get(CatViewModel.class);
        catViewModel.getCategories().observe(requireActivity(), catModelList -> {
            if (!catModelList.getData().isEmpty()){
                categoryAdapter.updateList(catModelList.getData());
            }
            loading.dismiss();
        });
    }

    @Override
    public void onClicked(CategoryModel categoryModel) {

    }
}