package com.example.quippertrainingapplication.retrofit

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl("https://content.guardianapis.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val retrofitApi : RetrofitInterface by lazy{
        retrofit.create(RetrofitInterface::class.java)
    }
}