package com.example.quippertrainingapplication.retrofit

import com.example.quippertrainingapplication.api_data.NewsArticles
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("search")
    fun retrieveFromGuardianApi(
        @Query("q")
        query : String = "a",
        @Query("section")
        section : String ="news",
        @Query("page-size")
        pageSize : Int = 10,
        @Query("page")
        pageNumber : Int = 1,
        @Query("api-key")
        apiKey : String = "a73fdc06-abdc-462e-af96-3385a41235e2"
    ) : Observable<NewsArticles>

}