package com.imagesandwallpaper.bazaar.iwb.models;

import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("fetch_category.php")
    Call<CatModelList> getAllCategory();

    @FormUrlEncoded
    @POST("fetch_popular_images.php")
    Call<ImageItemModelList> getPopularImageItem(@FieldMap Map<String,String> map);

    //fetch_cat_item_images.php
    //fetch_sub_category.php
    @FormUrlEncoded
    @POST("fetch_cat_item_images.php")
    Call<CatItemImageModelList> getCatItemImages(@Field("catId") String id);

    @FormUrlEncoded
    @POST("fetch_sub_category.php")
    Call<SubCatModelList> getSubCategories(@Field("catId") String id);

    @FormUrlEncoded
    @POST("fetch_sub_cat_item_images.php")
    Call<SubCatImageModelList> getSubCategoryItemsImages(@Field("catId") String id);


//    @FormUrlEncoded
//    @POST("ads_id_fetch.php")
//    Call<AdsModelList> fetchAds(@Field("id") String id);
}

