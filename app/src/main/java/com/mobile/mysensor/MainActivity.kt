package com.mobile.mysensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var file: File? = null

    private var acceleratorValues = ArrayList<Float>()
    private var timeValues = ArrayList<Long>()

    private lateinit var chronometer: Chronometer
    private var isWorking: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        chronometer = findViewById(R.id.chronometer)
    }

    override fun onResume() {
        super.onResume()
        mSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val acceleration = x * x + y * y + z * z

        val progressBarX: ProgressBar = findViewById(R.id.progress_bar_x)
        val progressBarXNegative: ProgressBar = findViewById(R.id.progress_bar_x_negative)
        val progressBarY: ProgressBar = findViewById(R.id.progress_bar_y)
        val progressBarYNegative: ProgressBar = findViewById(R.id.progress_bar_y_negative)
        val progressBarZ: ProgressBar = findViewById(R.id.progress_bar_z)
        val progressBarZNegative: ProgressBar = findViewById(R.id.progress_bar_z_negative)
        val progressBarAcc: ProgressBar = findViewById(R.id.progress_bar_acceleration)


        if (x > 0) progressBarX.progress = x.toInt()
        else progressBarXNegative.progress = (x * -1).toInt()
        if (y > 0) progressBarY.progress = y.toInt()
        else progressBarYNegative.progress = (y * -1).toInt()
        if (z > 0) progressBarZ.progress = z.toInt()
        else progressBarZNegative.progress = (z * -1).toInt()

        progressBarAcc.progress = acceleration.toInt()

        val valueX: TextView = findViewById(R.id.text_view_x_value)
        val valueY: TextView = findViewById(R.id.text_view_y_value)
        val valueZ: TextView = findViewById(R.id.text_view_z_value)
        val valueAcc: TextView = findViewById(R.id.text_view_acc_value)

        valueX.text = x.toString()
        valueY.text = y.toString()
        valueZ.text = z.toString()
        valueAcc.text = acceleration.toString()

        file?.let {
            acceleratorValues.add(acceleration)
            val elapsedMillis: Long = SystemClock.elapsedRealtime() - chronometer.base
            timeValues.add(elapsedMillis)
        }
    }

    fun startRecording(view: View) {
        file = FileHelper.createFile(getExternalFilesDir(null))
        if (!isWorking) {
            //chronometer starts with current time and not when the app started
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }
        isWorking = true
    }

    fun stopRecording(view: View) {
        val bufferedWriter = FileHelper.createBufferedWriter(file)
        file = null
        FileHelper.writeFile(acceleratorValues, timeValues, bufferedWriter)
        acceleratorValues.clear()
        timeValues.clear()
        if (isWorking) {
            chronometer.stop()
            //reset back to zero
            chronometer.base = SystemClock.elapsedRealtime()
        }
        isWorking = false
    }
}
