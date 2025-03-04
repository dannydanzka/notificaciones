package com.roberto.notificaciones

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var edtPeso: EditText
    private lateinit var edtEstatura: EditText
    private val CHANNEL_ID = "IMC_Channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtPeso = findViewById(R.id.edtPeso)
        edtEstatura = findViewById(R.id.edtEstatura)

        verificarPermisosNotificaciones()

        findViewById<Button>(R.id.btnCalcular).setOnClickListener {
            calcularIMC()
        }

        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            limpiarCampos()
        }

        findViewById<Button>(R.id.btnBasica).setOnClickListener {
            Helpers.mostrarNotificacion(this, "Notificación Básica", "Este es un mensaje básico")
        }

        findViewById<Button>(R.id.btnToque).setOnClickListener {
            mostrarNotificacionConToque()
        }

        findViewById<Button>(R.id.btnAccion).setOnClickListener {
            Helpers.enviarNotificacionConAccion(this, FormularioActivity::class.java, "Acción", "Presiona para ver detalles")
        }

        findViewById<Button>(R.id.btnProgreso).setOnClickListener {
            mostrarNotificacionConBarraDeProgreso()
        }
    }

    // ✅ Verificar permisos para Android 13+
    private fun verificarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG", "Solicitud de permiso de notificación")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    // ✅ Corregido: Función para calcular el IMC y lanzar la notificación con botones
    private fun calcularIMC() {
        if (edtPeso.text.isNotEmpty() && edtEstatura.text.isNotEmpty()) {
            val peso = edtPeso.text.toString().toFloat()
            val estatura = edtEstatura.text.toString().toFloat()
            val imc = peso / (estatura * estatura)

            Toast.makeText(this, "IMC: $imc", Toast.LENGTH_SHORT).show()
            Log.d("DEBUG", "IMC calculado: $imc")

            // Lógica para decidir si es necesario agendar cita
            val sobrepeso = imc > 25.0f
            val titulo = if (sobrepeso) "Tu IMC es alto: $imc" else "Tu IMC es saludable: $imc"
            val mensaje = if (sobrepeso) "¿Quieres agendar una cita con un especialista?" else "¿Te gustaría más información?"

            mostrarNotificacionIMC(titulo, mensaje)

        } else {
            Toast.makeText(this, "Campos vacíos. ¡Por favor completa los datos!", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Función para mostrar notificación con botones de Aceptar/Cancelar
    private fun mostrarNotificacionIMC(titulo: String, mensaje: String) {
        val notificationId = 105

        // Intent para "Aceptar" -> Abre FormularioActivity
        val intentAceptar = Intent(this, FormularioActivity::class.java)
        val pendingIntentAceptar = PendingIntent.getActivity(
            this, 1, intentAceptar, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para "Cancelar" -> Solo cierra la notificación
        val intentCancelar = Intent(this, PendingActivity::class.java).apply {
            putExtra("accion", 2)  // Acción 2 = "Cancelar"
        }
        val pendingIntentCancelar = PendingIntent.getActivity(
            this, 2, intentCancelar, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .addAction(R.drawable.notification_icon, "Aceptar", pendingIntentAceptar)
            .addAction(R.drawable.notification_icon, "Cancelar", pendingIntentCancelar)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        Log.d("DEBUG", "Notificación de IMC enviada con ID: $notificationId")

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // ✅ Notificación con acción de toque
    private fun mostrarNotificacionConToque() {
        val intent = Intent(this, PendingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación con Toque")
            .setContentText("Toca para abrir una Activity.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        Log.d("DEBUG", "Enviando notificación con toque")
        with(NotificationManagerCompat.from(this)) {
            notify(102, builder.build())
        }
    }

    // ✅ Notificación con barra de progreso
    private fun mostrarNotificacionConBarraDeProgreso() {
        val notificationManager = NotificationManagerCompat.from(this)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Descargando")
            .setContentText("Progreso de descarga")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        builder.setProgress(100, 0, false)
        notificationManager.notify(104, builder.build())

        Log.d("DEBUG", "Inicio de descarga")

        Thread {
            for (progress in 0..100 step 10) {
                Thread.sleep(1000)
                builder.setProgress(100, progress, false)
                notificationManager.notify(104, builder.build())
                Log.d("DEBUG", "Progreso: $progress%")
            }
            builder.setContentText("Descarga completada")
                .setProgress(0, 0, false)
            notificationManager.notify(104, builder.build())
            Log.d("DEBUG", "Descarga finalizada")
        }.start()
    }

    // ✅ Función para limpiar los campos
    private fun limpiarCampos() {
        Log.d("DEBUG", "Limpieza de campos ejecutada")
        edtPeso.text.clear()
        edtEstatura.text.clear()
        edtPeso.requestFocus()
    }
}
