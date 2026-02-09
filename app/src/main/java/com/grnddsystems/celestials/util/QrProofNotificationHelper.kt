package com.grnddsystems.celestials.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.grnddsystems.celestials.R

object ProofNotificationHelper {
    private const val CHANNEL_ID = "proof_generation_channel"
    private const val QR_NOTIFICATION_ID = 1001
    private const val PASSPORT_NOTIFICATION_ID = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Proof Generation"
            val descriptionText = "Notifications for ongoing proof generation"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, notificationId: Int, title: String, text: String) {
        createNotificationChannel(context)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_rarime)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    // QR Proof notifications
    fun showQrProofNotification(context: Context) {
        showNotification(
            context,
            QR_NOTIFICATION_ID,
            "In progress…",
            "Please return to complete the verification"
        )
    }

    fun cancelQrProofNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(QR_NOTIFICATION_ID)
    }

    // Passport Proof notifications
    fun showPassportProofNotification(context: Context) {
        showNotification(
            context,
            PASSPORT_NOTIFICATION_ID,
            "In progress…",
            "Please return to complete the verification"
        )
    }

    fun cancelPassportProofNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(PASSPORT_NOTIFICATION_ID)
    }

    // Cancel all
    fun cancelAllNotifications(context: Context) {
        cancelQrProofNotification(context)
        cancelPassportProofNotification(context)
    }
}