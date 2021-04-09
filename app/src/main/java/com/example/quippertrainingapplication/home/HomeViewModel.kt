package com.example.quippertrainingapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.NewsArticles
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    fun newsArticles(): Observable<NewsArticles>? {
        val answer: Observable<NewsArticles>? = null
        repository.retrieveFromApi()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(responseObserver)
        return answer
    }

    private val responseObserver : Observer<NewsArticles> = object : Observer<NewsArticles>{
        override fun onSubscribe(d: Disposable) {
            Log.i(TAG, "onSubscribe: ")
        }

        override fun onNext(t: NewsArticles) {
            Log.i(TAG, "onNext: $t")
        }

        override fun onError(e: Throwable) {
            Log.i(TAG, "onError: ")
        }

        override fun onComplete() {
            Log.i(TAG, "onComplete: ")
        }

    }
}