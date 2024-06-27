package com.umesh.myapplication.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umesh.myapplication.Model.ForecastResponseApi
import com.umesh.myapplication.databinding.ForecastViewholderBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ViewHolder>() {
    private lateinit var binding: ForecastViewholderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ForecastViewholderBinding.inflate(inflater, parent, false)
        return ViewHolder()

    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: ForecastAdapter.ViewHolder, position: Int) {
        val binding = ForecastViewholderBinding.bind(holder.itemView)
        val date =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(differ.currentList[position].dtTxt.toString())
        val calender = Calendar.getInstance()
        calender.time = date

        val dayOfWeekName = when (calender.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> "-"
        }

        binding.nameDayText.text = dayOfWeekName
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val amPm = if (hour < 12) "am" else "pm"
        val hour12 = calender.get(Calendar.HOUR)
        binding.timeText.text = hour12.toString() + amPm
        binding.temptext.text =
            differ.currentList[position].main?.temp?.let { Math.round(it) }.toString() + "°"

        val icon = when (differ.currentList[position].weather?.get(0)?.icon.toString()) {
            "01d", "0n" -> "sunny"
            "02d", "02n" -> "cloudy_sunny"
            "03d", "03n" -> "cloudy_sunny"
            "04", "04n" -> "cloudy"
            "09d", "09n" -> "rainy"
            "10d", "10n" -> "rainy"
            "11d", "11n" -> "storm"
            "13d", "13n" -> "snowy"
            "50d", "50n" -> "windy"
            else -> "sunny"
        }

        val drawableResourceId: Int =
            binding.root.resources.getIdentifier(icon, "drawable", binding.root.context.packageName)

        Glide.with(binding.root.context)
            .load(drawableResourceId)
            .into(binding.imageView2)
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val diffCallback = object : DiffUtil.ItemCallback<ForecastResponseApi.data>() {
        override fun areContentsTheSame(
            oldItem: ForecastResponseApi.data, newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(
            oldItem: ForecastResponseApi.data, newItem: ForecastResponseApi.data
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)

}