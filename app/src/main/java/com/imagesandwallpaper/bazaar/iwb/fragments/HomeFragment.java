package com.imagesandwallpaper.bazaar.iwb.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.activities.FullscreenActivity;
import com.imagesandwallpaper.bazaar.iwb.adapters.ImageItemAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.FragmentHomeBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModelList;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemViewModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ImageItemClickInterface {
    FragmentHomeBinding binding;
    RecyclerView imageItemRecyclerView;
    ImageItemAdapter imageItemAdapter;
    List<ImageItemModel> imageItemModelList = new ArrayList<>();
    MaterialButtonToggleGroup materialButtonToggleGroup;
    Button popularBtn, newBtn;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    Dialog loading;


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        //CommonMethods.loadingDialog(requireActivity()).show();
        materialButtonToggleGroup = binding.materialButtonToggleGroup;
        popularBtn = binding.popBtn;
        newBtn = binding.newBtn;
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.loadingDialog(requireActivity());

        imageItemRecyclerView = binding.imageItemRecyclerview;
        imageItemRecyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        imageItemRecyclerView.setHasFixedSize(true);

        map.put("tableName", "Popular_Images");
        setImageData(requireActivity(), map);


        materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.pop_btn:
                        loading.show();
                        map.put("tableName", "Popular_Images");
                        setImageData(requireActivity(), map);
                        break;
                    case R.id.new_btn:
                        loading.show();
                        map.put("tableName", "New_Images");
                        setImageData(requireActivity(), map);
                        break;
                }
            }
        });
        return binding.getRoot();
    }

    private void setImageData(FragmentActivity context, Map<String, String> map) {
        imageItemAdapter = new ImageItemAdapter(context, this);
        imageItemRecyclerView.setAdapter(imageItemAdapter);

        Call<ImageItemModelList> call = apiInterface.getPopularImageItem(map);
        call.enqueue(new Callback<ImageItemModelList>() {
            @Override
            public void onResponse(@NonNull Call<ImageItemModelList> call, @NonNull Response<ImageItemModelList> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {
                        imageItemAdapter.updateList(response.body().getData());
                    }
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ImageItemModelList> call, @NonNull Throwable t) {
                Log.e("Tag", t.getMessage());
            }
        });
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel) {
        Intent intent = new Intent(requireActivity(),FullscreenActivity.class);
        imageItemModelList.add(new ImageItemModel(imageItemModel.getId(), imageItemModel.getCatId(), imageItemModel.getImage()));
        intent.putExtra("myList", (Serializable) imageItemModelList);
        startActivity(intent);
    }
}