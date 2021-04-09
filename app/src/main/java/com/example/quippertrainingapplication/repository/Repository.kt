package com.example.quippertrainingapplication.repository

import com.example.quippertrainingapplication.retrofit.RetrofitInstance

class Repository {
    fun retrieveFromApi(
        query : String = "a",
        orderBy: String = "newest",
        section : String = "news",
        pageSize : Int = 100,
        pageNumber : Int = 1,
        apiKey : String = "a73fdc06-abdc-462e-af96-3385a41235e2") = RetrofitInstance.retrofitApi.retrieveFromGuardianApi(query,orderBy, section, pageSize, pageNumber, apiKey)

}