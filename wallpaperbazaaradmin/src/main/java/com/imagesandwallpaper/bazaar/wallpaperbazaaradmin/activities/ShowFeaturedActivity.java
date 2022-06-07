package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.activities;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.textfield.TextInputLayout;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.ShowDataActivity;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.FeaturedAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.FeaturedClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityShowFeaturedBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured.FeaturedModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured.FeaturedViewModel;
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

public class ShowFeaturedActivity extends AppCompatActivity implements FeaturedClickInterface {
    ActivityShowFeaturedBinding binding;
    RecyclerView featuredRecyclerview;
    FeaturedAdapter featuredAdapter;
    FeaturedViewModel featuredViewModel;
    List<FeaturedModel> featuredModels = new ArrayList<>();
    Dialog loadingDialog;
    String fId, fImage, encodedImage;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();
    ApiInterface apiInterface;
    EditText choseImgQuality;
    private Dialog featuredDialog;
    private ImageView featuredImage;
    private Bitmap bitmap;

    public static String imageStore(Bitmap bitmap, int imageQuality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream);
        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowFeaturedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = CommonMethods.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();


        featuredViewModel = new ViewModelProvider(this).get(FeaturedViewModel.class);
        featuredAdapter = new FeaturedAdapter(ShowFeaturedActivity.this, this);
        GridLayoutManager layoutManager = new GridLayoutManager(ShowFeaturedActivity.this, 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.featuredRecyclerView.setLayoutManager(layoutManager);
        binding.featuredRecyclerView.setAdapter(featuredAdapter);
        fetchFeaturedItems();
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                if (featuredImage != null) {
                    Glide.with(this).load(result).into(featuredImage);
                }
                try {
                    if (choseImgQuality != null) {
                        String imgQuality = choseImgQuality.getText().toString();
                        InputStream inputStream = this.getContentResolver().openInputStream(result);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = imageStore(bitmap, Integer.parseInt(imgQuality));
                        Toast.makeText(this, "Image quality is " + imgQuality, Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void fetchFeaturedItems() {
        loadingDialog.show();
        featuredViewModel.getFeatured().observe(this, featuredModelList -> {
            if (!featuredModelList.getData().isEmpty()) {
                featuredModels.clear();
                featuredModels.addAll(featuredModelList.getData());
                featuredAdapter.updateList(featuredModels);
            }
            loadingDialog.dismiss();
        });
    }

    @Override
    public void onClicked(FeaturedModel featuredModel) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        String[] items = new String[]{"Update", "Delete"};
        builder.setTitle("Update & Delete")
                .setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            uploadFeatured(featuredModel.getId(), featuredModel.getImage(), featuredModel.getUrl());
                            break;
                        case 1:
                            MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(ShowFeaturedActivity.this);
                            builder1.setTitle("Delete this Item?")
                                    .setNegativeButton("Cancel", (dialog1, which1) -> {

                                    }).setPositiveButton("Delete", (dialog12, which12) -> {
                                        loadingDialog.show();
                                        fId = featuredModel.getId();
                                        fImage = featuredModel.getImage();
                                        map.put("id", fId);
                                        map.put("title", "featured");
                                        map.put("path", "featured_images/" + fImage);
                                        deleteCategory(map, "featured");
                                    }).show();
                            break;
                        default:
                    }
                }).show();
    }

    private void deleteCategory(Map<String, String> map, String key) {
        Call<MessageModel> call = apiInterface.deleteCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(ShowFeaturedActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (key.equals("featured")) {
                        fetchFeaturedItems();
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void uploadFeatured(String featuredId, String fImage, String featuredUrl) {
        featuredDialog = new Dialog(this);
        featuredDialog.setContentView(R.layout.category_dialog);
        featuredDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        featuredDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        featuredDialog.setCancelable(false);
        featuredDialog.show();

        featuredImage = featuredDialog.findViewById(R.id.choose_cat_imageView);
        EditText catTitle = featuredDialog.findViewById(R.id.cat_title);
        TextInputLayout textInputLayout = featuredDialog.findViewById(R.id.textInputLayout);
        textInputLayout.setHint("Enter Url");
        Button uploadCatBtn = featuredDialog.findViewById(R.id.upload_cat_btn);
        choseImgQuality = featuredDialog.findViewById(R.id.img_quality);
        Button cancelBtn = featuredDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            featuredDialog.dismiss();
            encodedImage = "";
        });
        encodedImage = fImage;
        Glide.with(ShowFeaturedActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/featured_images/"
                + fImage).into(featuredImage);
        catTitle.setText(featuredUrl);
        featuredImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(quality) >= 10) {

                launcher.launch("image/*");
            } else {
                choseImgQuality.setError("Minimum Quality must be 10.");
            }
        });

        uploadCatBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String cTitle = catTitle.getText().toString().trim();

            if (TextUtils.isEmpty(cTitle)) {
                catTitle.setError("Cat Name Required");
                catTitle.requestFocus();
                loadingDialog.dismiss();
            } else {
                if (encodedImage.length() <= 100) {
                    map.put("catId", featuredId);
                    map.put("img", encodedImage);
                    map.put("deleteImg", fImage);
                    map.put("title", cTitle);
                    map.put("imgKey", "0");
                    map.put("tableName", "featured");
                    updateFeatured(map);
                } else {
                    map.put("catId", featuredId);
                    map.put("img", encodedImage);
                    map.put("deleteImg", fImage);
                    map.put("title", cTitle);
                    map.put("imgKey", "1");
                    map.put("tableName", "featured");
                    updateFeatured(map);
                }

            }
        });
    }

    private void updateFeatured(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Toast.makeText(ShowFeaturedActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchFeaturedItems();
                }
                featuredDialog.dismiss();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }
}