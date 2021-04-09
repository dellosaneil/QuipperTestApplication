package com.example.quippertrainingapplication.repository

import com.example.quippertrainingapplication.retrofit.RetrofitInstance

class Repository {

    fun retrieveFromApi() = RetrofitInstance.retrofitApi.retrieveFromGuardianApi()

}