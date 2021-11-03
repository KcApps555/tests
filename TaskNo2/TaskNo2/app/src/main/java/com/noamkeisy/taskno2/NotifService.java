package com.noamkeisy.taskno2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

public class NotifService extends Service {
    int notif_id, notif_resIconId, repeat;
    String notif_title, notif_text;
    String TAG = "NotifService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"on create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"on start command");

        notif_id = intent.getIntExtra("notif_id",0);
        notif_resIconId = intent.getIntExtra("notif_resIconId",0);
        notif_title = intent.getStringExtra("notif_title");
        notif_text = intent.getStringExtra("notif_text");
        //repeat = intent.getIntExtra("repeat", 0);
        new NotifThread(this, intent).start();



        return super.onStartCommand(intent, flags, startId);
    }

    private class NotifThread extends Thread {
        private Context context;
        private Intent intent;

        public NotifThread(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }


        @Override
        public void run() {
            super.run();
            for(int i = 1; i < 11; i++) {
                Log.d(TAG,i+"");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            context.stopService(intent);
            //Notification//
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notifManager = getSystemService(NotificationManager.class);
                notifManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MyNotifications");
            builder.setSmallIcon(notif_resIconId)
                    .setContentTitle(notif_title)
                    .setContentText(notif_text);
            Notification notification = builder.build();

            notification.defaults = Notification.DEFAULT_ALL;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

            notifManager.notify(notif_id, notification);
            /////////////////

           /* //Alarm Manager//
            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            intent = new Intent(context, NotifService.class);
            intent.putExtra("notif_id", notif_id)
                    .putExtra("notif_resIconId", notif_resIconId)
                    .putExtra("notif_title", notif_title)
                    .putExtra("notif_text", notif_text);
            //.putExtra("repeat", Integer.parseInt(repeat.getText().toString()));
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5*1000, pendingIntent);
            //Toast.makeText(context, "Alarm set.", Toast.LENGTH_SHORT).show();
            /////////////////*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"on destroy");
    }

    /*//Notification//
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotifications");
        builder.setSmallIcon(notif_resIconId)
                .setContentTitle(notif_title)
                .setContentText(notif_text);
        Notification notification = builder.build();

        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManagerCompat notifManager = NotificationManagerCompat.from(this);

        notifManager.notify(notif_id, notification);
        /////////////////

        //Alarm Manager//
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
         intent = new Intent(this, NotifService.class);
        intent.putExtra("notif_id", notif_id)
                .putExtra("notif_resIconId", notif_resIconId)
                .putExtra("notif_title", notif_title)
                .putExtra("notif_text", notif_text);
        //.putExtra("repeat", Integer.parseInt(repeat.getText().toString()));
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5*1000, pendingIntent);
        Toast.makeText(this, "Alarm set.", Toast.LENGTH_SHORT).show();
        /////////////////*/
}
