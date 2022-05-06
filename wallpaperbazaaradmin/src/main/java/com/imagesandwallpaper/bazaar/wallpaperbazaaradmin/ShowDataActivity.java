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
    String encodedImage, catId, catImage, catTitle;
    Dialog loadingDialog, imageDialog, catDialog;
    ApiInterface apiInterface;
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
            String[] items = new String[]{"Add Sub Category", "Add Item", "Update Category", "Delete Category"};
            builder.setTitle("Add Sub Category or Item").setCancelable(true).setItems(items, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        uploadCategory(categoryModel);
                        break;
                    case 1:
                        uploadImage(categoryModel);
                        break;
                    case 2:
                        // update category
                        catId = categoryModel.getId();
                        catImage = categoryModel.getImage();
                        catTitle = categoryModel.getTitle();
                        updateCategoryDialog(catId, catImage, catTitle);
                        break;
                    case 3:
                        // delete cat
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                        builder.setTitle("Delete this Category?")
                                .setNegativeButton("Cancel", (dialog1, which1) -> {

                                }).setPositiveButton("Delete", (dialog12, which12) -> {
                            loadingDialog.show();
                            catId = categoryModel.getId();
                            catImage = categoryModel.getImage();
                            map.put("id", catId);
                            map.put("title", "category");
                            map.put("path", "category_images/" + catImage);
                            deleteCategory(map, "category");
                        }).show();
                        break;
                }
            });
        } else if (categoryModel.getSubCat().equals("true")) {
            String[] items2 = new String[]{"Add a Subcategory", "Show Sub Category", "Update Category"};
            builder.setTitle("Add Subcategories").setCancelable(true).setItems(items2, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        uploadCategory(categoryModel);
                        break;
                    case 1:
                        Intent intent = new Intent(this, SubCatActivity.class);
                        intent.putExtra("catId", categoryModel.getId());
                        startActivity(intent);
                        break;
                    case 2:
                        // Update Category
                        catId = categoryModel.getId();
                        catImage = categoryModel.getImage();
                        catTitle = categoryModel.getTitle();
                        updateCategoryDialog(catId, catImage, catTitle);
                        break;
                }
            });
        } else if (categoryModel.getItem().equals("true")) {
            String[] items3 = new String[]{"Add an Item", "Show Images", "Update Category"};
            builder.setTitle("Add Item").setCancelable(true).setItems(items3, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        uploadImage(categoryModel);
                        break;
                    case 1:
                        Intent intent = new Intent(this, ShowImageActivity.class);
                        intent.putExtra("catId", categoryModel.getId());
                        intent.putExtra("catKey", "cat");
                        startActivity(intent);
                        break;
                    case 2:
                        // Update Category
                        catId = categoryModel.getId();
                        catImage = categoryModel.getImage();
                        catTitle = categoryModel.getTitle();
                        updateCategoryDialog(catId, catImage, catTitle);
                        break;
                }
            });
        }


//        if (categoryModel.getSubCat().equals("false") && categoryModel.getItem().equals("false")) {
//            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
//                    .setTitle("Add subcategory or Item")
//                    .setMessage("Would you like to add a subcategory or item?")
//                    .setCancelable(false)
//
//                    .setNegativeButton("Add Sub Category", (dialogInterface, i) -> {
//                        uploadCategory(categoryModel);
//
//                    })
//                    .setNeutralButton("Cancel", ((dialogInterface, i) -> {
//                    })).setPositiveButton("Add Item", (dialogInterface, i) -> {
//                uploadImage(categoryModel);
//
//            });
//
//        } else if (categoryModel.getSubCat().equals("true")) {
//            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
//                    .setTitle("Add subcategory")
//                    .setMessage("Would you like to add a subcategory?")
//                    .setCancelable(false)
//                    .setPositiveButton("Add subcategory", (dialogInterface, i) -> {
//                uploadCategory(categoryModel);
//
//            }).setNeutralButton("Cancel", ((dialogInterface, i) -> {
//            })).setNegativeButton("Show Sub Category", (dialogInterface, i) -> {
//                Intent intent = new Intent(this, SubCatActivity.class);
//                intent.putExtra("catId", categoryModel.getId());
//                startActivity(intent);
//            });
//
//
//        } else if (categoryModel.getItem().equals("true")) {
//            builder.setIcon(R.drawable.ic_baseline_add_alert_24)
//                    .setTitle("Add Item")
//                    .setMessage("Would you like to add a new image?")
//                    .setCancelable(false)
//                    .setPositiveButton("Add Item", (dialogInterface, i) -> {
//
//                uploadImage(categoryModel);
//            }).setNeutralButton("Cancel", ((dialogInterface, i) -> {
//            })).setNegativeButton("Show Images", (dialogInterface, i) -> {
//                Intent intent = new Intent(this, ShowImageActivity.class);
//                intent.putExtra("catId", categoryModel.getId());
//                intent.putExtra("catKey", "cat");
//                startActivity(intent);
//            });
//        }


        builder.show();
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
        Glide.with(ShowDataActivity.this).load("https://gedgetsworld.in/Wallpaper_Bazaar/category_images/"
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
                    map.put("tableName","Cat");
                    updateCategory(map);
                }else {
                    map.put("catId", catId);
                    map.put("img", encodedImage);
                    map.put("deleteImg", catImage);
                    map.put("title", cTitle);
                    map.put("imgKey", "1");
                    map.put("tableName","Cat");
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
                    Toast.makeText(ShowDataActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    fetchCategory();
                }
                catDialog.dismiss();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void deleteCategory(Map<String, String> map, String type) {
        Call<MessageModel> call = apiInterface.deleteCategory(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowDataActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (type.equals("category")) {
                        fetchCategory();
                    }
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

            }
        });
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
        choseImgQuality = catDialog.findViewById(R.id.img_quality);

        Button cancelBtn = catDialog.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(view -> catDialog.dismiss());
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
        choseImgQuality = imageDialog.findViewById(R.id.img_quality);
        Button cancelBtn = imageDialog.findViewById(R.id.cancel_btn);
        Button uploadImageBtn = imageDialog.findViewById(R.id.upload_image_btn);

        dialogTitle.setText(categoryModel.getTitle());
        cancelBtn.setOnClickListener(view -> imageDialog.dismiss());
        chooseImage.setOnClickListener(view -> {
            String quality = choseImgQuality.getText().toString().trim();
            if (quality.isEmpty()) {
                Toast.makeText(this, "Before Selecting an image please enter image quality!", Toast.LENGTH_LONG).show();
                loadingDialog.dismiss();
            } else if (Integer.parseInt(quality) >= 10) {

                launcher.launch("image/*");
            } else {
                choseImgQuality.setError("Minimum Quality must be 10.");
                loadingDialog.dismiss();
            }
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

