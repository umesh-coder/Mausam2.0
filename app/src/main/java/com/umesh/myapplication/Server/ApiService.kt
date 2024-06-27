package com.umesh.myapplication.Server

import com.umesh.myapplication.Model.CurrentResponseApi
import com.umesh.myapplication.Model.ForecastResponseApi
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather")
    fun getCurrentWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<CurrentResponseApi>


    @GET("data/2.5/forecast")
    fun getForecastWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): Call<ForecastResponseApi>
}