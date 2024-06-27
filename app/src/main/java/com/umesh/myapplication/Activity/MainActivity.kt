package com.umesh.myapplication.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.matteobattilana.weather.PrecipType
import com.umesh.myapplication.Adapter.ForecastAdapter
import com.umesh.myapplication.Model.CurrentResponseApi
import com.umesh.myapplication.Model.ForecastResponseApi
import com.umesh.myapplication.R
import com.umesh.myapplication.ViewModel.WeatherViewModel
import com.umesh.myapplication.databinding.ActivityMainBinding
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var bindding: ActivityMainBinding
    private val calendar by lazy { Calendar.getInstance() }
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val forecastAdapter by lazy { ForecastAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        bindding.apply {
            var lat = 51.10
            var lon = -0.12
            var name = "London"
            cityText.text = name
            progressBar2.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeatherData(lat, lon, "Metric")
                .enqueue(object : retrofit2.Callback<CurrentResponseApi> {
                    override fun onFailure(p0: Call<CurrentResponseApi>, p1: Throwable) {
                        Toast.makeText(this@MainActivity, p1.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        p0: Call<CurrentResponseApi>,
                        p1: Response<CurrentResponseApi>
                    ) {
                        if (p1.isSuccessful) {
                            val currentWeatherData = p1.body()
                            progressBar2.visibility = View.GONE
                            detailLayout.visibility = View.VISIBLE
                            currentWeatherData?.let {
                                statusText.text = it.weather?.get(0)?.main ?: "-"
                                windText.text =
                                    it.wind?.speed?.let { Math.round(it).toString() } + "Km"

                                humidityText.text = it.main?.humidity.toString() + "%"
                                currentTempText.text =
                                    it.main?.temp?.let { Math.round(it).toString() } + "°"
                                maxTempText.text =
                                    it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                                minTempText.text =
                                    it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                                val drawable = if (isNightNow()) R.drawable.night_bg
                                else {
                                    setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")

                                }

                                bgImageView.setImageResource(drawable)
                                setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
                            }

                        }
                    }


                })


            //setting blur view
            var radius = 10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blurView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blurView.clipToOutline = true
            }

            //forecast temp
            weatherViewModel.loadForecastWeatherData(lat, lon, "Metric")
                .enqueue(object : retrofit2.Callback<ForecastResponseApi> {
                    override fun onResponse(
                        p0: Call<ForecastResponseApi>,
                        p1: Response<ForecastResponseApi>
                    ) {
                        if (p1.isSuccessful) {
                            val forecastWeatherData = p1.body()
                            blurView.visibility = View.VISIBLE

                            forecastWeatherData?.let {
                                forecastAdapter.differ.submitList(it.list?.toMutableList())
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter

                                }
                            }
                        }
                    }

                    override fun onFailure(p0: Call<ForecastResponseApi>, p1: Throwable) {
                        TODO("Not yet implemented")
                    }

                })


        }
    }

    private fun isNightNow(): Boolean {

        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }

            else -> 0
        }
    }

    private fun setEffectRainSnow(icon: String) {
        when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)

            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)

            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)

            }


        }
    }

    private fun initWeatherView(type: PrecipType) {

        bindding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f

        }
    }
}