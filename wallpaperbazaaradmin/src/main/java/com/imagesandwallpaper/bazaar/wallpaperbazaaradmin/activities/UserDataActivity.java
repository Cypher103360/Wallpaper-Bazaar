package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.UserDataAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.UserDataClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityUserDataBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData.UserDataModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData.UserDataModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataActivity extends AppCompatActivity implements UserDataClickInterface {
    ActivityUserDataBinding binding;
    RecyclerView userDataRV;
    UserDataAdapter userDataAdapter;
    List<UserDataModel> userDataModels = new ArrayList<>();
    ApiInterface apiInterface;
    Dialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiWebServices.getApiInterface();

        loading = CommonMethods.loadingDialog(UserDataActivity.this);
        loading.show();
        userDataRV = binding.userDataRecyclerView;
        userDataRV.setLayoutManager(new LinearLayoutManager(UserDataActivity.this));
        userDataAdapter = new UserDataAdapter(UserDataActivity.this,this);
        userDataRV.setAdapter(userDataAdapter);

        fetchUserData();
    }

    private void fetchUserData() {
        Call<UserDataModelList> call = apiInterface.getAllUserData();
        call.enqueue(new Callback<UserDataModelList>() {
            @Override
            public void onResponse(@NonNull Call<UserDataModelList> call, @NonNull Response<UserDataModelList> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    if (!response.body().getData().isEmpty()){
                        userDataModels.clear();
                        userDataModels.addAll(response.body().getData());
                        userDataAdapter.updateList(userDataModels);
                    }
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<UserDataModelList> call, @NonNull Throwable t) {
                loading.dismiss();
            }
        });
    }

    @Override
    public void onClicked(UserDataModel userDataModel) {

    }
}