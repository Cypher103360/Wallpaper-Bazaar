package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;

import java.util.ArrayList;
import java.util.List;

public class SubCatImageAdapter extends RecyclerView.Adapter<SubCatImageAdapter.ViewHolder> {
    List<ImageItemModel> subCatImageModelList = new ArrayList<>();
    Context context;
    SubCatImageClickInterface subCatImageClickInterface;

    public SubCatImageAdapter(Context context, SubCatImageClickInterface subCatImageClickInterface) {
        this.context = context;
        this.subCatImageClickInterface = subCatImageClickInterface;
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
                +subCatImageModelList.get(position).getImage()).into(holder.itemImage);
        holder.itemView.setOnClickListener(view -> {
            subCatImageClickInterface.onClicked(subCatImageModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return subCatImageModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ImageItemModel> imageItemModels){
        subCatImageModelList.clear();
        subCatImageModelList.addAll(imageItemModels);
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
