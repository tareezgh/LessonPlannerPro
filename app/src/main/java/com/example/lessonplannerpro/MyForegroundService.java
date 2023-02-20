package com.example.lessonplannerpro;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MyForegroundService extends Service {
    String CHANNEL_ID = "CHANNEL_SAMPLE";
    Notification.Builder NotifyBuilder;
    NotificationManager notificationManager;
    ZonedDateTime time;
    LocalDate lt;
    SharedPreferences sharedPreferences;
    Intent BroadcastIntent = new Intent();
    boolean addedNewItem;

    @Override
    public void onCreate() {
        super.onCreate();
        CreateNotificationChannel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, sendNotification(""));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    /*********/
                    lt = LocalDate.now();
                    time = ZonedDateTime.now();
                    ZonedDateTime IsraelDateTime = time.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
//                    Log.d("time now",IsraelDateTime.getHour() + ":" + IsraelDateTime.getMinute());
                    /*********/

                    addedNewItem = LessonDetails.addedNewItem;
                    // if notification true send noti
                    if (sharedPreferences.getBoolean("noti", true)) {
                        if (addedNewItem) {
                            BroadcastIntent.setAction("com.example.lessonplannerpro.CUSTOM_INTENT");
                            sendBroadcast(BroadcastIntent);
                            Log.e("TAGGG",  "  Service");
                            sendNotification("New lesson added " + IsraelDateTime.getHour() + ":" + IsraelDateTime.getMinute());

                        }
                    }



                    LessonDetails.addedNewItem = false;
//                    Log.e("TAG", addedNewItem + " addedNewItem Service");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }


    public void CreateNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

    public Notification sendNotification(String title) {
        NotifyBuilder = new Notification.Builder(this, CHANNEL_ID).setAutoCancel(true).setContentText("Service").setSmallIcon(R.drawable.ic_notification).setContentTitle(title);

        Notification notification = NotifyBuilder.build();
        notificationManager.notify(0, notification);
        return notification;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
