package com.example.quippertrainingapplication.retrofit

import com.example.quippertrainingapplication.api_data.crypto.Bitcoin
import com.example.quippertrainingapplication.api_data.guardian.NewsArticles
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("search")
    fun retrieveFromGuardianApi(
        @Query("q")
        query : String,
        @Query("order-by")
        orderBy : String,
        @Query("section")
        section : String,
        @Query("page-size")
        pageSize : Int,
        @Query("page")
        pageNumber : Int,
        @Query("api-key")
        apiKey : String
    ) : Single<NewsArticles>

    @GET("assets/bitcoin")
    fun retrieveFromCryptoApi() : Single<Bitcoin>

}