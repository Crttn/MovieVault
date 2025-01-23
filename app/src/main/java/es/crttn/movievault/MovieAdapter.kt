package es.crttn.movievault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// En tu ViewHolder (MovieViewHolder)
class MovieAdapter(
    private val movieList: MutableList<MovieItem>,
    private val onDeleteClick: (MovieItem, Int) -> Unit,
    private val onEditClick: (MovieItem) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_movie, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieItem = movieList[position]
        holder.bind(movieItem)

        // Configurar el clic largo para mostrar las opciones de editar y eliminar
        holder.itemView.setOnLongClickListener {
            showPopupMenu(it, movieItem, position)
            true
        }
    }

    private fun showPopupMenu(view: View, movieItem: MovieItem, position: Int) {
        val popupMenu = PopupMenu(view.context, view)

        // Inflar el menú
        popupMenu.menuInflater.inflate(R.menu.movie_menu, popupMenu.menu)

        // Configurar las acciones de las opciones del menú
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    onEditClick(movieItem)  // Acción de editar
                    true
                }
                R.id.action_delete -> {
                    onDeleteClick(movieItem, position)  // Acción de eliminar
                    true
                }
                else -> false
            }
        }
        // Mostrar el PopupMenu
        popupMenu.show()
    }

    override fun getItemCount(): Int = movieList.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitle: TextView = itemView.findViewById(R.id.card_view_title)
        private val movieRank: AppCompatRatingBar = itemView.findViewById(R.id.card_view_rating) // Cambiar a AppCompatRatingBar
        private val movieImage: ImageView = itemView.findViewById(R.id.card_view_image)

        fun bind(movieItem: MovieItem) {
            movieTitle.text = movieItem.title
            movieRank.rating = movieItem.rank.toFloat() // Establecer la calificación en el RatingBar
            Glide.with(itemView.context).load(movieItem.imageUrl).into(movieImage)
        }
    }
}


