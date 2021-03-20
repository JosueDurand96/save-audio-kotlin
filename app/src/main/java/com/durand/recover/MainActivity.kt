package com.durand.recover

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    var FILE_RECORDING = ""

    val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    val PERMISSION_REQUEST_CODE = 100

    var buttonPlayRecording: Button? = null
    var buttonRecord: Button? = null
    var buttonStop: Button? = null
    var buttonPause:Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonPlayRecording = findViewById(R.id.buttonPlayRecording)
        buttonRecord = findViewById(R.id.buttonRecord)
        buttonStop = findViewById(R.id.buttonStop)
        buttonPause = findViewById(R.id.buttonPause)
        FILE_RECORDING = "${externalCacheDir!!.absolutePath}/recorder.mp3"

        setButtonRecordListener()
        setButtonPlayRecordingListener()
        enableDisableButtonPlayRecording()
        setStopAudio()
    }



    override fun onStart() {
        permission()
        super.onStart()
    }


    private fun permission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                record()
            }
        }
    }

    private fun enableDisableButtonPlayRecording() {
        buttonPlayRecording!!.isEnabled = doesFileExist()
    }

    private fun doesFileExist(): Boolean {
        val file = File(FILE_RECORDING)
        return file.exists()
    }

    private fun setButtonRecordListener() {
        buttonRecord!!.setOnClickListener {
            record()
        }
    }

    private fun setButtonPlayRecordingListener() {
        buttonPlayRecording!!.setOnClickListener {
            if (buttonPlayRecording!!.text.toString()
                    .equals(getString(R.string.playRecord), true)
            ) {
                buttonPlayRecording!!.text = getString(R.string.stopPlayingRecord)
                playRecording()
            } else {
                buttonPlayRecording!!.text = getString(R.string.playRecord)
                stopPlayingRecording()
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkSelfPermission(
            AUDIO_PERMISSION
        ) == PERMISSION_GRANTED
        else return true

    }

    private fun requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(AUDIO_PERMISSION), PERMISSION_REQUEST_CODE)
        }
    }

    private fun record() {
        if (!isPermissionGranted()) {
            requestAudioPermission()
            return
        }
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mediaRecorder!!.setOutputFile(FILE_RECORDING)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
    }


    private fun setStopAudio() {
        mediaPlayer = MediaPlayer()
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun playRecording() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(FILE_RECORDING)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        mediaPlayer!!.setOnCompletionListener {
            buttonPlayRecording!!.text = getString(R.string.playRecord)
        }
    }

    private fun stopPlayingRecording() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}