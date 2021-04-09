package com.example.quippertrainingapplication.home

import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.Response
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.Observable


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    fun newsArticles(query : String = "a"): Observable<Response> {
        return repository.retrieveFromApi(query = query)
            .map { it.response }
            .distinct { it.results }
    }
}