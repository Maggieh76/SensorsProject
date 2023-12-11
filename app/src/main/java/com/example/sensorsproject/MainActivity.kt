package com.example.sensorsproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView



class MainActivity : AppCompatActivity() {
    private lateinit var sensorEventListener: SensorEventListener
    private lateinit var TempSensorEventListener: SensorEventListener
    private lateinit var sensorManager: SensorManager
    private var humiditySensor: Sensor? = null
    private var temperatureSensor: Sensor? = null
    private lateinit var breadPick: String
    private val humList: List<Int> = arrayListOf(60, 80)
    private var optimalTempL: Int = 0
    private var optimalTempM: Int = 0
    var tempVal: Double = 0.0
    var humVal: Float = 0.0F
    lateinit var resultText: TextView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultText = findViewById(R.id.resultText)
        val spinner: Spinner = findViewById(R.id.typeSpinner)
        sensorManager = getSystemService(SensorManager::class.java)

        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                humVal = event!!.values[0]
                var messageH = "$humVal%"
                findViewById<TextView>(R.id.hum_value).text = messageH

            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

        }
        TempSensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                tempVal = (event!!.values[0] * 1.8) + 32
                val intTemp = tempVal.toInt()
                var messageT = "$intTemp Fahrenheit"
                findViewById<TextView>(R.id.temp_value).text = messageT
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
                    resultText.text = ""

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        findViewById<Button>(R.id.testButton).setOnClickListener {
            result(
                handlePick(breadPick),
                tempVal,
                humVal
            )
        }
        findViewById<ImageButton>(R.id.helpButton).setOnClickListener {
            // inflate the layout of the popup window
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView: View = inflater.inflate(R.layout.popup_window, null)

            // create the popup window
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it
            val popupWindow = PopupWindow(popupView, width, height, focusable)

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)

            // dismiss the popup window when touched
            popupView.setOnTouchListener { v, event ->
                popupWindow.dismiss()
                true
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

    fun handlePick(breadPick: String): List<Int> {
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
        return arrayListOf(optimalTempL, optimalTempM)

    }
    fun result(tempList: List<Int>, tempVal: Double, humVal: Float){
        var humResult = 0
        var tempResult = 0
        if (breadPick == "Choose your bread type"){
            resultText.text = "Please choose a bread type"
            resultText.setTextColor(Color.GRAY)
            return
        }
        if(humList[0]<humVal && humVal<humList[1]) humResult = 1
        if(tempVal.toInt() in tempList[0]..tempList[1]) tempResult = 1 // in range
        else if(tempVal.toInt() in tempList[0]-2..tempList[1]+2) tempResult = 2 //slightly outside range
        if(humResult == 1 && tempResult == 1) {
            resultText.text = "Result: Ideal"
            resultText.setTextColor(Color.GREEN)
        }
        if(humResult == 1 && tempResult == 2) {
            resultText.text = "Result: Okay"
            resultText.setTextColor(Color.BLUE)
        }
        if(humResult == 0 && tempResult in 1..2){
            resultText.text = "Find a Better Humidity"
            resultText.setTextColor(Color.GRAY)
        }
        if(humResult == 1 && tempResult == 0){
            resultText.text = "Find a Better Temperature"
            resultText.setTextColor(Color.GRAY)
        }
        if(humResult == 0 && tempResult == 0){
            findViewById<TextView>(R.id.textView2).text = "Result: \n Humidity and Temperature both not ideal "
            resultText.setTextColor(Color.RED)
        }
        }

    }
