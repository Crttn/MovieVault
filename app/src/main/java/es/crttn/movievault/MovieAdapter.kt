package es.crttn.movievault

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val movieList: List<MovieItem>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieItem = movieList[position]
        holder.bind(movieItem)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val movieTitle: TextView = itemView.findViewById(R.id.card_view_title)
        private val movieRank: AppCompatRatingBar = itemView.findViewById(R.id.card_view_rating)
        private val movieImage: ImageView = itemView.findViewById(R.id.card_view_image)

        fun bind(movieItem: MovieItem) {
            movieTitle.text = movieItem.title
            movieRank.rating = movieItem.rank.toFloat() // Set the rating to RatingBar
            Glide.with(itemView.context).load(movieItem.imageUrl).into(movieImage)
        }
    }

}
