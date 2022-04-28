package com.imagesandwallpaper.bazaar.iwb.models;

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

//    @FormUrlEncoded
//    @POST("ads_id_fetch.php")
//    Call<AdsModelList> fetchAds(@Field("id") String id);
}

