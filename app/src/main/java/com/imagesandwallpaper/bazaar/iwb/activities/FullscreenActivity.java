package com.imagesandwallpaper.bazaar.iwb.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.imagesandwallpaper.bazaar.iwb.R;
import com.imagesandwallpaper.bazaar.iwb.adapters.FullImageAdapter;
import com.imagesandwallpaper.bazaar.iwb.databinding.ActivityFullscreenBinding;
import com.imagesandwallpaper.bazaar.iwb.fragments.LiveWallpaperFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.NewFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PopularFragment;
import com.imagesandwallpaper.bazaar.iwb.fragments.PremiumFragment;
import com.imagesandwallpaper.bazaar.iwb.models.ApiInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ApiWebServices;
import com.imagesandwallpaper.bazaar.iwb.models.Favorite;
import com.imagesandwallpaper.bazaar.iwb.models.FavoriteAppDatabase;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemClickInterface;
import com.imagesandwallpaper.bazaar.iwb.models.ImageItemModel;
import com.imagesandwallpaper.bazaar.iwb.models.UrlModel;
import com.imagesandwallpaper.bazaar.iwb.utils.CommonMethods;
import com.imagesandwallpaper.bazaar.iwb.utils.GIFLiveWallpaper;
import com.imagesandwallpaper.bazaar.iwb.utils.PrefManager;
import com.imagesandwallpaper.bazaar.iwb.utils.Prevalent;
import com.imagesandwallpaper.bazaar.iwb.utils.ShowAds;
import com.ironsource.mediationsdk.IronSource;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullscreenActivity extends AppCompatActivity implements ImageItemClickInterface {
    private static final String DOWNLOAD_ONLY = "40000";
    private static final String DOWNLOAD_AND_SHARE = "40001";
    public static String gifName, gifPath;
    ActivityFullscreenBinding binding;
    BottomSheetDialog loadImageDialog, setImageDialog, coinsDialog;
    Dialog dialog, loadingDialog;
    String id, catId, img, pos, key;
    ViewPager2 fullImageViewPager;
    FavoriteAppDatabase favoriteAppDatabase;
    Favorite favorite;
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    ConstraintLayout homeScreen, lockScreen, bothScreen, download, memes, visitSite;
    ApiInterface apiInterface;
    String memesUrl, webUrl;
    SharedPreferences preferences;
    TextView progressTitle;
    MaterialButton watchAd, skipAdWithPoint;
    MaterialCardView cardView;
    private String fileName;

    public static String decodeEmoji(String message) {
        try {
            return URLDecoder.decode(
                    message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return message;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fullImageViewPager = binding.fullScreenItemViewPager;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        apiInterface = ApiWebServices.getApiInterface();
        getLifecycle().addObserver(ads);
        fetchmemesUrl();
        fetchWebUrl();
        loadingDialog = CommonMethods.loadingDialog(this);
        id = getIntent().getStringExtra("id");
        catId = getIntent().getStringExtra("catId");
        img = getIntent().getStringExtra("img");
        pos = getIntent().getStringExtra("pos");
        key = getIntent().getStringExtra("key");


        FullImageAdapter viewPager2Adapter = new FullImageAdapter(this, this);

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
            case "live":
                for (ImageItemModel m : PopularFragment.imageItemModels) {
                    if (id.equals(m.getId())) {
                        LiveWallpaperFragment.imageItemModels.remove(m);
                        break;
                    }
                }
                LiveWallpaperFragment.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(LiveWallpaperFragment.imageItemModels);

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
                FavoriteActivity.imageItemModels.add(0, new ImageItemModel(id, catId, img));
                viewPager2Adapter.updateList(FavoriteActivity.imageItemModels);

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
        loadingDialog.dismiss();
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

        switch (FilenameUtils.getExtension(imageItemModel.getImage())) {
            case "jpeg":
            case "jpg":
            case "png":
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
                break;
            case "gif":
                if (ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/" + imageItemModel.getImage(), FilenameUtils.removeExtension(imageItemModel.getImage()), FilenameUtils.getExtension(imageItemModel.getImage()), DOWNLOAD_AND_SHARE);
                }

                break;
        }


    }

    public void onDownloadImg(ImageItemModel imageItemModel, int position, ImageView itemImage) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Download Images");
        mFirebaseAnalytics.logEvent("Clicked_On_Download_Images", bundle);

        if (checkPermissionForReadExternalStorage()) {

            switch (FilenameUtils.getExtension(imageItemModel.getImage())) {
                case "jpeg":
                case "jpg":
                case "png":
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
                            .setMimeType(getMimeType(uri));
                    downloadManager.enqueue(request);
                    Toast.makeText(this, "Downloading Started", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(() -> {
                        Toast.makeText(this, "Downloading Completed.", Toast.LENGTH_LONG).show();

                    }, 1500);
                    break;
                case "gif":
                    new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/" + imageItemModel.getImage(), FilenameUtils.removeExtension(imageItemModel.getImage()), FilenameUtils.getExtension(imageItemModel.getImage()), DOWNLOAD_ONLY);


                    break;
            }

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


        if (key.equals("premium") || key.equals("live")) {
            setCoinsDialog();

        } else {
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

        fileName = imageItemModel.getImage();
        homeScreen = dialog.findViewById(R.id.home_screen);
        lockScreen = dialog.findViewById(R.id.lock_screen);
        bothScreen = dialog.findViewById(R.id.both);
        download = dialog.findViewById(R.id.download_icon);
        visitSite = dialog.findViewById(R.id.visit_icon);
        memes = dialog.findViewById(R.id.memes_icon);


        if (!FilenameUtils.getExtension(imageItemModel.getImage()).equals("gif")) {
            dialog.show();

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) itemImage.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            homeScreen.setOnClickListener(view -> {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper Home");
                mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
                dialog.dismiss();

                if (key.equals("premium") || key.equals("live")) {

                    setCoinsDialog();

                } else {
                    loadingDialog.show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        try {
                            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM);
                            loadImageDialog();
                            Toast.makeText(this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    }
                }


            });
            lockScreen.setOnClickListener(view -> {

                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper LockScreen");
                mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);

                if (key.equals("premium") || key.equals("live")) {
                    setCoinsDialog();


                } else {
                    loadingDialog.show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        try {
                            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK);
                            loadImageDialog();
                            Toast.makeText(this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
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

                if (key.equals("premium") || key.equals("live")) {
                    setCoinsDialog();


                } else {
                    loadingDialog.show();

                    try {
                        loadImageDialog();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_LOCK);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            loadingDialog.dismiss();
                            wallpaperManager.setBitmap(bitmap, null, false, WallpaperManager.FLAG_SYSTEM);
                        }
                        Toast.makeText(this, "Wallpaper Applied", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            download.setOnClickListener(view -> {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Download Wallpaper");
                mFirebaseAnalytics.logEvent("Clicked_On_Download_Wallpaper_Images", bundle);
                onDownloadImg(imageItemModel, position, itemImage);
                dialog.dismiss();

            });
            memes.setOnClickListener(view -> {
                Log.d("ContentValue", memesUrl);
                openWebPage(memesUrl, this);
                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Memes");
                mFirebaseAnalytics.logEvent("Clicked_On_memes", bundle);

            });

            visitSite.setOnClickListener(view -> {
                Log.d("ContentValue", webUrl);
                openWebPage(webUrl, this);
                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Memes");
                mFirebaseAnalytics.logEvent("Clicked_On_VisitSite", bundle);
            });

        } else {

            homeScreen.setOnClickListener(view -> {

                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper Home");
                mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
                dialog.dismiss();
                if (key.equals("premium") || key.equals("live")) {

                    setCoinsDialog();


                } else {
                    set("https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/" + imageItemModel.getImage());
                }

            });
            lockScreen.setOnClickListener(view -> {

                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper LockScreen");
                mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
                if (key.equals("premium") || key.equals("live")) {
                    setCoinsDialog();

                } else {
                    set("https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/" + imageItemModel.getImage());

                }
            });
            bothScreen.setOnClickListener(view -> {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Set wallpaper Both");
                mFirebaseAnalytics.logEvent("Clicked_On_Set_Wallpaper_Images", bundle);
                dialog.dismiss();
                if (key.equals("premium") || key.equals("live")) {
                    setCoinsDialog();

                } else {
                    set("https://gedgetsworld.in/Wallpaper_Bazaar/live_wallpapers/" + imageItemModel.getImage());
                }
            });

            download.setOnClickListener(view -> {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "https://gedgetsworld.in/Wallpaper_Bazaar/all_images/" + imageItemModel.getImage());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Download Wallpaper");
                mFirebaseAnalytics.logEvent("Clicked_On_Download_Wallpaper_Images", bundle);
                onDownloadImg(imageItemModel, position, itemImage);
                dialog.dismiss();

            });
            memes.setOnClickListener(view -> {
                Log.d("ContentValue", memesUrl);
                openWebPage(memesUrl, this);
                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Memes");
                mFirebaseAnalytics.logEvent("Clicked_On_memes", bundle);

            });
            visitSite.setOnClickListener(view -> {
                Log.d("ContentValue", webUrl);
                openWebPage(webUrl, this);
                dialog.dismiss();
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Memes");
                mFirebaseAnalytics.logEvent("Clicked_On_VisitSite", bundle);
            });
        }
    }

    private void setCoinsDialog() {
        coinsDialog = new BottomSheetDialog(FullscreenActivity.this);
        coinsDialog.setContentView(R.layout.set_coin_use_dialog);
        coinsDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        coinsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        coinsDialog.setCancelable(true);
        coinsDialog.show();

        watchAd = coinsDialog.findViewById(R.id.watchAd);
        skipAdWithPoint = coinsDialog.findViewById(R.id.skipAd);
        cardView = coinsDialog.findViewById(R.id.cardView);

        watchAd.setOnClickListener(view -> {
            Snackbar.make(cardView, "Watch Reward Ads", Snackbar.LENGTH_LONG).show();

        });
        skipAdWithPoint.setOnClickListener(view -> {
            Snackbar.make(cardView, "Skip Reward Ads", Snackbar.LENGTH_LONG).show();

        });

    }


    @Override
    public void onClicked() {
        if (preferences.getString("action", "").equals("")) {
            onBackPressed();
        } else {
            preferences.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);

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

    protected void onResume() {
        super.onResume();
        if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerTopNetworkName)).equals("IronSourceWithMeta")) {
            ads.showTopBanner(this, binding.adViewTop);
        } else if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerBottomNetworkName)).equals("IronSourceWithMeta")) {
            ads.showBottomBanner(this, binding.adViewBottom);
        }
        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url, Context context) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    void fetchmemesUrl() {
        Call<UrlModel> call = apiInterface.getUrls("memes");
        call.enqueue(new Callback<UrlModel>() {
            @Override
            public void onResponse(@NonNull Call<UrlModel> call, @NonNull Response<UrlModel> response) {
                if (response.isSuccessful()) {
                    memesUrl = decodeEmoji(Objects.requireNonNull(response.body()).getUrl());

                }
            }

            @Override
            public void onFailure(@NonNull Call<UrlModel> call, @NonNull Throwable t) {

            }
        });
    }

    void fetchWebUrl() {
        Call<UrlModel> call = apiInterface.getUrls("web");
        call.enqueue(new Callback<UrlModel>() {
            @Override
            public void onResponse(@NonNull Call<UrlModel> call, @NonNull Response<UrlModel> response) {
                if (response.isSuccessful()) {
                    webUrl = decodeEmoji(Objects.requireNonNull(response.body()).getUrl());

                }
            }

            @Override
            public void onFailure(@NonNull Call<UrlModel> call, @NonNull Throwable t) {

            }
        });


    }

    public void share(String path) {
        File externalFile = new File(path);
        Uri imageUri = FileProvider.getUriForFile(FullscreenActivity.this, getApplicationContext().getPackageName() + ".provider", externalFile);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, "That's Awesome...\uD83D\uDC40 \n\n Install Now!☺☺ \n\n" + "https://play.google.com/store/apps/details?id=" + this.getPackageName());
        startActivity(Intent.createChooser(intent, "Share Image from " + this.getString(R.string.app_name)));

    }

    public void set(String uri) {
        if (ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            new SetWallpaper().execute(uri);
        }
    }

    public class DownloadFileFromURL extends AsyncTask<Object, String, String> {

        private String old = "-100";
        private String share_app;
        private String file_url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show();
//            linear_layout_wallpapar_activity_done_download.setVisibility(View.GONE);
//            linear_layout_gif_activity_download.setVisibility(View.GONE);
//            linear_layout_wallpapar_activity_download_progress.setVisibility(View.VISIBLE);
        }

        public boolean dir_exists(String dir_path) {
            boolean ret = false;
            File dir = new File(dir_path);
            if (dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(Object... f_url) {
            int count;
            try {
                URL url = new URL((String) f_url[0]);
                String title = (String) f_url[1];
                String extension = (String) f_url[2];
                this.share_app = (String) f_url[3];
                Log.v("contentValue", url + "  " + title + "  " + extension);
                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                Log.v("lenghtOfFile", lenghtOfFile + "");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                String dir_path = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.DownloadFolder);

                if (!dir_exists(dir_path)) {
                    File directory = new File(dir_path);
                    directory.mkdirs();
                    directory.mkdir();
                }
                OutputStream output = new FileOutputStream(dir_path + title.toString().replace("/", "_") + "" + "." + extension);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                this.file_url = dir_path + title.toString().replace("/", "_") + "." + extension;

                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{dir_path + title.replace("/", "_") + "" + "." + extension},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(new File(String.format("%s%s.%s", dir_path, title.toString().replace("/", "_"), extension)));
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    sendBroadcast(intent);
                }
            } catch (Exception e) {
                Log.v("exdownload", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            try {
                if (!progress[0].equals(old)) {
                    old = progress[0];
                    float is = (float) Float.parseFloat(progress[0]);
                    Log.v("download", progress[0] + "%");
                    loadingDialog.show();
                    progressTitle = loadingDialog.findViewById(R.id.progressTitle);
                    if (DOWNLOAD_ONLY.equals(share_app)) {
                        progressTitle.setVisibility(View.VISIBLE);

                    } else {
                        progressTitle.setVisibility(View.GONE);
                    }
                    progressTitle.setText(String.format("Downloading  %s", progress[0] + "%"));

                    new Handler().postDelayed(() -> {
                        progressTitle.setText(String.format("Downloaded"));
                    }, 1500);

                }
            } catch (Exception e) {
            }
        }

        @Override
        protected void onPostExecute(String file_url) {

            Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    FullscreenActivity.this.runOnUiThread(() -> {

                        progressTitle = loadingDialog.findViewById(R.id.progressTitle);
//                            progressTitle.setVisibility(View.VISIBLE);
                        progressTitle.setText("");

                        loadingDialog.dismiss();
//                            linear_layout_wallpapar_activity_done_download.setVisibility(View.GONE);
//                            linear_layout_gif_activity_download.setVisibility(View.VISIBLE);
//                            linear_layout_wallpapar_activity_download_progress.setVisibility(View.GONE);
                    });
                }
            }, 2000);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (DOWNLOAD_AND_SHARE.equals(share_app)) {
                    if (this.file_url != null) {
                        share(this.file_url);
                    } else {
                        loadingDialog.dismiss();
                    }
                }
            }, 3000);

        }
    }

    class SetWallpaper extends AsyncTask<String, String, String> {

        private String file_url = null;
        private String old = "-100";

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show();

        }

        public boolean dir_exists(String dir_path) {
            boolean ret = false;
            File dir = new File(dir_path);
            if (dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String dir_path = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.DownloadFolder);

                if (!dir_exists(dir_path)) {
                    File directory = new File(dir_path);
                    if (directory.mkdirs()) {
                        Log.v("dir", "is created 1");
                    } else {
                        Log.v("dir", "not created 1");

                    }
                    if (directory.mkdir()) {
                        Log.v("dir", "is created 2");
                    } else {
                        Log.v("dir", "not created 2");

                    }
                } else {
                    Log.v("dir", "is exist");
                }

                // Output stream
                OutputStream output = new FileOutputStream(dir_path + FilenameUtils.removeExtension(fileName).replace("/", "_") + "." + FilenameUtils.getExtension(fileName));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                output.close();
                input.close();
                this.file_url = dir_path + FilenameUtils.removeExtension(fileName).replace("/", "_") + "." + FilenameUtils.getExtension(fileName);
            } catch (Exception e) {
                Log.v("exdownload", e.getMessage());

            }
            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            try {
                float is = (float) Float.parseFloat(progress[0]);
                Log.v("download", progress[0] + "%");
                loadingDialog.show();
                progressTitle = loadingDialog.findViewById(R.id.progressTitle);
//                progressTitle.setVisibility(View.VISIBLE);
                progressTitle.setVisibility(View.GONE);
//                progressTitle.setText(String.format("Downloading  %s", is / 100));

//                progress_wheel_gif_activity_apply_progress.setProgress((float) (is / 100));
//                text_view_gif_activity_apply_progress.setText(progress[0] + "%");
            } catch (Exception e) {
            }
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            if (this.file_url == null) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_server), Toast.LENGTH_LONG).show();
                //  toggleProgress();
            } else {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    gifName = FilenameUtils.removeExtension(fileName).replace("/", "_") + "." + FilenameUtils.getExtension(fileName);
                    gifPath = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.DownloadFolder);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullscreenActivity.this);
                    wallpaperManager.clear();
                    stringBuilder.append(gifName);
                    PrefManager prf = new PrefManager(getApplicationContext());
                    prf.setString("LOCAL_GIF_NAME", gifName);
                    prf.setString("LOCAL_GIF_PATH", gifPath);

                    Log.d("contentValue", gifName + " " + gifPath);
//                    new GIFLiveWallpaper().setToWallPaper(FullscreenActivity.this);
                    Intent intent = new Intent(
                            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                            new ComponentName(FullscreenActivity.this, GIFLiveWallpaper.class));
                    startActivity(intent);
//                    progressTitle = loadingDialog.findViewById(R.id.progressTitle);
//                    progressTitle.setVisibility(View.VISIBLE);
//                    progressTitle.setText(String.format("Downloaded%s", 100 / 100));

                    loadingDialog.dismiss();
//                    progress_wheel_gif_activity_apply_progress.setProgress((float) (100 / 100));
//                    text_view_gif_activity_apply_progress.setText(getResources().getString(R.string.applying));
                } catch (Exception e) {
                    Log.v("exdownload", e.getMessage());
                }
            }
        }
    }
}
