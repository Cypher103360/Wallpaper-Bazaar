package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.Ads;

public class AdsModel {
    String id;
    String appId;
    String appLovinSdkKey;
    String bannerTopNetworkName;
    String bannerTop;
    String bannerBottomNetworkName;
    String bannerBottom;
    String interstitialNetwork;
    String interstitialAds;
    String nativeAdsNetworkName;
    String nativeAds;

    public AdsModel(String id, String appId, String appLovinSdkKey, String bannerTop, String bannerTopNetworkName, String bannerBottom, String bannerBottomNetworkName, String interstitialAds, String interstitialNetwork, String nativeAds, String nativeAdsNetworkName) {
        this.id = id;
        this.appId = appId;
        this.appLovinSdkKey = appLovinSdkKey;
        this.bannerTop = bannerTop;
        this.bannerTopNetworkName = bannerTopNetworkName;
        this.bannerBottom = bannerBottom;
        this.bannerBottomNetworkName = bannerBottomNetworkName;
        this.interstitialAds = interstitialAds;
        this.interstitialNetwork = interstitialNetwork;
        this.nativeAds = nativeAds;
        this.nativeAdsNetworkName = nativeAdsNetworkName;
    }

    public String getId() {
        return id;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppLovinSdkKey() {
        return appLovinSdkKey;
    }

    public String getBannerTop() {
        return bannerTop;
    }

    public String getBannerTopNetworkName() {
        return bannerTopNetworkName;
    }

    public String getBannerBottom() {
        return bannerBottom;
    }

    public String getBannerBottomNetworkName() {
        return bannerBottomNetworkName;
    }

    public String getInterstitialAds() {
        return interstitialAds;
    }

    public String getInterstitialNetwork() {
        return interstitialNetwork;
    }

    public String getNativeAds() {
        return nativeAds;
    }

    public String getNativeAdsNetworkName() {
        return nativeAdsNetworkName;
    }
}


//CREATE TABLE `ads_table`
//      (`id` varchar(255),
//      `appId` varchar(255),
//      `appLovinSdkKey` varchar(255),
//      `bannerTop` varchar(255),
//      `bannerTopNetworkName` varchar(255),
//      `bannerBottom` varchar(255),
//      `bannerBottomNetworkName` varchar(255),
//      `interstitialAds` varchar(255),
//      `interstitialNetwork` varchar(255),
//      `nativeAds` varchar(255),
//      `nativeAdsNetworkName` varchar(255))
//      ;