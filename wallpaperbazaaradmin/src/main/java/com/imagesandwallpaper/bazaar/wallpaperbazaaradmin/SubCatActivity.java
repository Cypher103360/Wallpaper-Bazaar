package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.SubCatClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.SubCategoryAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivitySubCatBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.SubCatModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.SubCatModelFactory;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.SubCatViewModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils.CommonMethods;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCatActivity extends AppCompatActivity implements SubCatClickInterface {
    SubCatViewModel subCatViewModel;
    List<SubCatModel> subCatModels;
    SubCategoryAdapter subCategoryAdapter;
    GridLayoutManager layoutManager;
    ApiInterface apiInterface;
    Dialog loadingDialog, imageDialog;
    ActivitySubCatBinding binding;
    String catId;
    MaterialAlertDialogBuilder builder;
    ImageView chooseImage;
    Bitmap bitmap;
    String encodedImage;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();



    public static String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
//        Log.d("TAg", myList.toString());
        catId = getIntent().getStringExtra("catId");
        subCatViewModel = new ViewModelProvider(this, new SubCatModelFactory(this.getApplication(),catId)).get(SubCatViewModel.class);
        subCatModels = new ArrayList<>();
        subCategoryAdapter = new SubCategoryAdapter(this, this);
        layoutManager = new GridLayoutManager(this, 3);
        loadingDialog = CommonMethods.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();

        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.showRV.setLayoutManager(layoutManager);
        binding.showRV.setAdapter(subCategoryAdapter);
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                    Glide.with(this).load(result).into(chooseImage);
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(result);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = imageStore(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        fetchSubCategory();

    }

    private void fetchSubCategory() {
        loadingDialog.show();
        subCatViewModel.getAllSubCategory().observe(this, subCatModelList -> {
            if (subCatModelList.getData() != null) {
                subCatModels.clear();
                subCatModels.addAll(subCatModelList.getData());
                subCategoryAdapter.updateList(subCatModels);
            }
            loadingDialog.dismiss();
        });
    }

    @Override
    public void onClicked(SubCatModel categoryModel) {
        builder = new MaterialAlertDialogBuilder(SubCatActivity.this);

        builder.setIcon(R.drawable.ic_baseline_add_alert_24)
                .setTitle("Add Item")
                .setMessage("Would you like to add a new image?")
                .setCancelable(false).setPositiveButton("Add Item", (dialogInterface, i) -> {

            uploadImage(categoryModel);
        }).setNeutralButton("Cancel", ((dialogInterface, i) -> {
        })).setNegativeButton("Show Images", (dialogInterface, i) -> {
            Intent intent = new Intent(this, ShowImageActivity.class);
            intent.putExtra("catId", categoryModel.getId());
            intent.putExtra("catKey","subCat");
            startActivity(intent);
        });
        builder.show();

    }

    private void uploadImage(SubCatModel categoryModel) {
        imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.upload_image_layout);
        imageDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        imageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        imageDialog.setCancelable(false);
        imageDialog.show();

        TextView dialogTitle = imageDialog.findViewById(R.id.dialog_title);
        chooseImage = imageDialog.findViewById(R.id.choose_imageView);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText(categoryModel.getTitle());
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setOnClickListener(view -> {
            launcher.launch("image/*");
        });
        uploadImageBtn.setOnClickListener(view -> {
            loadingDialog.show();
            if (encodedImage == null) {
                Toast.makeText(getApplicationContext(), "Please Select an Image", Toast.LENGTH_SHORT).show();
            } else {
                map.put("img", encodedImage);
                map.put("catId", categoryModel.getId());

                uploadCatImages(map);
            }
        });


    }

    private void uploadCatImages(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadSubCatItemImages(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    imageDialog.dismiss();
                } else {
                    assert response.body() != null;
                    Toast.makeText(getApplicationContext(), response.body().getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }
}