package es.crttn.movievault

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UtilsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FragmentResolution()
            1 -> FragmentBattery()
            2 -> FragmentLight()
            3 -> FragmentLocation()
            else -> FragmentResolution()
        }
    }
}

