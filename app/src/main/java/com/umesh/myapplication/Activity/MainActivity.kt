package com.umesh.myapplication.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.matteobattilana.weather.PrecipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var bindding: ActivityMainBinding
    private val calendar by lazy { Calendar.getInstance() }
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val forecastAdapter by lazy { ForecastAdapter() }
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        bindding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            if (lat == 0.0 && lon == 0.0) {
                lat = 17.45
                lon = 78.47
                name = "Hyderabad"
            }

            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }

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
                                    
                    }

                })


        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            getLastKnownLocation()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val lat = it.latitude
                val lon = it.longitude
                val cityName = getCityName(lat, lon)
                Log.d("Location", "Latitude: $lat, Longitude: $lon, City: $cityName")
            } ?: run {
                // Default location
                val lat = 17.45
                val lon = 78.47 // 78.47
                val cityName = "Hyderabad"
                Log.d("Location", "Default - Latitude: $lat, Longitude: $lon, City: $cityName")
            }
        }
    }

    private fun getCityName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        return if (addresses != null && addresses.isNotEmpty()) {
            addresses[0].locality ?: "Unknown"
        } else {
            "Unknown"
        }
    }


}