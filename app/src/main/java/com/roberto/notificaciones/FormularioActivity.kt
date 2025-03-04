package com.roberto.notificaciones

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FormularioActivity : AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtApellidos: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        edtNombre = findViewById(R.id.edtNombre)
        edtApellidos = findViewById(R.id.edtApellidos)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            registrarCita()
        }

        findViewById<Button>(R.id.btnRegresar).setOnClickListener {
            finish()
        }
    }

    private fun registrarCita() {
        val nombre = edtNombre.text.toString()
        val apellidos = edtApellidos.text.toString()

        if (nombre.isNotBlank() && apellidos.isNotBlank()) {
            Toast.makeText(this, "Cita registrada a nombre de $nombre $apellidos", Toast.LENGTH_LONG).show()

            // Notificación corregida
            Helpers.enviarNotificacionConAccion(
                this, InfoActivity::class.java,
                "Registro exitoso", "Cita registrada correctamente. Haz clic para ver más información."
            )

            limpiarCampos()
        } else {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        edtNombre.text.clear()
        edtApellidos.text.clear()
        edtNombre.requestFocus()
    }
}
