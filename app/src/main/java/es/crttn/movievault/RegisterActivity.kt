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

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerEmail: EditText
    private lateinit var registerPassword: EditText
    private lateinit var registerRepeatPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    private lateinit var userDatabaseHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar el helper de la base de datos
        userDatabaseHelper = UserDatabaseHelper(this)

        // Referencias a los campos de texto y botones
        registerEmail = findViewById(R.id.registerEmailET)
        registerPassword = findViewById(R.id.registerPasswordET)
        registerRepeatPassword = findViewById(R.id.registerPasswordRepeatET)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.registerLoginButton)

        // Acción cuando el usuario hace clic en el botón de registro
        registerButton.setOnClickListener {
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            val repeatPassword = registerRepeatPassword.text.toString()

            // Verificar que las contraseñas coincidan y que los campos no estén vacíos
            if (password == repeatPassword && (email.isNotEmpty() && password.isNotEmpty() && repeatPassword.isNotEmpty())) {
                // Insertar el usuario en la base de datos SQLite
                userDatabaseHelper.insertUser(email, password)
                Toast.makeText(applicationContext, "Usuario Registrado con éxito", Toast.LENGTH_LONG).show()

                // Guardar el email y la contraseña en SharedPreferences
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("user_email", email)  // Guardar el email
                editor.putString("user_password", password)  // Guardar la contraseña
                editor.apply()  // Guardar los cambios de forma asíncrona

                // Redirigir al inicio de sesión
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(applicationContext, getString(R.string.register_error), Toast.LENGTH_LONG).show()
            }
        }

        // Acción cuando el usuario hace clic en el botón de inicio de sesión
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}
