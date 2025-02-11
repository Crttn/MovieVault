package es.crttn.movievault

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.main)
        navigationView = findViewById(R.id.navigation_view)

        // Cargar el nombre del usuario en el Drawer
        loadUserEmail()

        // Cargamos el fragmento de inicio en la actividad
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, FragmentMovies())
        transaction.commit()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Acción al seleccionar "Home"
                    showToast(getString(R.string.home_toast))
                    // Reemplazamos el fragmento
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentMovies()).commit()
                }
                R.id.nav_profile -> {
                    // Acción al seleccionar "Profile"
//                    showToast(getString(R.string.list_toast))
                    // Reemplazamos el fragmento
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentRecorder()).commit()
                }
                R.id.nav_settings -> {
                    // Acción al seleccionar "Settings"
                    showToast(getString(R.string.cosa_toast))
                    // Reemplazamos el fragmento
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentUtils()).commit()
                }
                R.id.nav_logout -> {
                    // Acción al seleccionar "Cerrar sesión"
                    logoutUser()
                }
            }
            // Cerrar el panel después de seleccionar una opción
            drawerLayout.closeDrawers()
            true
        }
    }

    // Mostrar mensaje Toast con la opción pulsada en el menú
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Lógica de cerrar sesión
    private fun logoutUser() {
        // Limpiar las preferencias de usuario o los datos de sesión
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Borra todos los datos guardados
        editor.apply()

        // Redirigir al usuario a la pantalla de inicio de sesión
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Finaliza la actividad principal para evitar que el usuario regrese
    }

    private fun loadUserEmail() {
        // Obtener SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Recuperar el correo del usuario (si no existe, poner un valor predeterminado)
        val userEmail = sharedPreferences.getString("user_email", "Correo no disponible")  // Valor predeterminado

        // Obtener el header del NavigationView
        val headerView = navigationView.getHeaderView(0)

        // Obtener el TextView donde se mostrará el correo
        val userEmailTextView: TextView = headerView.findViewById(R.id.drawerheaderEmail)  // Asegúrate de que el ID sea correcto

        // Establecer el texto del TextView con el correo del usuario
        userEmailTextView.text = userEmail
    }



}
