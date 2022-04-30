package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.ArrayList;
import java.util.List;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ViewHolder> {
    List<ImageItemModel> imageItemModelList = new ArrayList<>();
    Context context;
    ImageItemClickInterface imageItemClickInterface;

    public ImageItemAdapter(Context context, ImageItemClickInterface imageItemClickInterface) {
        this.context = context;
        this.imageItemClickInterface = imageItemClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                +imageItemModelList.get(position).getImage()).into(holder.itemImage);
        holder.itemView.setOnClickListener(view -> {
            imageItemClickInterface.onClicked(imageItemModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return imageItemModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ImageItemModel> imageItemModels){
        imageItemModelList.clear();
        imageItemModelList.addAll(imageItemModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage  = itemView.findViewById(R.id.item_image);
        }
    }
}
