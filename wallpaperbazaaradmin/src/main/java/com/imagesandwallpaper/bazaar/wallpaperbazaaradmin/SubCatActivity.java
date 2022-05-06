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
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.SubCatClickInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.SubCategoryAdapter;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.databinding.ActivitySubCatBinding;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
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
    Dialog loadingDialog, imageDialog,catDialog;
    ActivitySubCatBinding binding;
    String catId,catImage,catTitle;
    MaterialAlertDialogBuilder builder;
    ImageView chooseImage,categoryImage;
    Bitmap bitmap;
    String encodedImage;
    ActivityResultLauncher<String> launcher;
    Map<String, String> map = new HashMap<>();
    EditText choseImgQuality;


    public static String imageStore(Bitmap bitmap, int imageQuality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, stream);
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
        subCatViewModel = new ViewModelProvider(this, new SubCatModelFactory(this.getApplication(), catId)).get(SubCatViewModel.class);
        subCatModels = new ArrayList<>();
        subCategoryAdapter = new SubCategoryAdapter(this, this);
        layoutManager = new GridLayoutManager(this, 3);
        loadingDialog = CommonMethods.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();

        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.showRV.setLayoutManager(layoutManager);
        binding.showRV.setAdapter(subCategoryAdapter);
        fetchSubCategory();
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                if (chooseImage != null) {
                    Glide.with(this).load(result).into(chooseImage);
                } else {
                    Glide.with(this).load(result).into(categoryImage);
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
                .setCancelable(true)
                .setPositiveButton("Add Item", (dialogInterface, i) -> {
                    uploadImage(categoryModel);
                })
                .setNeutralButton("Update Category", ((dialogInterface, i) -> {
                    // update category
                    catId = categoryModel.getId();
                    catImage = categoryModel.getImage();
                    catTitle = categoryModel.getTitle();
                    updateCategoryDialog(catId, catImage, catTitle);
                }))
                .setNegativeButton("Show Images", (dialogInterface, i) -> {
                    Intent intent = new Intent(this, ShowImageActivity.class);
                    intent.putExtra("catId", categoryModel.getId());
                    intent.putExtra("catKey", "subCat");
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
        choseImgQuality = imageDialog.findViewById(R.id.img_quality);

        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText(categoryModel.getTitle());
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(quality) >= 10) {

                launcher.launch("image/*");
            } else {
                choseImgQuality.setError("Minimum Quality must be 10.");
            }
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

    private void updateCategoryDialog(String catId, String catImage, String mTitle) {
        catDialog = new Dialog(this);
        catDialog.setContentView(R.layout.category_dialog);
        catDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        catDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        catDialog.setCancelable(false);
        catDialog.show();

        categoryImage = catDialog.findViewById(R.id.choose_cat_imageView);
        EditText catTitle = catDialog.findViewById(R.id.cat_title);
        Button uploadCatBtn = catDialog.findViewById(R.id.upload_cat_btn);
        choseImgQuality = catDialog.findViewById(R.id.img_quality);
        Button cancelBtn = catDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> {
            catDialog.dismiss();
            encodedImage = "";
        });
        encodedImage = catImage;
        Glide.with(SubCatActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/category_images/"
                + catImage).into(categoryImage);
        catTitle.setText(mTitle);

        categoryImage.setOnClickListener(view -> {
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
                    map.put("catId", catId);
                    map.put("img", encodedImage);
                    map.put("deleteImg", catImage);
                    map.put("title", cTitle);
                    map.put("imgKey", "0");
                    map.put("tableName","subCat");
                    updateCategory(map);
                }else {
                    map.put("catId", catId);
                    map.put("img", encodedImage);
                    map.put("deleteImg", catImage);
                    map.put("title", cTitle);
                    map.put("imgKey", "1");
                    map.put("tableName","subCat");
                    updateCategory(map);
                }

            }
        });
    }
    private void updateCategory(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    Toast.makeText(SubCatActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchSubCategory();
                }
                catDialog.dismiss();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }
}