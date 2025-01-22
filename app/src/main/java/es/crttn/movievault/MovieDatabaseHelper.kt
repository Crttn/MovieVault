package es.crttn.movievault

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MovieDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movieVault.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MOVIES = "movies"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_RANK = "rank"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_USER_EMAIL = "user_email"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableSQL = """
            CREATE TABLE $TABLE_MOVIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_RANK INTEGER,
                $COLUMN_IMAGE TEXT,
                $COLUMN_USER_EMAIL TEXT
            )
        """
        db?.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    // Método para obtener las películas de un usuario específico
    fun getMoviesForUser(userEmail: String): List<MovieItem> {
        val movieList = mutableListOf<MovieItem>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.query(
                TABLE_MOVIES,
                null, // Seleccionamos todas las columnas
                "$COLUMN_USER_EMAIL = ?", // Filtro para el correo del usuario
                arrayOf(userEmail),
                null, null, null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val idIndex = it.getColumnIndex(COLUMN_ID)
                    val titleIndex = it.getColumnIndex(COLUMN_TITLE)
                    val rankIndex = it.getColumnIndex(COLUMN_RANK)
                    val imageIndex = it.getColumnIndex(COLUMN_IMAGE)

                    if (idIndex >= 0 && titleIndex >= 0 && rankIndex >= 0 && imageIndex >= 0) {
                        do {
                            val id = it.getInt(idIndex)
                            val title = it.getString(titleIndex)
                            val rank = it.getInt(rankIndex)
                            val image = it.getString(imageIndex)

                            movieList.add(MovieItem(id, title, rank, image))
                        } while (it.moveToNext())
                    } else {
                        Log.e("MovieDatabaseHelper", "Una o más columnas no existen en la tabla.")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MovieDatabaseHelper", "Error al obtener películas: ${e.message}")
        }

        return movieList
    }

    // Método para insertar una nueva película con transacción
    fun insertMovie(title: String, image: String, rank: Int, userEmail: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_RANK, rank)
            put(COLUMN_IMAGE, image)
            put(COLUMN_USER_EMAIL, userEmail)
        }

        try {
            db.beginTransaction()
            db.insert(TABLE_MOVIES, null, values)
            db.setTransactionSuccessful()  // Confirmar la transacción
        } catch (e: Exception) {
            Log.e("MovieDatabaseHelper", "Error al insertar película: ${e.message}")
        } finally {
            db.endTransaction()  // Finalizar la transacción
        }
    }

    // Método para actualizar una película con transacción
    fun updateMovie(id: Int, title: String, image: String, rank: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_RANK, rank)
            put(COLUMN_IMAGE, image)
        }

        try {
            db.beginTransaction()
            db.update(TABLE_MOVIES, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("MovieDatabaseHelper", "Error al actualizar película: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    // Método para eliminar una película
    fun deleteMovie(id: Int) {
        val db = writableDatabase

        try {
            db.beginTransaction()
            db.delete(TABLE_MOVIES, "$COLUMN_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("MovieDatabaseHelper", "Error al eliminar película: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }
}
