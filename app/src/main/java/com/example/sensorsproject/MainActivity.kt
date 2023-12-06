package com.example.sensorsproject

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView



class MainActivity : AppCompatActivity() {
    lateinit var sensorEventListener: SensorEventListener
    lateinit var TempSensorEventListener: SensorEventListener
    lateinit var sensorManager: SensorManager
    lateinit var spinner: Spinner
    var humiditySensor: Sensor? = null
    var temperatureSensor: Sensor? = null
    private var resume = false;
    private lateinit var breadPick: String
    var optimalHumL: Int = 60
    var optimalHumM: Int = 80
    var optimalTempL: Int = 0
    var optimalTempM: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById(R.id.typeSpinner)
        sensorManager = getSystemService(SensorManager::class.java)

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                findViewById<TextView>(R.id.hum_value).text = event!!.values[0].toString()

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }
        TempSensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                var tempVal = (event!!.values[0] * 1.8) + 32
                findViewById<TextView>(R.id.temp_value).text = tempVal.toString()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.breads,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.run {
                    breadPick = getItemAtPosition(position).toString()
                    handlePick(breadPick)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            sensorEventListener,
            humiditySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            TempSensorEventListener,
            temperatureSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
        sensorManager.unregisterListener(TempSensorEventListener)
    }

    fun handlePick(breadPick: String) {
        when (breadPick) {
            "Sourdough" -> {
                optimalTempL = 70
                optimalTempM = 85
                Log.d("Type", "Sourdough")
            }
            "Rye Dough" -> {
                optimalTempL = 80
                optimalTempM = 85
                Log.d("Type", "Rye Dough")
            }
            "Sweet Dough / Croissant" -> {
                optimalTempL = 75
                optimalTempM = 80
                Log.d("Type", "Sweet Dough / Croissant")
            }
            "Lean Dough" -> {
                optimalTempL = 75
                optimalTempM = 78
                Log.d("Type", "Lean Dough")
            }
            "Pre-ferments" -> {
                optimalTempL = 70
                optimalTempM = 72
                Log.d("Type", "Pre-ferments")
            }
            "Other" -> {
                optimalTempL = 81
                optimalTempM = 81
                Log.d("Type", "Other")
            }
        }

    }
}