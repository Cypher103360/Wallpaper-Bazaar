package com.imagesandwallpaper.bazaar.iwb.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.FullImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;
import com.imagesandwallpaper.bazaar.iwb.fragments.NewFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PopularFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PremiumFragment;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;

public class FullscreenActivity extends AppCompatActivity implements ImageItemClickInterface {
    ActivityFullscreenBinding binding;
    BottomSheetDialog loadImageDialog, setImageDialog;
    Dialog dialog;
    String id, catId, img, pos, key;
    ViewPager2 fullImageViewPager;
    FavoriteAppDatabase favoriteAppDatabase;
    Favorite favorite;
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    ConstraintLayout homeScreen, lockScreen, bothScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fullImageViewPager = binding.fullScreenItemViewPager;

        getLifecycle().addObserver(ads);

        id = getIntent().getStringExtra("id");
        catId = getIntent().getStringExtra("catId");
        img = getIntent().getStringExtra("img");
        pos = getIntent().getStringExtra("pos");
        key = getIntent().getStringExtra("key");


        FullImageAdapter viewPager2Adapter = new FullImageAdapter(this, this);

        if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerTopNetworkName)).equals("IronSourceWithMeta")) {
            ads.showTopBanner(this, binding.adViewTop);

        } else if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerBottomNetworkName)).equals("IronSourceWithMeta")) {
            ads.showTopBanner(this, binding.adViewTop);
        }
        // adding the adapter to viewPager2
        // to show the views in recyclerview
        fullImageViewPager.setAdapter(viewPager2Adapter);
        switch (key) {
            case "new":
                for (ImageItemModel m : NewFragment.imageItemModels) {
                    if (id.equals(m.getId())) {
                        NewFragment.imageItemModels.remove(m);
                        break;
                    }
                }
                NewFragment.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(NewFragment.imageItemModels);

                break;
            case "pop":
                for (ImageItemModel m : PopularFragment.imageItemModels) {
                    if (id.equals(m.getId())) {
                        PopularFragment.imageItemModels.remove(m);
                        break;
                    }
                }
                PopularFragment.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(PopularFragment.imageItemModels);

                break;
            case "premium":
                for (ImageItemModel m : PremiumFragment.premiumModels) {
                    if (id.equals(m.getId())) {
                        PremiumFragment.premiumModels.remove(m);
                        break;
                    }
                }
                PremiumFragment.premiumModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(PremiumFragment.premiumModels);

                break;
            case "catItem":
                for (ImageItemModel m : CatItemsActivity.imageItemModels) {
                    if (id.equals(m.getId())) {
                        CatItemsActivity.imageItemModels.remove(m);
                        break;
                    }
                }
                CatItemsActivity.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(CatItemsActivity.imageItemModels);

                break;
            case "fav":
                for (ImageItemModel m : FavoriteActivity.imageItemModels) {
                    if (id.equals(m.getId())) {
                        FavoriteActivity.imageItemModels.remove(m);
                        break;
                    }
                }
                CatItemsActivity.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(CatItemsActivity.imageItemModels);

                break;
        }


        // To get swipe event of viewpager2
        fullImageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                Log.d("ContentValue", "OnScrolled");
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
//                Log.d("ContentValue", "onPageSelected");

            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
//                Log.d("ContentValue", "onPageScrollStateChanged");
            }
        });
    }


    public void loadImageDialog() {
        loadImageDialog = new BottomSheetDialog(FullscreenActivity.this);
        loadImageDialog.setContentView(R.layout.load_image_dialog);
        loadImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadImageDialog.setCancelable(false);
        loadImageDialog.show();

        ImageView cancelBtn = loadImageDialog.findViewById(R.id.cancel_btn);
        FrameLayout nativeAds = loadImageDialog.findViewById(R.id.native_ads);

        ads.showNativeAds(this, nativeAds);
        cancelBtn.setOnClickListener(view -> {
            loadImageDialog.dismiss();
            setImageDialog();
        });

    }

    public void setImageDialog() {
        setImageDialog = new BottomSheetDialog(FullscreenActivity.this);
        setImageDialog.setContentView(R.layout.set_image_dialog);
        setImageDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setImageDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setImageDialog.setCancelable(false);
        setImageDialog.show();

        ImageView cancelBtn = setImageDialog.findViewById(R.id.cancel_btn);
        FrameLayout nativeAds = setImageDialog.findViewById(R.id.native_ads);
        ads.showNativeAds(this, nativeAds);


        cancelBtn.setOnClickListener(view -> {
            setImageDialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ads.destroyBanner();
        finish();
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {
    }

    @Override
    public void onShareImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Share Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Share_Images", bundle);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(this.getExternalCacheDir(), File.separator + "/" + "Wallpaper Bazaar" + ".jpeg");
        BitmapDrawable bitmapDrawable = (BitmapDrawable) itemImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        try {

            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, "That's Awesome...\uD83D\uDC40 \n\n Install Now!☺☺ \n\n" + "https://play.google.com/store/apps/details?id=" + this.getPackageName());
            startActivity(Intent.createChooser(intent, "Share Image from " + this.getString(R.string.app_name)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Download Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Download_Images", bundle);

        ads.showInterstitialAds(this);
        if (checkPermissionForReadExternalStorage()) {
            String dirPath = "/Wallpaper Bazaar";
            String fileName = "images.jpg";
            File file = new File(dirPath, fileName);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(file))
                    .setMimeType(getMimeType(uri))
            ;
            downloadManager.enqueue(request);
            Toast.makeText(this, "Downloading Started", Toast.LENGTH_LONG).show();
        } else {
            try {
                requestPermissionForReadExternalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getMimeType(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }


    @Override
    public void onFavoriteImg(ImageItemModel imageItemModel, int position, ImageView favoriteIcon) {

        if (key.equals("fav")) {
            Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Saved in Favorite", Toast.LENGTH_SHORT).show();
        }


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Added Favorite Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Set_Favorite_Images", bundle);

        favoriteAppDatabase = Room.databaseBuilder(
                this,
                FavoriteAppDatabase.class
                , "FavoriteDB")
                .build();

        if (key.equals("fav")) {
            favoriteIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24));
            favorite = new Favorite(imageItemModel.getImage(), imageItemModel.getCatId(), Integer.parseInt(imageItemModel.getId()));
            ExecutorService service = Executors.newSingleThreadExecutor();
            new Handler(Looper.getMainLooper());
            service.execute(() -> {
                // Background work
                deleteFavorite();
            });
        } else {
            favoriteIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24));
            ExecutorService service = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());
            service.execute(() -> {
                // Background work
                Favorite f = favoriteAppDatabase.getFavoriteDao().getFavorite(imageItemModel.getImage());
                if (f != null) {
                    if (f.getImage().equals(imageItemModel.getImage()) && f.getCatId().equals("true")) {
                        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24));
                    } else {
                        CreateFavorite(imageItemModel.getImage(), "true");

                    }
                } else {
                    CreateFavorite(imageItemModel.getImage(), "true");
                }
            });

        }

    }

    private void deleteFavorite() {
        favoriteAppDatabase.getFavoriteDao().deleteFavorite(favorite);
    }

    private void CreateFavorite(String image, String catId) {
        favoriteAppDatabase.getFavoriteDao().addFavorite(new Favorite(image, catId, 0));


    }

    @Override
    public void onSetImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.set_wallpaper_layout);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.show();

        homeScreen = dialog.findViewById(R.id.home_screen);
        lockScreen = dialog.findViewById(R.id.lock_screen);
        bothScreen = dialog.findViewById(R.id.both);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) itemImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        homeScreen.setOnClickListener(view -> {
            loadImageDialog();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper Home");
            mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
            dialog.dismiss();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        lockScreen.setOnClickListener(view -> {
            loadImageDialog();
            dialog.dismiss();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper LockScreen");
            mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        bothScreen.setOnClickListener(view -> {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper Both");
            mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
            dialog.dismiss();

            try {
                loadImageDialog();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

    @Override
    public void onClicked() {
        onBackPressed();

    }

    public boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

}