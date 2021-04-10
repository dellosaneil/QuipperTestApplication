package com.example.quippertrainingapplication.retrofit

import com.example.quippertrainingapplication.api_data.NewsArticles
import io.reactivex.Observable
import retrofit2.Response
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
    ) : Observable<NewsArticles>

}