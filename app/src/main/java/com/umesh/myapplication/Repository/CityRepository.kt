package com.umesh.myapplication.Repository

import com.umesh.myapplication.Server.ApiService

class CityRepository(val apiService: ApiService) {

    fun getCities(q: String, limit: Int) =
        apiService.getCityListData(q, limit, "7e61581e9aefe072a7dd721f4ae4b762")

}