package es.crttn.movievault

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentBattery : Fragment() {
    private lateinit var batteryStatusTextView: TextView
    private lateinit var toggleButton: Button
    private var isReceiverRegistered = false
    private lateinit var batteryReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_batttery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        batteryStatusTextView = view.findViewById(R.id.textViewBattery)
        toggleButton = view.findViewById(R.id.buttonToggle)

        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                batteryStatusTextView.text = when (action) {
                    Intent.ACTION_POWER_CONNECTED -> "Cargando"
                    Intent.ACTION_POWER_DISCONNECTED -> "No está cargando"
                    else -> "Estado desconocido"
                }
            }
        }

        toggleButton.setOnClickListener {
            if (!isReceiverRegistered) {
                requireContext().registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_POWER_CONNECTED))
                requireContext().registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED))
                isReceiverRegistered = true
            } else {
                requireContext().unregisterReceiver(batteryReceiver)
                isReceiverRegistered = false
            }
        }
    }
}
