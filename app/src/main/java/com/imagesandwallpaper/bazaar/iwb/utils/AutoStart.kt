package com.imagesandwallpaper.bazaar.iwb.utils

import android.content.Context
import android.widget.Toast
import com.judemanutd.autostarter.AutoStartPermissionHelper

fun autoStart(context: Context) {

    if (AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(context)) {
        AutoStartPermissionHelper.getInstance().getAutoStartPermission(context)
    }else{
        Toast.makeText(context, "Auto Start is available", Toast.LENGTH_SHORT).show()
    }

}


