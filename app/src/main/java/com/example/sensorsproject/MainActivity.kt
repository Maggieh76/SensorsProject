package com.example.sensorsproject

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView



class MainActivity : AppCompatActivity() {
    lateinit var sensorEventListener: SensorEventListener
    lateinit var TempSensorEventListener: SensorEventListener
    lateinit var sensorManager : SensorManager
    lateinit var spinner: Spinner
    var humiditySensor: Sensor? = null
    var temperatureSensor: Sensor? = null
    private var resume = false;
    private lateinit var breadPick : String



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
                findViewById<TextView>(R.id.temp_value).text = event!!.values[0].toString()
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
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.run{
                    breadPick = getItemAtPosition(position).toString()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(TempSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
        sensorManager.unregisterListener(TempSensorEventListener)
    }


}