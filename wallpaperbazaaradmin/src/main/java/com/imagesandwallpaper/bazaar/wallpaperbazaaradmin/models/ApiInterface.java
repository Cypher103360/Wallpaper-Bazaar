package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("upload_popular_images.php")
    Call<MessageModel> uploadPopularImages(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_cat_item_images.php")
    Call<MessageModel> uploadCatItemImages(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_sub_cat_item_images.php")
    Call<MessageModel> uploadSubCatItemImages(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_category_images.php")
    Call<MessageModel> uploadCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_sub_category_images.php")
    Call<MessageModel> uploadSubCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_cat_item_images.php")
    Call<CatItemModelList> fetchCatItems(@Field("catId") String catId);

    @FormUrlEncoded
    @POST("fetch_sub_cat_item_images.php")
    Call<CatItemModelList> fetchSubCatItems(@Field("catId") String catId);

    @POST("fetch_category.php")
    Call<CatModelList> getAllCategory();

    @FormUrlEncoded
    @POST("fetch_sub_category.php")
    Call<SubCatModelList> getAllSubCategory(@Field("catId") String catId);

}

