package com.pneuma.fotomarwms_grupo5.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result

import com.pneuma.fotomarwms_grupo5.MainActivity
import com.pneuma.fotomarwms_grupo5.R
import com.pneuma.fotomarwms_grupo5.network.RetrofitClient
import android.util.Log

class PedidoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val CHANNEL_ID = "pedidos_notifications"
        private const val PREFS_NAME = "pedidos_prefs"
        private const val KEY_LAST_ORDER_ID = "last_order_id"
    }

    override suspend fun doWork(): Result {
        return try {
            val response = RetrofitClient.pedidosService.getPedidosPendientes()
            if (response.isSuccessful) {
                val pedidos = response.body() ?: emptyList()
                if (pedidos.isNotEmpty()) {
                    val lastId = pedidos.maxByOrNull { it.id }?.id ?: 0

                    val savedId = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .getInt(KEY_LAST_ORDER_ID, 0)

                    if (lastId > savedId) {
                        showNotification(pedidos.size)
                        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putInt(KEY_LAST_ORDER_ID, lastId)
                            .apply()
                    }
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("PedidoWorker", "Error checking for new orders", e)
            Result.retry()
        }
    }

    private fun showNotification(count: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de Pedidos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder) // Cambiar por icono de la app si existe
            .setContentTitle("Nuevos Pedidos Pendientes")
            .setContentText("Hay $count pedidos esperando ser procesados.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}

