package es.crttn.movievault

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FragmentLocation : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textViewLocationInfo: TextView

    // Código de solicitud de permisos
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout de este fragmento
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos la vista y el cliente de ubicación
        textViewLocationInfo = view.findViewById(R.id.textViewLocationInfo)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Verificar y solicitar permisos de ubicación
        checkLocationPermission()
    }

    // Verificar si los permisos están concedidos
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Si los permisos están concedidos, obtener la última ubicación
            getLastKnownLocation()
        } else {
            // Si no están concedidos, solicitar los permisos
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    // Obtener la última ubicación conocida
    private fun getLastKnownLocation() {
        // Verificar nuevamente si los permisos están concedidos antes de acceder a la ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Llamar al API para obtener la última ubicación
            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity(), OnSuccessListener<Location?> { location ->
                    // Si la ubicación es válida, mostrarla
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val altitude = it.altitude
                        val bearing = it.bearing

                        // Mostrar la información de la ubicación en un TextView
                        textViewLocationInfo.text = """
                            Latitud: $latitude
                            Longitud: $longitude
                            Altitud: $altitude
                            Rumbo: $bearing
                        """.trimIndent()
                    } ?: run {
                        // Si no se obtiene ubicación, mostrar un mensaje de error
                        textViewLocationInfo.text = "No se pudo obtener la ubicación."
                    }
                })
        } else {
            // Si el permiso no está concedido, mostrar un mensaje de error
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Gestionar la respuesta de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Si el permiso fue concedido
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Obtener la última ubicación
                getLastKnownLocation()
            } else {
                // Si no se concede el permiso, mostrar un mensaje al usuario
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
