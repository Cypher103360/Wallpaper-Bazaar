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
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CategoryModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.SubCatModel;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {
    List<SubCatModel> categoryModelList = new ArrayList<>();
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
                +categoryModelList.get(position).getImage()).into(holder.catImage);
        holder.catTitle.setText(categoryModelList.get(position).getTitle());
        holder.itemView.setOnClickListener(view -> {
            catClickInterface.onClicked(categoryModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<SubCatModel> categoryModels){
        categoryModelList.clear();
        categoryModelList.addAll(categoryModels);
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
