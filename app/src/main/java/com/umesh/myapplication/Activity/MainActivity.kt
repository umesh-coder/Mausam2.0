package com.umesh.myapplication.Activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.umesh.myapplication.Model.CurrentResponseApi
import com.umesh.myapplication.R
import com.umesh.myapplication.ViewModel.WeatherViewModel
import com.umesh.myapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var bindding: ActivityMainBinding
    private val calendar by lazy { Calendar.getInstance() }
    private val weatherViewModel: WeatherViewModel by viewModels()
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
                                    it.wind.speed.let { Math.round(it).toString() } + "Km"
                                currentTempText.text =
                                    it.main.temp.let { Math.round(it).toString() } + "°"
                                maxTempText.text =
                                    it.main.tempMax.let { Math.round(it).toString() } + "°"
                                minTempText.text =
                                    it.main.tempMin.let { Math.round(it).toString() } + "°"

                                val drawable = if (isNightNow()) R.drawable.night_bg else {


                                }
                            }

                        }
                    }


                })


        }
    }

    private fun isNightNow(): Boolean {

        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01d" -> R.drawable.sunny_bg
            "01n" -> R.drawable.night_bg
        }
    }
}