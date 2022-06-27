package com.imagesandwallpaper.bazaar.iwb.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imagesandwallpaper.bazaar.iwb.activities.RefreshingActivity;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class BootUpReceiver extends BroadcastReceiver {
    private static final String ONESIGNAL_APP_ID = "4d2242b4-ec1e-4e4e-9969-5bf6571c8b4d";

    @Override
    public void onReceive(Context context, Intent intent) {

         intent = new Intent(context, RefreshingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(context);
        OneSignal.setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(context));
        OneSignal.setAppId(ONESIGNAL_APP_ID);

    }

    private static class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        Context context;

        public ExampleNotificationOpenedHandler(Context context) {
            this.context = context;
        }

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            OSNotificationAction.ActionType actionType = result.getAction().getType();
            JSONObject data = result.getNotification().getAdditionalData();
            String activityToBeOpened;
            String activity;

//            if (data != null) {
//                activityToBeOpened = data.optString("activityToBeOpened", null);
//                if (activityToBeOpened.equals("ABC")) {
//                    Log.i("OneSignal", "customkey set with value: " + activityToBeOpened);
//                    Intent intent = new Intent(MyApp.this, HomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
//                } else if (activityToBeOpened.equals("DEF")) {
//                    Intent intent = new Intent(MyApp.this, FavoriteActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
//                }
//            } else {
            Intent intent = new Intent(context, RefreshingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//            }

        }
    }

}
