package com.imagesandwallpaper.bazaar.iwb.activities;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.FullImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;
import com.imagesandwallpaper.bazaar.iwb.fragments.HomeFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PremiumFragment;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.utils.Ads;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FullscreenActivity extends AppCompatActivity implements ImageItemClickInterface {
    ActivityFullscreenBinding binding;
    BottomSheetDialog loadImageDialog, setImageDialog;
    String id, catId, img, pos, key;
    ViewPager2 fullImageViewPager;
    FavoriteAppDatabase favoriteAppDatabase;
    Favorite favorite;
    ShowAds ads = new ShowAds();


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

        // adding the adapter to viewPager2
        // to show the views in recyclerview
        fullImageViewPager.setAdapter(viewPager2Adapter);
        if (key.equals("home")) {
            for (ImageItemModel m : HomeFragment.imageItemModels) {
                if (id.equals(m.getId())) {
                    HomeFragment.imageItemModels.remove(m);
                    break;
                }
            }
            HomeFragment.imageItemModels.add(0, new ImageItemModel(id, catId, img));
            viewPager2Adapter.updateList(HomeFragment.imageItemModels);

        } else if (key.equals("premium")) {
            for (ImageItemModel m : PremiumFragment.premiumModels) {
                if (id.equals(m.getId())) {
                    PremiumFragment.premiumModels.remove(m);
                    break;
                }
            }
            PremiumFragment.premiumModels.add(0, new ImageItemModel(id, catId, img));
            viewPager2Adapter.updateList(PremiumFragment.premiumModels);

        } else if (key.equals("catItem")) {
            for (ImageItemModel m : CatItemsActivity.imageItemModels) {
                if (id.equals(m.getId())) {
                    CatItemsActivity.imageItemModels.remove(m);
                    break;
                }
            }
            CatItemsActivity.imageItemModels.add(0, new ImageItemModel(id, catId, img));
            viewPager2Adapter.updateList(CatItemsActivity.imageItemModels);

        } else if (key.equals("fav")) {
            for (ImageItemModel m : FavoriteActivity.imageItemModels) {
                if (id.equals(m.getId())) {
                    FavoriteActivity.imageItemModels.remove(m);
                    break;
                }
            }
            CatItemsActivity.imageItemModels.add(0, new ImageItemModel(id, catId, img));
            viewPager2Adapter.updateList(CatItemsActivity.imageItemModels);

        }

        // To get swipe event of viewpager2
        fullImageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            // This method is triggered when there is any scrolling activity for the current page
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // triggered when you select a new page
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            // triggered when there is
            // scroll state will be changed
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
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
        Ads.destroyBanner();
        finish();
    }

    @Override
    public void onClicked(ImageItemModel imageItemModel, int position) {
    }

    @Override
    public void onShareImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

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
            intent.putExtra(Intent.EXTRA_TEXT, "Play store Link : https://play.google.com/store/apps/details?id=" + this.getPackageName());
            startActivity(Intent.createChooser(intent, "Share Image from " + this.getString(R.string.app_name)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

        ads.showInterstitialAds(this);
        Ads.destroyBanner();
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
                    if (f.getImage().equals(imageItemModel.getImage())) {
                        favoriteIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24));
                    } else {
                        CreateFavorite(imageItemModel.getImage(), imageItemModel.getCatId());

                    }
                } else {
                    CreateFavorite(imageItemModel.getImage(), imageItemModel.getCatId());
                }
            });

        }

    }

    private void deleteFavorite() {
        favoriteAppDatabase.getFavoriteDao().deleteFavorite(favorite);
        Toast.makeText(this, "Removed from Favorite", Toast.LENGTH_SHORT).show();

    }

    private void CreateFavorite(String image, String catId) {
        favoriteAppDatabase.getFavoriteDao().addFavorite(new Favorite(image, catId, 0));
        Toast.makeText(this, "Saved in Favorite", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onSetImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {
        ads.showInterstitialAds(this);
        Ads.destroyBanner();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) itemImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
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

}