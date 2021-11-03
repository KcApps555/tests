package com.noamkeisy.taskno2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class NotifReceive extends BroadcastReceiver {
    final static int NOTIF_WEATHER_ID = 0;
    final static int NOTIF_GENERAL_ID = 1;
    final static int NOTIF_TECHNOLOGY_ID = 2;
    final static int NOTIF_BUSINESS_ID = 3;
    int notif_id, notif_resIconId, repeat;
    String notif_title, notif_text;
    boolean notif_weather_is_on = false, notif_general_is_on = false, notif_technology_is_on = false, notif_business_is_on = false;
    String TAG = "NotifService";
    SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"on Receive");

        sp = context.getSharedPreferences("general", MODE_PRIVATE);
        notif_weather_is_on = sp.getBoolean("notif_weather_is_on", false);
        notif_general_is_on = sp.getBoolean("notif_general_is_on", false);
        notif_technology_is_on = sp.getBoolean("notif_technology_is_on", false);
        notif_business_is_on = sp.getBoolean("notif_business_is_on", false);
        repeat = sp.getInt("repeat", 0);

        if(notif_weather_is_on) {
            notif_resIconId = sp.getInt("notif_weather_resIconId",0);
            notif_title = sp.getString("notif_weather_title", "");
            notif_text = sp.getString("notif_weather_text", "");
            sendNotif(context, NOTIF_WEATHER_ID, notif_resIconId, notif_title, notif_text);
            sendAlarm(context, 0, repeat);
        }
        if(notif_general_is_on) {
            notif_resIconId = sp.getInt("notif_general_resIconId",0);
            notif_title = sp.getString("notif_general_title", "");
            notif_text = sp.getString("notif_general_text", "");
            sendNotif(context, NOTIF_GENERAL_ID, notif_resIconId, notif_title, notif_text);
            sendAlarm(context, 1, repeat);
        }
        if(notif_technology_is_on) {
            notif_resIconId = sp.getInt("notif_technology_resIconId",0);
            notif_title = sp.getString("notif_technology_title", "");
            notif_text = sp.getString("notif_technology_text", "");
            sendNotif(context, NOTIF_TECHNOLOGY_ID, notif_resIconId, notif_title, notif_text);
            sendAlarm(context, 2, repeat);
        }
        if(notif_business_is_on) {
            notif_resIconId = sp.getInt("notif_business_resIconId",0);
            notif_title = sp.getString("notif_business_title", "");
            notif_text = sp.getString("notif_business_text", "");
            sendNotif(context, NOTIF_BUSINESS_ID, notif_resIconId, notif_title, notif_text);
            sendAlarm(context, 3, repeat);
        }
    }

    public void sendNotif(Context context, int notif_id, int notif_resIconId, String notif_title, String notif_text) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notifManager = context.getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyNotifications");
        builder.setSmallIcon(notif_resIconId)
                .setContentTitle(notif_title)
                .setContentText(notif_text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        Intent intent = new Intent(context, MainActivity.class);

        switch(notif_id) {
            case NOTIF_WEATHER_ID:
                intent.putExtra("notif_callback", 0);
                PendingIntent contentIntent0 = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent0);
                break;
            case NOTIF_GENERAL_ID:
                intent.putExtra("notif_callback", 1);
                PendingIntent contentIntent1 = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent1);
                break;
            case NOTIF_TECHNOLOGY_ID:
                intent.putExtra("notif_callback", 2);
                PendingIntent contentIntent2 = PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent2);
                break;
            case NOTIF_BUSINESS_ID:
                intent.putExtra("notif_callback", 3);
                PendingIntent contentIntent3 = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(contentIntent3);
                break;
        }
        Notification notification = builder.build();


        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

        notifManager.notify(notif_id, notification);
    }

    public void sendAlarm(Context context, int requestCode, int repeat) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotifReceive.class);
        intent.putExtra("notif_id", notif_id)
                .putExtra("notif_resIconId", notif_resIconId)
                .putExtra("notif_title", notif_title)
                .putExtra("notif_text", notif_text);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeat*60*1000, pendingIntent);
        //Toast.makeText(context, "התזכורת הבאה בעוד " + repeat + " דקות.", Toast.LENGTH_SHORT).show();
    }
}
