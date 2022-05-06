package hu.bme.aut.android.tesislifebuddy.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import hu.bme.aut.android.tesislifebuddy.MainActivity
import hu.bme.aut.android.tesislifebuddy.R

class NotificationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "NotificationServiceChannel"
    }

    override fun onBind(p0: Intent?): IBinder = ServiceNotificationBinder()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notification = intent.getStringExtra("NOTIFICATION")
        startForeground(NOTIFICATION_ID, createNotification(notification ?: "Error"))

        return START_STICKY
    }

    private fun createNotification(text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        createNotificationChannel()

        val contentIntent = PendingIntent.getActivity(this,
            NOTIFICATION_ID,
            notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tesis Life Buddy")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(contentIntent)
            .build()
    }

    fun updateNotification(text: String) {
        val notification = createNotification(text)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    inner class ServiceNotificationBinder : Binder() {
        val service: NotificationService
            get() = this@NotificationService
    }
}