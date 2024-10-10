package com.roberto.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object Helpers {

    private const val CHANNEL_ID = "IMC_Channel"

    // Función para enviar notificaciones básicas
    fun mostrarNotificacion(context: Context, titulo: String, contenido: String) {
        crearCanalNotificacion(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(context).notify(202, builder.build())
    }

    // Función para enviar notificaciones con una acción
    fun enviarNotificacionConAccion(context: Context, destino: Class<*>, titulo: String, contenido: String) {
        crearCanalNotificacion(context)

        val intent = Intent(context, destino)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(201, builder.build())
    }

    // Función para crear el canal de notificaciones
    private fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal de IMC"
            val descriptionText = "Canal para notificaciones de IMC"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
