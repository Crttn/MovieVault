package es.crttn.movievault

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var loginRegisterButton: Button

    private lateinit var userDatabaseHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userDatabaseHelper = UserDatabaseHelper(this)

        loginEmail = findViewById(R.id.loginEmailET)
        loginPassword = findViewById(R.id.loginPasswordET)
        loginButton = findViewById(R.id.loginButton)
        loginRegisterButton = findViewById(R.id.loginRegisterButton)

        // Verificar si ya hay un usuario logueado
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString("user_email", null)
        val storedPassword = sharedPreferences.getString("user_password", null)

        if (storedEmail != null && storedPassword != null) {
            // Verificar si existe el usuario en la base de datos
            if (userDatabaseHelper.checkUserExists(storedEmail, storedPassword)) {
                // Si el usuario existe, iniciar sesión automáticamente
                startActivity(Intent(this, MainActivity::class.java))
                finish()  // Finalizar LoginActivity
            } else {
                // Si las credenciales son incorrectas, eliminamos las preferencias
                sharedPreferences.edit().clear().apply()
                Toast.makeText(applicationContext, getString(R.string.login_error), Toast.LENGTH_LONG).show()
            }
        }

        loginButton.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Verificar si el usuario existe en la base de datos local
                if (userDatabaseHelper.checkUserExists(email, password)) {
                    // Guardar el correo y la contraseña en SharedPreferences
                    val editor = sharedPreferences.edit()
                    editor.putString("user_email", email)  // Guardar el correo
                    editor.putString("user_password", password)  // Guardar la contraseña
                    editor.apply()  // Guardar los cambios de forma asíncrona

                    // Iniciar la actividad principal
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()  // Finalizar LoginActivity
                } else {
                    Toast.makeText(applicationContext, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Por favor, ingrese los campos", Toast.LENGTH_LONG).show()
            }
        }

        loginRegisterButton.setOnClickListener {
            // Redirigir al registro
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}

