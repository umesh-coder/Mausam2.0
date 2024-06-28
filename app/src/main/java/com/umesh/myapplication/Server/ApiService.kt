package com.umesh.myapplication.Server

import com.umesh.myapplication.Model.CityResponseApi
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


    //    geo/1.0/direct
    @GET("geo/1.0/direct")
    fun getCityListData(
        @Query("q") q: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Call<CityResponseApi>
}
