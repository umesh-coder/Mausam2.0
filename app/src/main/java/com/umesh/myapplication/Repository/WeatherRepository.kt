package com.umesh.myapplication.Repository

import com.umesh.myapplication.Server.ApiService

class WeatherRepository(val api: ApiService) {

    fun getCurrentWeatherData(lat: Double, lon: Double, units: String) =
        api.getCurrentWeatherData(lat, lon, units, "7e61581e9aefe072a7dd721f4ae4b762")


    fun getForecastWeatherData(lat: Double, lon: Double, units: String) =
        api.getForecastWeatherData(lat, lon, units, "7e61581e9aefe072a7dd721f4ae4b762")

}