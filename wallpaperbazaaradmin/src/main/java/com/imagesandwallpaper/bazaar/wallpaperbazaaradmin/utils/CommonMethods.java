package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;

public class CommonMethods {

    public static Dialog loadingDialog(Context context) {
        Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.item_bg));
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }
}
