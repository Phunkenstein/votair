package com.montedigorno.votair;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Chris Harmon on 3/5/2016.
 * Ref: https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
 * Ref: http://developer.android.com/training/notify-user/build-notification.html#click
 * Ref: http://developer.android.com/reference/android/content/Intent.html
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                showToast(extras.getString("message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // This will pop up a simple temporary notification (or toast) on the bottom of the screen.
                // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                // This will create a notification and an Intent for that notification.
                // The Intent specifies what action should be performed, in this case, MainActivity.
                // Once the notification is built we call notify().
                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setTicker("Votair")
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle("Votair!")
                        .setContentText(message)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true)
                        .build();

                NotificationManager notificationManager2 = (NotificationManager) getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
                notificationManager2.notify(0, notification);
            }
        });
    }
}
