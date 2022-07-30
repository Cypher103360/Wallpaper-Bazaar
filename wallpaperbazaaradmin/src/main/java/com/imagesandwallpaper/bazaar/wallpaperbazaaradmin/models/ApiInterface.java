package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads.AdsModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData.UserDataModelList;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured.FeaturedModelList;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
    @POST("upload_featured.php")
    Call<MessageModel> uploadFeatured(@FieldMap Map<String, String> map);

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

    @POST("fetch_featured.php")
    Call<FeaturedModelList> getAllFeatured();

    @FormUrlEncoded
    @POST("fetch_urls.php")
    Call<UrlModel> getUrls(@Field("id") String id);

    @FormUrlEncoded
    @POST("update_urls.php")
    Call<MessageModel> updateUrls(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("fetch_sub_category.php")
    Call<SubCatModelList> getAllSubCategory(@Field("catId") String catId);


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

    @POST("fetch_user_data.php")
    Call<UserDataModelList> getAllUserData();

    @POST("fetch_pro_wallpaper_url.php")
    Call<ProWallModelList> fetchProWallUrl();

    @POST("fetch_getWallpaper.php")
    Call<ProWallModelList> fetchGetWallpaperMessage();

    @POST("fetch_file_transfer_url.php")
    Call<ProWallModelList> fetchFileTransferUrl();


    @FormUrlEncoded
    @POST("update_pro_wallpaper_url.php")
    Call<MessageModel> updateProWallUrl(@FieldMap Map<String, String> map);

    @Multipart
    @POST("update_banner.php")
    Call<MessageModel> updateBanner(@Part MultipartBody.Part idPart,
                                    @Part MultipartBody.Part imgPart,
                                    @Part MultipartBody.Part urlPart,
                                    @Part MultipartBody.Part deleteImgPart,
                                    @Part MultipartBody.Part imgKeyPart,@Part MultipartBody.Part tablePart);

    @Multipart
    @POST("upload_live_wallpaper.php")
    Call<ResponseBody> uploadLiveWallpaper(@Part MultipartBody.Part liveWallPart,
                                           @Part MultipartBody.Part idPart);

    //
    @FormUrlEncoded
    @POST("update_premium.php")
    Call<MessageModel> updatePremium(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("upload_fileShare_banners.php")
    Call<MessageModel> uploadFileShareBanners(@FieldMap Map<String, String> map);

    @POST("fetch_fileShare_banners.php")
    Call<List<BannerModel>> fetchBanners();
}

