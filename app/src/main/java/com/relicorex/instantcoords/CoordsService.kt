package com.relicorex.instantcoords

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Locale

class CoordsService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val channelId = "InstantCoordsChannelSilent"
    private val notificationId = 1337
    private var currentCoords = "Waiting for GPS fix..."

    private val mainHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateNotification(currentCoords)
            mainHandler.postDelayed(this, 15000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(notificationId, buildNotification(currentCoords))

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                15000L,
                0f,
                this
            )
        } catch (e: SecurityException) {
            Log.e("CoordsService", "Location permission missing", e)
            currentCoords = "Permission denied. Enable GPS."
            updateNotification(currentCoords)
        }

        mainHandler.post(updateRunnable)
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        val lat = String.format(Locale.US, "%.6f", location.latitude)
        val lon = String.format(Locale.US, "%.6f", location.longitude)
        currentCoords = "$lat, $lon"
        updateNotification(currentCoords)
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {
        currentCoords = "GPS is turned OFF."
        updateNotification(currentCoords)
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun buildNotification(contentText: String): Notification {
        val copyIntent = Intent(this, CopyReceiver::class.java).apply {
            action = "com.relicorex.instantcoords.COPY_ACTION"
            putExtra("coords", contentText)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, copyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Your Instant Coords (Tap to Copy)")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setSound(null)
            .build()
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, buildNotification(text))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "GPS Tracking Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows live coordinates quietly in the notification tray."
            setSound(null, null)
            enableLights(false)
            enableVibration(false)
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(updateRunnable)
        locationManager.removeUpdates(this)
    }
}