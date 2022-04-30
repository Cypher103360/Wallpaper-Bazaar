package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CatItemModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CatItemsAdapter extends RecyclerView.Adapter<CatItemsAdapter.ViewHolder> {
    List<CatItemModel> catItemModels = new ArrayList<>();
    Context context;
    CatItemClickInterface catItemClickInterface;

    public CatItemsAdapter(Context context, CatItemClickInterface catItemClickInterface) {
        this.context = context;
        this.catItemClickInterface = catItemClickInterface;
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
                +catItemModels.get(position).getImage()).into(holder.catImage);

        holder.itemView.setOnClickListener(view -> {
            catItemClickInterface.onClicked(catItemModels.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return catItemModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CatItemModel> catItemModels){
        this.catItemModels.clear();
        this.catItemModels.addAll(catItemModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.item_image);
        }
    }
}
