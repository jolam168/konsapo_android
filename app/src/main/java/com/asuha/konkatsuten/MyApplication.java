package com.asuha.konkatsuten;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Created by lamyiucho on 21/10/2017.
 */

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).setNotificationOpenedHandler(new NewsNotificationOpenedHandler()).setNotificationReceivedHandler(new NewsNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



        // Call syncHashedEmail anywhere in your app if you have the user's email.
        // This improves the effectiveness of OneSignal's "best-time" notification scheduling feature.
        // OneSignal.syncHashedEmail(userEmail);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private class NewsNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler  {

        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            // Get custom datas from notification
            JSONObject data = result.notification.payload.additionalData;
            if (data != null) {
                String myCustomData = data.optString("key", null);
            }

            OSNotificationAction.ActionType actionType = result.action.type;
            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            }

            // Launch new activity using Application object
            String launchUrl = result.notification.payload.launchURL;

//            if (launchUrl != null){
                Log.d("DeBug", "Launch URL: " + launchUrl);
                Intent intent = new Intent(getApplicationContext(), WebActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("launchURL",launchUrl);
                getApplicationContext().startActivity(intent);
//            }else {


//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("launchURL",launchUrl);
//                getApplicationContext().startActivity(intent);
//            }


        }

    }


    private class NewsNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customKey;

            if (data != null) {
                //While sending a Push notification from OneSignal dashboard
                // you can send an addtional data named "customkey" and retrieve the value of it and do necessary operation
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }

    }
}
