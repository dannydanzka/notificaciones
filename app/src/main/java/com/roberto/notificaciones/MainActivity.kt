package com.roberto.notificaciones

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "Canal_notificacion"
    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar y solicitar permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            }
        }

        // Crear canal de notificación
        createNotificationChannel()

        // Asignar listeners a los botones
        findViewById<Button>(R.id.btnBasica).setOnClickListener {
            showBasicNotification()
        }

        findViewById<Button>(R.id.btnToque).setOnClickListener {
            showNotificationWithTapAction()
        }

        findViewById<Button>(R.id.btnAccion).setOnClickListener {
            showNotificationWithActions()
        }

        findViewById<Button>(R.id.btnProgreso).setOnClickListener {
            showProgressNotification()
        }
    }

    // Método para crear el canal de notificación
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Canal de Notificaciones"
            val descriptionText = "Descripción del canal"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Método para manejar la notificación básica
    private fun showBasicNotification() {
        val notificationId = 101
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación Básica")
            .setContentText("Esta es una notificación básica")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // Método para manejar la notificación con acción de toque
    private fun showNotificationWithTapAction() {
        val notificationId = 102
        val intent = Intent(this, PendingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación con Toque")
            .setContentText("Toque para abrir la Activity")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // Método para manejar la notificación con botones de acción
    private fun showNotificationWithActions() {
        val notificationId = 103
        val intentSi = Intent(this, PendingActivity::class.java).apply {
            putExtra("accion", 1)
        }
        val intentNo = Intent(this, PendingActivity::class.java).apply {
            putExtra("accion", 2)
        }

        val pendingIntentSi = PendingIntent.getActivity(
            this, 0, intentSi, PendingIntent.FLAG_IMMUTABLE
        )
        val pendingIntentNo = PendingIntent.getActivity(
            this, 0, intentNo, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación con Acciones")
            .setContentText("Selecciona una opción")
            .addAction(R.drawable.notification_icon, getString(R.string.si), pendingIntentSi)
            .addAction(R.drawable.notification_icon, getString(R.string.no), pendingIntentNo)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // Método para manejar la notificación con barra de progreso
    private fun showProgressNotification() {
        val notificationId = 104
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Descargando")
            .setContentText("Progreso de descarga")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        NotificationManagerCompat.from(this).apply {
            builder.setProgress(100, 0, false)
            notify(notificationId, builder.build())

            // Simulación de descarga en un hilo separado
            Thread {
                for (progress in 0..100 step 10) {
                    Thread.sleep(1000)
                    builder.setProgress(100, progress, false)
                    notify(notificationId, builder.build())
                }
                builder.setContentText("Descarga completada")
                    .setProgress(0, 0, false)
                notify(notificationId, builder.build())
            }.start()
        }
    }

    // Sobrescribir el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue otorgado, continuar con las notificaciones
            } else {
                // El permiso fue denegado
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
