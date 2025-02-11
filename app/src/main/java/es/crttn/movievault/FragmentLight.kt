package es.crttn.movievault

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentLight : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private lateinit var textViewLightLevel: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el TextView y el SensorManager
        textViewLightLevel = view.findViewById(R.id.textViewLightLevel)
        sensorManager = requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager

        // Obtenemos el sensor de luz
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Registramos el listener para el sensor de luz
        lightSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_LIGHT) {
            return
        }

        // Obtenemos el valor de luz en lux
        val lightLevel = event.values[0]

        // Clasificamos el nivel de luz
        val lightStatus = when {
            lightLevel < 100 -> "Oscuro"
            lightLevel in 100.0..2000.0 -> "Normal"
            else -> "Brillante"
        }

        // Mostramos el nivel de luz y su clasificación en el TextView
        textViewLightLevel.text = "Nivel de luz: $lightLevel lx\nSituación: $lightStatus"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No es necesario implementar en este caso
    }

    override fun onPause() {
        super.onPause()
        // Dejamos de escuchar el sensor cuando el fragmento se pausa
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        // Volvemos a registrar el listener cuando el fragmento se reanuda
        lightSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }
}
