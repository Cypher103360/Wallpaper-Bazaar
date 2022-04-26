package com.imagesandwallpaper.bazaar.iwb.models;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

//    @FormUrlEncoded
//    @POST("upload_news_api.php")
//    Call<MessageModel> uploadNews(@FieldMap Map<String, String> map);
//
//    @FormUrlEncoded
//    @POST("upload_tips_api.php")
//    Call<MessageModel> uploadTips(@FieldMap Map<String, String> map);
//
//    @FormUrlEncoded
//    @POST("upload_strip_ban_api.php")
//    Call<MessageModel> uploadStripBan(@FieldMap Map<String, String> map);
//
    @POST("fetch_news_api.php")
    Call<CatModelList> getAllCategory();

    @POST("fetch_tips_api.php")
    Call<ImageItemModelList> getAllImageItem();

//    @FormUrlEncoded
//    @POST("ads_id_fetch.php")
//    Call<AdsModelList> fetchAds(@Field("id") String id);
}

