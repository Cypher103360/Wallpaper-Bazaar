package com.imagesandwallpaper.bazaar.iwb.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;

public class FullImageAdapter extends RecyclerView.Adapter<FullImageAdapter.ViewHolder> {
    List<ImageItemModel> imageItemModelList = new ArrayList<>();
    Activity context;
    ImageItemClickInterface imageItemClickInterface;
    FavoriteAppDatabase favoriteAppDatabase;
    ShowAds showAds = new ShowAds();


    public FullImageAdapter(Activity context, ImageItemClickInterface imageItemClickInterface) {
        this.context = context;
        this.imageItemClickInterface = imageItemClickInterface;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.full_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((ViewHolder) holder).itemImage.layout(0, 0, 0, 0);
        context.runOnUiThread(Glide.with(context).load("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/"
                + imageItemModelList.get(position).getImage()).into(holder.itemImage)::getRequest);

//        holder.itemView.setOnClickListener(view -> imageItemClickInterface.onClicked(imageItemModelList.get(position), position));

        holder.favoriteIcon.setOnClickListener(view -> imageItemClickInterface.onFavoriteImg(imageItemModelList.get(position), position, holder.favoriteIcon));
        holder.downloadIcon.setOnClickListener(view -> imageItemClickInterface.onDownloadImg(imageItemModelList.get(position), position, holder.itemImage));
        holder.shareIcon.setOnClickListener(view -> imageItemClickInterface.onShareImg(imageItemModelList.get(position), position, holder.itemImage));
        holder.setBtn.setOnClickListener(view -> imageItemClickInterface.onSetImg(imageItemModelList.get(position), position, holder.itemImage));


        Log.d("ContentValue", String.valueOf(position));
        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            holder.adviewTop.setVisibility(View.GONE);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            holder.adviewBottom.setVisibility(View.GONE);

        } else {
            showAds.showTopBanner(context, holder.adviewTop);
            showAds.showBottomBanner(context, holder.adviewBottom);
        }


        if (position > 0) {
            if (position % 3 == 0) {
                showAds.showInterstitialAds(context);
            }
        }

        favoriteAppDatabase = Room.databaseBuilder(
                context,
                FavoriteAppDatabase.class
                , "FavoriteDB")
                .build();

        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        service.execute(() -> {
            // Background work
            for (Favorite f : favoriteAppDatabase.getFavoriteDao().getAllFavorite()) {
                if (f != null) {
                    if (f.getImage().equals(imageItemModelList.get(position).getImage()) && f.getCatId().equals("true")) {
                        holder.favoriteIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24));
                    }
                }
            }

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
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage, favoriteIcon, downloadIcon, shareIcon;
        Button setBtn;
        RelativeLayout adviewTop, adviewBottom;
        ShowAds showAds = new ShowAds();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.full_item_image);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            downloadIcon = itemView.findViewById(R.id.download_icon);
            shareIcon = itemView.findViewById(R.id.share_icon);
            setBtn = itemView.findViewById(R.id.set_btn);
            adviewTop = itemView.findViewById(R.id.adView_top);
            adviewBottom = itemView.findViewById(R.id.adView_bottom);


        }
    }
}
