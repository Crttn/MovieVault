package es.crttn.movievault

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentMovies : Fragment() {

    private lateinit var movieDatabaseHelper: MovieDatabaseHelper
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var movieList: MutableList<MovieItem>
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_movies, container, false)

        movieDatabaseHelper = MovieDatabaseHelper(requireContext())
        movieList = mutableListOf()

        val recyclerView: RecyclerView = rootView.findViewById(R.id.movies_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        movieAdapter = MovieAdapter(movieList, ::deleteMovie, ::editMovie)  // Pasar las funciones
        recyclerView.adapter = movieAdapter

        val btnAddMovie: Button = rootView.findViewById(R.id.add_button)
        btnAddMovie.setOnClickListener {
            showMovieDialog(isEditing = false, movieItem = null)
        }


        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userEmail = sharedPreferences.getString("user_email", null)

        loadMovies()

        return rootView
    }

    private fun loadMovies() {

        if (!userEmail.isNullOrEmpty()) {
            val movies = movieDatabaseHelper.getMoviesForUser(userEmail!!)
            if (movies.isNotEmpty()) {
                movieList.clear()
                movieList.addAll(movies)
                movieAdapter.notifyDataSetChanged()
            } else {
                showToastSafe("No tienes películas guardadas.")
            }
        } else {
            showToastSafe("Error: No se pudo obtener el correo del usuario.")
        }
    }

    private fun showToastSafe(message: String) {
        if (isAdded && !requireActivity().isFinishing) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun editMovie(movieItem: MovieItem) {
        showMovieDialog(isEditing = true, movieItem = movieItem)
    }



    private fun deleteMovie(movieItem: MovieItem, position: Int) {
        movieDatabaseHelper.deleteMovie(movieItem.id)
        movieList.removeAt(position)
        movieAdapter.notifyItemRemoved(position)
        showToastSafe("Película eliminada")
    }

    private fun showMovieDialog(isEditing: Boolean, movieItem: MovieItem?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)

        val movieTitleET: EditText = dialogView.findViewById(R.id.movieTitleET)
        val movieRankET: EditText = dialogView.findViewById(R.id.movieRankET)
        val movieImageET: EditText = dialogView.findViewById(R.id.movieImageET)

        if (isEditing && movieItem != null) {
            movieTitleET.setText(movieItem.title)
            movieRankET.setText(movieItem.rank.toString())
            movieImageET.setText(movieItem.imageUrl)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(if (isEditing) "Editar Película" else "Agregar Película")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val title = movieTitleET.text.toString()
                val rank = movieRankET.text.toString()
                val image = movieImageET.text.toString()

                if (title.isNotEmpty() && rank.isNotEmpty() && image.isNotEmpty()) {
                    try {
                        val rankInt = rank.toInt()

                        if (rankInt in 1..10) {
                            if (isEditing && movieItem != null) {
                                movieDatabaseHelper.updateMovie(movieItem.id, title, image, rankInt)
                                showToastSafe("Película actualizada")
                                val position = movieList.indexOf(movieItem)
                                movieList[position] = MovieItem(movieItem.id, title, rankInt, image)
                                movieAdapter.notifyItemChanged(position)
                            } else {
                                val newMovieId = movieDatabaseHelper.insertMovie(title, image, rankInt, userEmail ?: "")
                                showToastSafe("Película guardada")
                                movieList.add(MovieItem(newMovieId.toInt(), title, rankInt, image))
                                movieAdapter.notifyItemInserted(movieList.size - 1)
                            }
                        } else {
                            showToastSafe("Clasificación debe ser entre 1 y 10")
                        }
                    } catch (e: NumberFormatException) {
                        showToastSafe("Por favor ingrese una clasificación válida")
                    }
                } else {
                    showToastSafe("Por favor complete todos los campos")
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

}
