package com.roberto.notificaciones

import android.os.Bundle
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
        val mensaje = when (accion) {
            1 -> "Seleccionaste Aceptar"
            2 -> "Seleccionaste Cancelar"
            else -> "Acción al tocar la Activity"
        }

        findViewById<TextView>(R.id.txtPending).text = mensaje
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

        // Botón para regresar a la pantalla anterior
        findViewById<Button>(R.id.btnRegresar).setOnClickListener {
            finish()  // Regresa a la pantalla anterior
        }

        // Eliminar notificaciones
        NotificationManagerCompat.from(this).cancelAll()
    }
}
