package com.imagesandwallpaper.bazaar.iwb.models;

import com.imagesandwallpaper.bazaar.iwb.models.BannerImages.BannerModelList;
import com.imagesandwallpaper.bazaar.iwb.models.CatItemImage.CatItemImageModelList;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumModelList;
import com.imagesandwallpaper.bazaar.iwb.models.UserData.UserDataModel;
import com.imagesandwallpaper.bazaar.iwb.models.UserData.UserDataModelList;

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
    Call<ImageItemModelList> getPopularImageItem(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_popular_images.php")
    Call<ImageItemModelList> getNewImageItem(@Field("tableName") String id);

    @FormUrlEncoded
    @POST("fetch_premium_images.php")
    Call<ImageItemModelList> getPremiumImages(@FieldMap Map<String, String> map);

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

    @FormUrlEncoded
    @POST("fetch_premium_images.php")
    Call<PremiumModelList> getAllPremium(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_banner.php")
    Call<BannerModelList> getBanners(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_ads.php")
    Call<AdsModelList> fetchAds(@Field("id") String id);

    @POST("fetch_pro_wallpaper_url.php")
    Call<ProWallModelList> fetchProWallUrl();

    @POST("fetch_getWallpaper.php")
    Call<ProWallModelList> fetchGetWallUrl();

    @FormUrlEncoded
    @POST("upload_user_data.php")
    Call<MessageModel> uploadUserData(@FieldMap Map<String, String> map);

    @POST("fetch_random_images.php")
    Call<ImageItemModelList> fetchRandomImages();

    @FormUrlEncoded
    @POST("fetch_urls.php")
    Call<UrlModel> getUrls(@Field("id") String id);

    @FormUrlEncoded
    @POST("fetch_popular_images.php")
    Call<ImageItemModelList> getLiveWallpaperImage(@FieldMap Map<String, String> map);


    @POST("fetch_coins.php")
    Call<CoinsModelList> getAllCoins();

    @FormUrlEncoded
    @POST("update_coins.php")
    Call<MessageModel> updateCoins(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_user_data.php")
    Call<UserDataModel> getAllUserData(@Field("email") String email);

    @FormUrlEncoded
    @POST("update_user_data.php")
    Call<MessageModel> updateUserData(@FieldMap Map<String, String> map);

}

