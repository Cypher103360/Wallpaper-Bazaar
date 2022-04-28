package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("upload_popular_images.php")
    Call<MessageModel> uploadPopularImages(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_category_images.php")
    Call<MessageModel> uploadCategory(@FieldMap Map<String, String> map);
}

