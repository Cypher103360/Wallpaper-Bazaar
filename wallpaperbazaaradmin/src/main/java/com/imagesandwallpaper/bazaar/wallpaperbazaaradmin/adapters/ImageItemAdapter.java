package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiInterface;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.MessageModel;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageItemAdapter extends RecyclerView.Adapter<ImageItemAdapter.ViewHolder> {
    List<ImageItemModel> imageItemModelList = new ArrayList<>();
    Context context;
    ImageItemClickInterface imageItemClickInterface;
    Map<String, String> map = new HashMap<>();

    public ImageItemAdapter(Context context, ImageItemClickInterface imageItemClickInterface) {
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
        Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                + imageItemModelList.get(position).getImage()).into(holder.itemImage);
        holder.itemView.setOnClickListener(view -> {
            imageItemClickInterface.onClicked(imageItemModelList.get(position));
        });

        switch (FilenameUtils.getExtension(imageItemModelList.get(position).getImage())) {
            case "jpeg":
            case "jpg":
            case "png":
                Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                        + imageItemModelList.get(position).getImage()).into(holder.itemImage);
                break;
            case "gif":
                Glide.with(context).asGif().load("https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/"
                        + imageItemModelList.get(position).getImage()).into(holder.itemImage);
                break;
        }

        if (imageItemModelList.get(position).getLockItem() != null) {
            holder.switchMaterial.setVisibility(View.VISIBLE);
            holder.switchMaterial.setChecked(imageItemModelList.get(position).getLockItem().equals("true"));
        }

        holder.switchMaterial.setOnClickListener(v -> {
            boolean isChecked = holder.switchMaterial.isChecked();
            if (isChecked) {
                Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();
                map.put("id", imageItemModelList.get(position).getId());
                map.put("lockItem", "true");
            } else {
                Toast.makeText(context, "false", Toast.LENGTH_SHORT).show();
                map.put("id", imageItemModelList.get(position).getId());
                map.put("lockItem", "false");
            }
            holder.updatePremium(map);
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
        Collections.reverse(imageItemModelList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SwitchMaterial switchMaterial;
        ImageView itemImage;
        ApiInterface apiInterface;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            switchMaterial = itemView.findViewById(R.id.switch1);
            apiInterface = ApiWebServices.getApiInterface();
        }

        private void updatePremium(Map<String, String> map) {
            Call<MessageModel> call = apiInterface.updatePremium(map);
            call.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                    assert response.body() != null;
                    Toast.makeText(itemView.getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<MessageModel> call, Throwable t) {
                    Toast.makeText(itemView.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
