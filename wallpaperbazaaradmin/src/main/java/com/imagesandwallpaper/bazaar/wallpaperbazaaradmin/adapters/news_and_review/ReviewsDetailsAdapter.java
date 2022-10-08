package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters.news_and_review;

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
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.news_and_reviews.DetailsModel;

import java.util.ArrayList;
import java.util.List;


public class ReviewsDetailsAdapter extends RecyclerView.Adapter<ReviewsDetailsAdapter.ViewHolder> {

    List<DetailsModel> detailsModelList = new ArrayList<>();
    Context context;
    ReviewsDetailsClickInterface reviewsDetailsClickInterface;

    public ReviewsDetailsAdapter(Context context, ReviewsDetailsClickInterface reviewsDetailsClickInterface) {
        this.context = context;
        this.reviewsDetailsClickInterface = reviewsDetailsClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.details_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.title.setText(detailsModelList.get(position).getNewsTitle());
        Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/fs_review_details_img/" +
                detailsModelList.get(position).getNewsImg()).into(holder.newsImg);
        holder.itemView.setOnClickListener(view ->
                reviewsDetailsClickInterface.OnReviewsClicked(detailsModelList.get(position)));

    }

    @Override
    public int getItemCount() {
        return detailsModelList.size();
    }

    public void updateList(List<DetailsModel> detailsModels) {
        detailsModelList.clear();
        detailsModelList.addAll(detailsModels);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView newsImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.detailsTitle);
            newsImg = itemView.findViewById(R.id.detailsImage);
        }
    }


}