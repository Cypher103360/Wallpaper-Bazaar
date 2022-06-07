package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.featured.FeaturedModel;

import java.util.ArrayList;
import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {
    List<FeaturedModel> featuredModelList = new ArrayList<>();
    Context context;
    FeaturedClickInterface featuredClickInterface;

    public FeaturedAdapter(Context context, FeaturedClickInterface featuredClickInterface) {
        this.context = context;
        this.featuredClickInterface = featuredClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.featured_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/featured_images/"
                + featuredModelList.get(position).getImage()).into(holder.featuredImage);
        holder.itemView.setOnClickListener(view -> {
            featuredClickInterface.onClicked(featuredModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return featuredModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<FeaturedModel> featuredModels) {
        featuredModelList.clear();
        featuredModelList.addAll(featuredModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView featuredImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            featuredImage = itemView.findViewById(R.id.featured_image);
        }
    }
}
