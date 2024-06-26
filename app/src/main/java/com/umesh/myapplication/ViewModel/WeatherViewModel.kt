package com.umesh.myapplication.ViewModel

import androidx.lifecycle.ViewModel
import com.umesh.myapplication.Repository.WeatherRepository
import com.umesh.myapplication.Server.ApiClient
import com.umesh.myapplication.Server.ApiService

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

constructor():this(WeatherRepository(ApiClient().getClient().create(ApiService::class.java)))

    fun loadCurrentWeatherData(lat: Double, lon: Double, units: String) =
        repository.getCurrentWeatherData(lat, lon, units)


}