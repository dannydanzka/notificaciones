package com.roberto.notificaciones

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.PendingIntent
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var edtPeso: EditText
    private lateinit var edtEstatura: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtPeso = findViewById(R.id.edtPeso)
        edtEstatura = findViewById(R.id.edtEstatura)

        // Botón para calcular el IMC
        findViewById<Button>(R.id.btnCalcular).setOnClickListener {
            calcularIMC()
        }

        // Botón para limpiar los campos
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            limpiarCampos()
        }

        // Verificar permisos para notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }

        // Asignar listeners a los botones de notificación
        findViewById<Button>(R.id.btnBasica).setOnClickListener {
            mostrarNotificacionBasica()
        }

        findViewById<Button>(R.id.btnToque).setOnClickListener {
            mostrarNotificacionConToque()
        }

        findViewById<Button>(R.id.btnAccion).setOnClickListener {
            mostrarNotificacionConBotones()
        }

        findViewById<Button>(R.id.btnProgreso).setOnClickListener {
            mostrarNotificacionConBarraDeProgreso()
        }
    }

    // Función para calcular el IMC y lanzar la notificación
    private fun calcularIMC() {
        if (edtPeso.text.isNotEmpty() && edtPeso.text.isNotBlank() &&
            edtEstatura.text.isNotEmpty() && edtEstatura.text.isNotBlank()) {

            val peso = edtPeso.text.toString().toFloat()
            val estatura = edtEstatura.text.toString().toFloat()
            val imc = peso / (estatura * estatura)

            Toast.makeText(this, "IMC: $imc", Toast.LENGTH_SHORT).show()

            // Limpiar los campos inmediatamente después de calcular el IMC
            limpiarCampos()

            // Notificación para resultado del IMC
            if (imc > 25.0f) {
                Toast.makeText(this, "¡Estás pasado de peso!", Toast.LENGTH_SHORT).show()

                // Preparar notificación de aceptar/cancelar
                mostrarNotificacionIMC(imc, true)

            } else {
                Toast.makeText(this, "¡Estás en buen estado!", Toast.LENGTH_SHORT).show()

                // Preparar notificación de aceptar/cancelar
                mostrarNotificacionIMC(imc, false)
            }
        } else {
            Toast.makeText(this, "Campos vacíos. ¡Por favor completa los datos!", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para mostrar notificación de IMC con Aceptar o Cancelar
    private fun mostrarNotificacionIMC(imc: Float, sobrepeso: Boolean) {
        val titulo = if (sobrepeso) "Tu IMC es $imc. ¡Cuidado!" else "Tu IMC es $imc. ¡Saludable!"
        val texto = if (sobrepeso) "¿Te gustaría agendar una cita con un nutricionista?" else "¿Te gustaría más información sobre el IMC?"

        // Intent para aceptar (dirige a FormularioActivity)
        val intentAceptar = Intent(this, FormularioActivity::class.java)
        val pendingIntentAceptar = PendingIntent.getActivity(
            this, 1, intentAceptar, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para cancelar (redirige a MainActivity)
        val intentCancelar = Intent(this, MainActivity::class.java)
        val pendingIntentCancelar: PendingIntent = PendingIntent.getActivity(
            this, 2, intentCancelar, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configurar la notificación con dos botones: Aceptar y Cancelar
        val builder = NotificationCompat.Builder(this, "Canal_notificacion")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(titulo)
            .setContentText(texto)
            .addAction(R.drawable.notification_icon, getString(R.string.si), pendingIntentAceptar)
            .addAction(R.drawable.notification_icon, getString(R.string.no), pendingIntentCancelar)
            .setAutoCancel(true)  // Cierra la notificación automáticamente al hacer clic
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Mostrar la notificación
        with(NotificationManagerCompat.from(this)) {
            notify(105, builder.build())
        }
    }

    // Limpiar los campos de peso y estatura
    private fun limpiarCampos() {
        edtPeso.text.clear()
        edtEstatura.text.clear()
        edtPeso.requestFocus()
    }

    // Notificación básica
    private fun mostrarNotificacionBasica() {
        val builder = NotificationCompat.Builder(this, "Canal_notificacion")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación Básica")
            .setContentText("Esta es una notificación básica.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Esta es una notificación básica con más contenido visible si expandes la notificación."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(101, builder.build())
        }
    }

    // Notificación con acción de toque
    private fun mostrarNotificacionConToque() {
        val intent = Intent(this, PendingActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "Canal_notificacion")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación con Toque")
            .setContentText("Toca para abrir una Activity.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(102, builder.build())
        }
    }

    // Notificación con botones de acción
    private fun mostrarNotificacionConBotones() {
        val intentSi = Intent(this, PendingActivity::class.java).apply {
            putExtra("accion", 1)
        }

        val pendingIntentSi = PendingIntent.getActivity(
            this, 0, intentSi, PendingIntent.FLAG_IMMUTABLE
        )

        // Aquí asignamos el mismo PendingIntent para el botón "Cancelar"
        val builder = NotificationCompat.Builder(this, "Canal_notificacion")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Notificación con Botones")
            .setContentText("Selecciona una opción.")
            .addAction(R.drawable.notification_icon, getString(R.string.si), pendingIntentSi)
            .addAction(R.drawable.notification_icon, getString(R.string.no), pendingIntentSi) // "Cancelar" hace lo mismo que "Aceptar"
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(103, builder.build())
        }
    }

    // Notificación con barra de progreso
    private fun mostrarNotificacionConBarraDeProgreso() {
        val builder = NotificationCompat.Builder(this, "Canal_notificacion")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Descargando")
            .setContentText("Progreso de descarga")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        NotificationManagerCompat.from(this).apply {
            builder.setProgress(100, 0, false)
            notify(104, builder.build())

            // Simulación de descarga
            Thread {
                for (progress in 0..100 step 10) {
                    Thread.sleep(1000)
                    builder.setProgress(100, progress, false)
                    notify(104, builder.build())
                }
                builder.setContentText("Descarga completada")
                    .setProgress(0, 0, false)
                notify(104, builder.build())
            }.start()
        }
    }
}
