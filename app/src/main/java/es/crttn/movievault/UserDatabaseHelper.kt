package es.crttn.movievault

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createTableQuery = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_EMAIL TEXT NOT NULL,
                    $COLUMN_PASSWORD TEXT NOT NULL
                )
            """
            db.execSQL(createTableQuery)
        } catch (e: Exception) {
            Log.e("UserDatabaseHelper", "Error al crear la tabla: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        } catch (e: Exception) {
            Log.e("UserDatabaseHelper", "Error al actualizar la base de datos: ${e.message}")
        }
    }

    // Insertar un nuevo usuario
    fun insertUser(email: String, password: String) {
        val db = writableDatabase
        try {
            db.beginTransaction() // Iniciar la transacción
            val values = ContentValues().apply {
                put(COLUMN_EMAIL, email)
                put(COLUMN_PASSWORD, password)
            }
            db.insert(TABLE_NAME, null, values)
            db.setTransactionSuccessful() // Marcar la transacción como exitosa
        } catch (e: Exception) {
            Log.e("UserDatabaseHelper", "Error al insertar el usuario: ${e.message}")
        } finally {
            db.endTransaction() // Finalizar la transacción
            db.close() // Asegurarse de cerrar la base de datos
        }
    }

    // Verificar si el usuario existe en la base de datos
    fun checkUserExists(email: String, password: String): Boolean {
        val db = readableDatabase
        var userExists = false
        val cursor = try {
            val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
            db.rawQuery(query, arrayOf(email, password))
        } catch (e: Exception) {
            Log.e("UserDatabaseHelper", "Error al verificar si el usuario existe: ${e.message}")
            null
        }

        cursor?.let {
            userExists = it.moveToFirst()
            it.close()
        }
        db.close()
        return userExists
    }

    // Obtener el usuario actualmente logueado
    fun getLoggedInUser(): String? {
        val db = readableDatabase
        var loggedInUser: String? = null
        val cursor = try {
            db.rawQuery("SELECT $COLUMN_EMAIL FROM $TABLE_NAME WHERE is_logged_in = 1 LIMIT 1", null)
        } catch (e: Exception) {
            Log.e("UserDatabaseHelper", "Error al obtener el usuario logueado: ${e.message}")
            null
        }

        cursor?.let {
            if (it.moveToFirst()) {
                loggedInUser = it.getString(0)
            }
            it.close()
        }
        db.close()
        return loggedInUser
    }
}