package es.crttn.movievault

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException

class FragmentRecorder : Fragment() {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""
    private lateinit var btnRecord: Button
    private lateinit var btnStop: Button
    private lateinit var fabPlay: View
    private lateinit var seekBarVolume: SeekBar
    private var isRecording = false
    private var lastAudioFilePath: String? = null
    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recorder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRecord = view.findViewById(R.id.btnRecord)
        btnStop = view.findViewById(R.id.btnStop)
        fabPlay = view.findViewById(R.id.fabPlay)
        seekBarVolume = view.findViewById(R.id.seekBarVolume)

        if (!checkPermissions()) requestPermissions()

        btnRecord.setOnClickListener { startRecording() }
        btnStop.setOnClickListener { stopRecording() }
        fabPlay.setOnClickListener { lastAudioFilePath?.let { playAudio(it) } }
    }

    private fun startRecording() {
        if (isRecording) {
            Snackbar.make(btnRecord, "Ya estás grabando", Snackbar.LENGTH_SHORT).show()
            return
        }

        val userDirectory = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "user1")
        if (!userDirectory.exists()) userDirectory.mkdirs()

        audioFilePath = "${userDirectory.absolutePath}/audio_${System.currentTimeMillis()}.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            isRecording = true
            lastAudioFilePath = audioFilePath
            Snackbar.make(btnRecord, "Grabación iniciada", Snackbar.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Snackbar.make(btnRecord, "Error al iniciar la grabación", Snackbar.LENGTH_SHORT).show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Snackbar.make(btnRecord, "Error: No se puede acceder al micrófono", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        if (!isRecording) return

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            Snackbar.make(btnStop, "Grabación guardada", Snackbar.LENGTH_SHORT).show()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Snackbar.make(btnStop, "Error al detener la grabación", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun playAudio(filePath: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                setVolume(seekBarVolume.progress / 10f, seekBarVolume.progress / 10f)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
                Snackbar.make(fabPlay, "Error al reproducir", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(btnRecord, "Permiso concedido", Snackbar.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaRecorder?.release()
        mediaPlayer?.release()
    }
}
