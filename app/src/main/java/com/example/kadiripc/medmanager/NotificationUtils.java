package com.example.kadiripc.medmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.kadiripc.medmanager.MainActivity;

/**
 * Created by kADIRI PC on 4/14/2018.
 */

public class NotificationUtils {
    public static final String CHANNELNAME = "name";

    NotificationUtils() {
    }


    public static void remindUserToTakeDrugs(Context context) {

        NotificationManager systemService = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNELNAME, "hey", NotificationManager.IMPORTANCE_HIGH);

            systemService.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(context, CHANNELNAME);
        notificationbuilder.setContentTitle("Medicine Reminder")
                .setContentText("A reminder to use your drugs").setSmallIcon(R.drawable.ic_launcher_background).setContentIntent(contentIntent(context)).setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationbuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        }
        systemService.notify(1, notificationbuilder.build());
    }


    public static PendingIntent contentIntent(Context context) {

        Intent startActivity = new Intent(context, MainActivity.class);

        return PendingIntent.getActivity(context, 1, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);

    }


}
