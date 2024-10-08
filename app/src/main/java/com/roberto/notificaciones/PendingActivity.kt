package com.roberto.notificaciones

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class PendingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending)

        // Muestra un mensaje según la acción recibida
        val accion = intent.getIntExtra("accion", -1)
        val mensaje = if (accion == 1) "Seleccionaste Aceptar" else "Seleccionaste Cancelar"
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

        // Elimina las notificaciones
        NotificationManagerCompat.from(this).cancelAll()
    }
}
