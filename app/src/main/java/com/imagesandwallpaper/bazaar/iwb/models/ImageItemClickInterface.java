package com.imagesandwallpaper.bazaar.iwb.models;

import android.widget.ImageView;

public interface ImageItemClickInterface {
    void onClicked(ImageItemModel imageItemModel, int position);

    void onShareImg(ImageItemModel imageItemModel, int position, ImageView itemImage);

    void onDownloadImg(ImageItemModel imageItemModel, int position, ImageView itemImage);

    void onFavoriteImg(ImageItemModel imageItemModel, int position, ImageView favoriteIcon);

    void onSetImg(ImageItemModel imageItemModel, int position, ImageView itemImage);

    void onClicked();
}
