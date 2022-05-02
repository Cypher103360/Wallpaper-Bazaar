package com.imagesandwallpaper.bazaar.iwb.adapters;

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
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.SubCatModel;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    List<SubCatModel> subCatModelList = new ArrayList<>();
    Context context;
    SubCatClickInterface catClickInterface;

    public SubCategoryAdapter(Context context, SubCatClickInterface catClickInterface) {
        this.context = context;
        this.catClickInterface = catClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cat_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/category_images/"
                + subCatModelList.get(position).getImage()).into(holder.catImage);
        holder.catTitle.setText(subCatModelList.get(position).getTitle());
        holder.itemView.setOnClickListener(view -> {
            catClickInterface.onClicked(subCatModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return subCatModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<SubCatModel> subCatModels){
        subCatModelList.clear();
        subCatModelList.addAll(subCatModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catImage);
            catTitle = itemView.findViewById(R.id.catTitle);
        }
    }
}
