package es.crttn.movievault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FragmentHome : Fragment() {
    private lateinit var movieDatabaseHelper: MovieDatabaseHelper
    private lateinit var userEmail: String

    // Sobrescribimos onCreateView para inflar el layout del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


}