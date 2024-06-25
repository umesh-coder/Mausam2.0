package com.umesh.myapplication.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import com.umesh.myapplication.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherView = findViewById<WeatherView>(R.id.weather_view)
        weatherView.setWeatherData(PrecipType.SNOW)
    }
}