package com.ahmed.bydvoice.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ahmed.bydvoice.MainActivity
import com.ahmed.bydvoice.R

object UpdateNotifier {

    private const val CHANNEL_ID = "app_updates"
    private const val NOTIFICATION_ID = 1001

    fun notifyUpdateAvailable(context: Context, versionName: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notify_update_title),
                NotificationManager.IMPORTANCE_HIGH,
            )
            nm.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val launchIntent: Intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?: Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_update)
            .setContentTitle(context.getString(R.string.notify_update_title))
            .setContentText(context.getString(R.string.notify_update_body, versionName))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        nm.notify(NOTIFICATION_ID, notification)
    }
}
