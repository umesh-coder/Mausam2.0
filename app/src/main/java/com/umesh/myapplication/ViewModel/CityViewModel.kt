package com.umesh.myapplication.ViewModel

import androidx.lifecycle.ViewModel
import com.umesh.myapplication.Repository.CityRepository
import com.umesh.myapplication.Server.ApiClient
import com.umesh.myapplication.Server.ApiService

class CityViewModel(val cityRepository: CityRepository) : ViewModel() {
    constructor() : this(CityRepository(ApiClient().getClient().create(ApiService::class.java)))

    fun loadCity(q: String, limit: Int) =
        cityRepository.getCities(q, limit)

}