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
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.PremiumImages.PremiumModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.ViewHolder> {
    List<PremiumModel> premiumModelList = new ArrayList<>();
    Context context;
    PremiumClickInterface premiumClickInterface;

    public PremiumAdapter(Context context, PremiumClickInterface premiumClickInterface) {
        this.context = context;
        this.premiumClickInterface = premiumClickInterface;
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
                +premiumModelList.get(position).getImage()).into(holder.itemImage);
        holder.itemView.setOnClickListener(view -> {
            premiumClickInterface.onClicked(premiumModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return premiumModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<PremiumModel> premiumModels){
        premiumModelList.clear();
        premiumModelList.addAll(premiumModels);
        Collections.reverse(premiumModelList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage,premiumImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage  = itemView.findViewById(R.id.item_image);
            premiumImage = itemView.findViewById(R.id.pre_logo);
            premiumImage.setVisibility(View.VISIBLE);
        }
    }
}
