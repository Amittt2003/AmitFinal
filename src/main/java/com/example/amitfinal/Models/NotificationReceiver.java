package com.example.amitfinal.Models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;

import com.example.amitfinal.Activities.MainActivity;
import com.example.amitfinal.R;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent repeatingIntent = new Intent(context, MainActivity.class);
            repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //difference between Intent and PendingIntent is that by using the first, you want to start / launch /
        // execute something NOW, while by using the second entity you want to execute that something in the future
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_background);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyAmitFinal")
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.small_notification_icon)
                    .setContentTitle("𝔸𝕞𝕚𝕥𝔽𝕚𝕟𝕒𝕝")
                    .setContentText("It`s time to practice your memory! Tap to play :)")
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(100, builder.build());
    }
}
