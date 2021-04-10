package com.example.quippertrainingapplication.repository

import com.example.quippertrainingapplication.retrofit.RetrofitInstance

class CryptoRepository {

    fun retrieveFromCryptoApi() = RetrofitInstance.cryptoApi.retrieveFromCryptoApi()
}