package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.databinding.AdLayoutBinding;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;

public class SubCatImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 7;
    List<ImageItemModel> subCatImageModelList = new ArrayList<>();
    Activity context;
    SubCatImageClickInterface subCatImageClickInterface;
    ShowAds showAds = new ShowAds();
    SharedPreferences preferences;

    public SubCatImageAdapter(Activity context, SubCatImageClickInterface subCatImageClickInterface) {
        this.context = context;
        this.subCatImageClickInterface = subCatImageClickInterface;
    }

    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.image_item_layout, parent, false);
            return new ViewHolder(view);
        } else if (viewType == AD_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.ad_layout, parent, false);
            final ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams glp = (StaggeredGridLayoutManager.LayoutParams) lp;
                glp.setFullSpan(true);
            }
            return new AdViewHolder(view);
        } else return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {

        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(700) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.9f) //the alpha of the underlying children
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();

        // This is the placeholder for the imageView
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);

        if (holder.getItemViewType() == ITEM_VIEW) {
            int position = pos - Math.round(pos / ITEM_FEED_COUNT);

            Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                            + subCatImageModelList.get(position).getImage())
                    .placeholder(shimmerDrawable)
                    .into(((ViewHolder) holder).itemImage);


            ((ViewHolder) holder).itemView.setOnClickListener(view -> {
                subCatImageClickInterface.onClicked(subCatImageModelList.get(position), position);
            });

            if (preferences.getString("action", "").equals("cat")) {
                if (!preferences.getString("cat_item_pos", "").equals("")) {
                    subCatImageClickInterface.onClicked(subCatImageModelList.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))), Integer.parseInt(preferences.getString("cat_item_pos", "0")));
//                preferences.edit().clear().apply();
                }
            }

        } else if (holder.getItemViewType() == AD_VIEW) {
            ((AdViewHolder) holder).bindAdData();
        }
    }

    @Override
    public int getItemCount() {
        if (subCatImageModelList.size() > 0) {
            return subCatImageModelList.size() + Math.round(subCatImageModelList.size() / ITEM_FEED_COUNT);
        }
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ImageItemModel> imageItemModels) {
        subCatImageModelList.clear();
        subCatImageModelList.addAll(imageItemModels);
        Collections.reverse(subCatImageModelList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        AdLayoutBinding binding;

        public AdViewHolder(@NonNull View itemAdView2) {
            super(itemAdView2);
            binding = AdLayoutBinding.bind(itemAdView2);


        }

        private void bindAdData() {
            Log.d("admobAdNative", Paper.book().read(Prevalent.nativeAds));

            showAds.showNativeAds(context, binding.adLayout);


        }
    }
}
