package com.example.quippertrainingapplication.home

import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.Response
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var behaviorSubject : BehaviorSubject<Response>  = BehaviorSubject.create()

    fun getNewsArticles() : BehaviorSubject<Response> = behaviorSubject

    fun newsArticles(query: String, pageNumber : Int = 1){
        repository.retrieveFromApi(query = query, pageNumber = pageNumber)
            .subscribeOn(io())
            .map { it.response }
            .distinct { it.results }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                behaviorSubject.onNext(it)
            }
            .subscribe()
    }



}