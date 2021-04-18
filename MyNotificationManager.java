package com.android_batch_31.designdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import java.util.Random;

// To display from your application, you need following 3 things :
// 1. Notification Channel (Necessary in Oreo and above version).
// 2. Notification Builder.
// 3. Notification Manager.

public class MyNotificationManager {

    private static final String CHANNEL = "channelID";
    private static NotificationChannel channel;
    public static void addNotification(String title, String message,
                                       Context ctx, PendingIntent pintent,
                                       boolean isSound, boolean isVibrate)
    {
        createChannel(ctx);

        Bitmap bmp = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.flag);
        NotificationCompat.Builder builder = null;
        builder =
                new NotificationCompat.Builder(ctx,CHANNEL)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setLargeIcon(bmp)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bmp))
                        .setAutoCancel(true)
                        .setContentIntent(pintent).setLights(Color.parseColor("#FFFFFF"),
                        1000, 1000);

        NotificationManager notificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        if(isVibrate==true)
        {
            VibratePhone(ctx);
        }
        if(isSound==true)
        {
            //check phone is in silent mode or not
            AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
            if(isVibrate==false && (am.getRingerMode()==AudioManager.RINGER_MODE_SILENT ||
                    am.getRingerMode()==AudioManager.RINGER_MODE_VIBRATE))
                VibratePhone(ctx);
            else
            {
                MediaPlayer player = MediaPlayer.create(ctx,R.raw.bell);
                player.start();
            }
        }
        int notificationId = new Random().nextInt();
        notificationManager.notify(notificationId, builder.build());
    }

    private static void VibratePhone(Context ctx) {
        Vibrator vibe = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(1000);
    }

    public static void createChannel(Context ctx)
    {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        else
        { //26 or higher
            NotificationManager notificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            channel = new NotificationChannel(CHANNEL,"name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Description");

            notificationManager.createNotificationChannel(channel);
        }
    }
}
