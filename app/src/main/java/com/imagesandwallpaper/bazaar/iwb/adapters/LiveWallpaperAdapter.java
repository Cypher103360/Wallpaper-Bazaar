package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiveWallpaperAdapter extends RecyclerView.Adapter<LiveWallpaperAdapter.ViewHolder> {

    List<ImageItemModel> imageItemModelList = new ArrayList<>();
    Activity context;
    ImageItemClickInterface imageItemClickInterface;

    public LiveWallpaperAdapter(Activity context, ImageItemClickInterface imageItemClickInterface) {
        this.context = context;
        this.imageItemClickInterface = imageItemClickInterface;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.image_item_layout, parent, false);
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
//                .setDuration(800) // how long the shimmering animation takes to do one full sweep
//                .setBaseAlpha(0.9f) //the alpha of the underlying children
//                .setHighlightAlpha(0.5f) // the shimmer alpha amount
//                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
//                .setAutoStart(true)
//                .build();
//
//        // This is the placeholder for the imageView
//        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
//        shimmerDrawable.setShimmer(shimmer);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(holder.itemView.getContext());
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        switch (FilenameUtils.getExtension(imageItemModelList.get(position).getImage())) {
            case "jpeg":
            case "jpg":
            case "png":
                Glide.with(context)
                        .load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                                + imageItemModelList.get(position).getImage())
                        .placeholder(circularProgressDrawable)
                        .into(((ViewHolder) holder).itemImage);
                break;
            case "gif":
                Glide.with(context)
                        .asGif().load("https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/"
                                + imageItemModelList.get(position).getImage())
                        .placeholder(circularProgressDrawable)
                        .into(((ViewHolder) holder).itemImage);
                break;
        }


        ((ViewHolder) holder).itemView.setOnClickListener(view -> {
            imageItemClickInterface.onClicked(imageItemModelList.get(position), position);
        });
    }

    @Override
    public int getItemCount() {

        return imageItemModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ImageItemModel> imageItemModels) {
        imageItemModelList.clear();
        imageItemModelList.addAll(imageItemModels);
        Collections.shuffle(imageItemModelList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage, premiumImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            premiumImage = itemView.findViewById(R.id.pre_logo);
            premiumImage.setVisibility(View.VISIBLE);
        }
    }

}
