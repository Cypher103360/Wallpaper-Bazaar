package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.CatClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    List<CategoryModel> categoryModelList = new ArrayList<>();
    Context context;
    CatClickInterface catClickInterface;

    public CategoryAdapter(Context context, CatClickInterface catClickInterface) {
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
        holder.catImage.setImageResource(categoryModelList.get(position).getCatImage());
        holder.catTitle.setText(categoryModelList.get(position).getCatTitle());
        holder.itemView.setOnClickListener(view -> {
            catClickInterface.onClicked(categoryModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CategoryModel> categoryModels){
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
