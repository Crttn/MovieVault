package es.crttn.movievault

// Representa un artículo de película en la base de datos o UI
data class MovieItem(
    val id: Int,                   // ID único de la película
    val title: String,             // Título de la película
    val rank: Int,                 // Clasificación/ranking de la película
    val imageUrl: String           // URL de la imagen de la película
)
