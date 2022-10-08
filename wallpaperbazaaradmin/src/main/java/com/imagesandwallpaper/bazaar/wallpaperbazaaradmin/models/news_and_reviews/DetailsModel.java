package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews;

public class DetailsModel {
    String id,newsImg,newsTitle,url,newsDesc;

    public DetailsModel(String id, String newsImg, String newsTitle, String url, String newsDesc) {
        this.id = id;
        this.newsImg = newsImg;
        this.newsTitle = newsTitle;
        this.url = url;
        this.newsDesc = newsDesc;
    }

    public String getId() {
        return id;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getUrl() {
        return url;
    }

    public String getNewsDesc() {
        return newsDesc;
    }
}
