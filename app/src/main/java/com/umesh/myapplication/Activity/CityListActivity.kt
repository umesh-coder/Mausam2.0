package com.umesh.myapplication.Activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.umesh.myapplication.Adapter.CityAdapter
import com.umesh.myapplication.Model.CityResponseApi
import com.umesh.myapplication.ViewModel.CityViewModel
import com.umesh.myapplication.databinding.ActivityCityListBinding
import retrofit2.Call
import retrofit2.Response

class CityListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCityListBinding
    private val cityAdapter by lazy { CityAdapter() }
    private val cityViewModel: CityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        binding.apply {
            cityEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    progressBar.visibility = View.VISIBLE
                    cityViewModel.loadCity(p0.toString(), 18)
                        .enqueue(object : retrofit2.Callback<CityResponseApi> {
                            override fun onFailure(p0: Call<CityResponseApi>, p1: Throwable) {

                            }

                            override fun onResponse(
                                p0: Call<CityResponseApi>,
                                p1: Response<CityResponseApi>
                            ) {
                                if (p1.isSuccessful) {
                                    val data = p1.body()
                                    data?.let {

                                        progressBar.visibility = View.GONE
                                        cityAdapter.differ.submitList(it)
                                        cityView.apply {
                                            layoutManager = LinearLayoutManager(
                                                this@CityListActivity,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                            adapter = cityAdapter
                                        }
                                    }
                                }
                            }


                        })
                }


            })
        }


    }
}