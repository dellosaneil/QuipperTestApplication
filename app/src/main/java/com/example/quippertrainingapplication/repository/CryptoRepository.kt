package com.example.quippertrainingapplication.repository

import com.example.quippertrainingapplication.retrofit.RetrofitInstance

class CryptoRepository {

    fun retrieveFromCryptoApiBitcoin() = RetrofitInstance.cryptoApi.retrieveFromCryptoApiBitcoin()
    fun retrieveFromCryptoApiEthereum() = RetrofitInstance.cryptoApi.retrieveFromCryptoApiEthereum()
}