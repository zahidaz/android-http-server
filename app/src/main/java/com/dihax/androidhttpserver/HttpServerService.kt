package com.dihax.androidhttpserver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dihax.androidhttpserver.server.CIOEmbeddedServer
import com.dihax.androidhttpserver.server.buildServer
import com.dihax.androidhttpserver.server.getLocalIpAddress

class HttpServerService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "HttpServerChannel"
        const val ACTION_START_SERVER = "START_SERVER"
        const val ACTION_STOP_SERVER = "STOP_SERVER"
        const val EXTRA_PORT = "port"
    }

    private val binder = LocalBinder()
    private var server: CIOEmbeddedServer? = null
    private var currentPort = 8080
    private var isRunning = false

    inner class LocalBinder : Binder() {
        fun getService(): HttpServerService = this@HttpServerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVER -> {
                val port = intent.getIntExtra(EXTRA_PORT, 8080)
                startServer(port)
            }

            ACTION_STOP_SERVER -> {
                stopServer()
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun startServer(port: Int) {
        try {
            stopServer()
            currentPort = port
            server = buildServer(port)
            server?.start(wait = false)
            isRunning = true
            
            // Try to start as foreground service, fall back to regular notification if restricted
            try {
                showNotification("Server running on http://${getLocalIpAddress()}:$port")
            } catch (e: Exception) {
                // If foreground service fails, just update the running state
                // The notification will be shown when the service is properly started
            }
        } catch (e: Exception) {
            isRunning = false
            try {
                showNotification("Failed to start server: ${e.message}")
            } catch (notificationError: Exception) {
                // If notification also fails, just log the error
            }
        }
    }

    private fun stopServer() {
        server?.stop(1000, 2000)
        server = null
        isRunning = false
        showNotification("Server stopped")
    }

    fun isServerRunning(): Boolean = isRunning

    fun getServerUrl(): String = "http://${getLocalIpAddress()}:$currentPort"

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HTTP Server",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HTTP Server")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(isRunning)
            .build()

        try {
            if (isRunning) {
                startForeground(NOTIFICATION_ID, notification)
            } else {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            // If startForeground fails due to restrictions, try regular notification
            try {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            } catch (e2: Exception) {
                // If both fail, we can't show notifications
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }
}