package com.roberto.notificaciones

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class PendingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending)

        val accion = intent.getIntExtra("accion", -1)
        Log.d("DEBUG", "Intent recibido con acción: $accion")

        val mensaje = when (accion) {
            1 -> "Seleccionaste Aceptar"
            2 -> "Seleccionaste Cancelar"
            else -> "Acción al tocar la Activity"
        }

        val txtPending = findViewById<TextView>(R.id.txtPending)
        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        txtPending?.text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

        btnRegresar?.setOnClickListener {
            finish()  // Regresa a la pantalla anterior
        }

        // Eliminar solo la notificación específica
        val notificationId = when (accion) {
            1 -> 105  // ID de notificación para aceptar
            2 -> 106  // ID de notificación para cancelar
            else -> 201 // Otra notificación
        }

        Log.d("DEBUG", "Cancelando notificación con ID: $notificationId")
        NotificationManagerCompat.from(this).cancel(notificationId)
    }
}
