package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CatClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.CategoryAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivityShowDataBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatViewModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CategoryModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;
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

public class ShowDataActivity extends AppCompatActivity implements CatClickInterface {

    CatViewModel catViewModel;
    List<CategoryModel> categoryModels;
    CategoryAdapter categoryAdapter;
    GridLayoutManager layoutManager;
    ActivityShowDataBinding binding;
    MaterialAlertDialogBuilder builder;
    ActivityResultLauncher<String> launcher;
    ImageView chooseImage, categoryImage;
    Bitmap bitmap;
    String encodedImage;
    Dialog loadingDialog, imageDialog, catDialog;
    ApiInterface apiInterface;
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
        binding = ActivityShowDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        catViewModel = new ViewModelProvider(this).get(CatViewModel.class);
        categoryModels = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, this);
        layoutManager = new GridLayoutManager(this, 3);
        loadingDialog = CommonMethods.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();

        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.showRV.setLayoutManager(layoutManager);
        binding.showRV.setAdapter(categoryAdapter);
        fetchCategory();
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                if (chooseImage != null) {
                    Glide.with(this).load(result).into(chooseImage);
                } else {
                    Glide.with(this).load(result).into(categoryImage);
                }
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(result);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = imageStore(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void fetchCategory() {
        loadingDialog.show();
        catViewModel.getAllCategory().observe(this, catModelList -> {
            if (catModelList.getData() != null) {
                categoryModels.clear();
                categoryModels.addAll(catModelList.getData());
                categoryAdapter.updateList(categoryModels);
            }
            loadingDialog.dismiss();
        });
    }

    @Override
    public void onClicked(CategoryModel categoryModel) {
        builder = new MaterialAlertDialogBuilder(ShowDataActivity.this);

        if (categoryModel.getSubCat().equals("false") && categoryModel.getItem().equals("false")) {
            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
                    .setTitle("Add subcategory or Item")
                    .setMessage("Would you like to add a subcategory or item?")
                    .setCancelable(false).setNegativeButton("Add Sub Category", (dialogInterface, i) -> {
                uploadCategory(categoryModel);

            })
                    .setNeutralButton("Cancel", ((dialogInterface, i) -> {
                    })).setPositiveButton("Add Item", (dialogInterface, i) -> {
                uploadImage(categoryModel);

            });

        } else if (categoryModel.getSubCat().equals("true")) {
            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
                    .setTitle("Add subcategory")
                    .setMessage("Would you like to add a subcategory?")
                    .setCancelable(false).setPositiveButton("Add subcategory", (dialogInterface, i) -> {

                uploadCategory(categoryModel);

            }).setNeutralButton("Cancel", ((dialogInterface, i) -> {
            })).setNegativeButton("Show Sub Category", (dialogInterface, i) -> {
                Intent intent =new Intent(this, SubCatActivity.class);
                intent.putExtra("catId", categoryModel.getId());
                startActivity(intent);

                startActivity(intent);
            });


        } else if (categoryModel.getItem().equals("true")) {
            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
                    .setTitle("Add Item")
                    .setMessage("Would you like to add a new image?")
                    .setCancelable(false).setPositiveButton("Add Item", (dialogInterface, i) -> {

                uploadImage(categoryModel);
            }).setNeutralButton("Cancel", ((dialogInterface, i) -> {
            })).setNegativeButton("Show Images", (dialogInterface, i) -> {
                Intent intent = new Intent(this, ShowImageActivity.class);
                intent.putExtra("catId", categoryModel.getId());
                intent.putExtra("catKey", "cat");

                startActivity(intent);
            });
        }


        builder.show();
    }

    private void uploadCategory(CategoryModel categoryModel) {
        catDialog = new Dialog(this);
        catDialog.setContentView(R.layout.category_dialog);
        catDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        catDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        catDialog.setCancelable(false);
        catDialog.show();

        categoryImage = catDialog.findViewById(R.id.choose_cat_imageView);
        EditText catTitle = catDialog.findViewById(R.id.cat_title);
        Button uploadCatBtn = catDialog.findViewById(R.id.upload_cat_btn);
        Button cancelBtn = catDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> catDialog.dismiss());
        categoryImage.setOnClickListener(view -> {
            launcher.launch("image/*");
        });

        uploadCatBtn.setOnClickListener(view -> {
            loadingDialog.show();
            String cTitle = catTitle.getText().toString().trim();

            if (encodedImage == null) {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(cTitle)) {
                catTitle.setError("Name Required");
                catTitle.requestFocus();
            } else {
                map.put("catId", categoryModel.getId());
                map.put("img", encodedImage);
                map.put("title", cTitle);
                map.put("subCat", "true");
                uploadCatData(map);
            }
        });
    }

    private void uploadCatData(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadSubCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Category Uploaded", Toast.LENGTH_SHORT).show();
                    catDialog.dismiss();
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

    private void uploadImage(CategoryModel categoryModel) {
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
                map.put("subCat", "false");
                uploadCatImages(map);
            }
        });


    }

    private void uploadCatImages(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.uploadCatItemImages(map);
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

