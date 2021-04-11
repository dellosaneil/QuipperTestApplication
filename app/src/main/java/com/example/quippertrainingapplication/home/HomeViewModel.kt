package com.example.quippertrainingapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.guardian.Response
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var publishSubject: PublishSubject<Response> = PublishSubject.create()
    private var observerLoadingState: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun getNewsArticles(): PublishSubject<Response> = publishSubject
    fun getLoadingState() : BehaviorSubject<Boolean> = observerLoadingState

    fun newsArticles(query: String,pageNumber : Int = 1, observable: rx.subjects.BehaviorSubject<String>) {
        observable.debounce(2000, TimeUnit.MILLISECONDS)
            .doOnSubscribe{
                Log.i(TAG, "newsArticles SUBSCRIBE:")
            }
            .doOnNext{
                Log.i(TAG, "newsArticles: $it")
                test(query, pageNumber)
            }.subscribe()
    }

    private fun test(query: String, pageNumber: Int) {
        repository.retrieveFromApi(query = query, pageNumber = pageNumber)
            .compose(applySchedulers())
            .doOnSubscribe { observerLoadingState.onNext(true) }
            .map { it.response }
            .doOnSuccess {
                publishSubject.onNext(it)
                observerLoadingState.onNext(false)
            }
            .doOnError {
                Log.i(TAG, "newsArticles: ${it.message}")
            }
            .subscribe()
    }



    private fun <T> applySchedulers(): SingleTransformer<T, T> {
        return SingleTransformer<T, T> {
            it.subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun <T> applySchedulersObservable(): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> {
            it.subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }


    override fun onCleared() {
        super.onCleared()

    }


}