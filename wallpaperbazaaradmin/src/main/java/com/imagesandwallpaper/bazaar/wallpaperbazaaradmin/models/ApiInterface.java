package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads.AdsModelList;

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

    @FormUrlEncoded
    @POST("update_banner.php")
    Call<MessageModel> updateBanner(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_banner.php")
    Call<BannerModelList> fetchBanner(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("delete_api.php")
    Call<MessageModel> deleteCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("update_category.php")
    Call<MessageModel> updateCategory(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_popular_images.php")
    Call<ImageItemModelList> getPopularImageItem(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_premium_images.php")
    Call<ImageItemModelList> getPremiumImages(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_ads.php")
    Call<AdsModelList> fetchAds(@Field("id") String id);

    @FormUrlEncoded
    @POST("update_ads_id.php")
    Call<MessageModel> updateAdsId(@FieldMap Map<String, String> map);
}

