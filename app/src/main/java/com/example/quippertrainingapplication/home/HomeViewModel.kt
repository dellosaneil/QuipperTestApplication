package com.example.quippertrainingapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.Response
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var publishSubject: PublishSubject<Response> = PublishSubject.create()
    private var observerLoadingState: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    fun getNewsArticles(): PublishSubject<Response> = publishSubject
    fun getLoadingState() : BehaviorSubject<Boolean> = observerLoadingState

    fun newsArticles(query: String, pageNumber: Int = 1) {
        repository.retrieveFromApi(query = query, pageNumber = pageNumber)
            .subscribeOn(io())
            .doOnSubscribe { observerLoadingState.onNext(true) }
            .map { it.response }
            .distinct()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                publishSubject.onNext(it)
                observerLoadingState.onNext(false)
            }
            .doOnError {
                Log.i(TAG, "newsArticles: ${it.message}")
            }
            .subscribe()
    }
}