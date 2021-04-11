package com.example.quippertrainingapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.guardian.Response
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit


private const val TAG = "HomeViewModel"

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var publishSubject: PublishSubject<Response> = PublishSubject.create()
    private var observerLoadingState: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    fun getNewsArticles(): PublishSubject<Response> = publishSubject
    fun getLoadingState(): BehaviorSubject<Boolean> = observerLoadingState

    private val compositeDisposable = CompositeDisposable()

    fun observerSubscription(
        queryObservable: BehaviorSubject<String>,
        pageCountObservable: BehaviorSubject<Int>
    ) {
        Observable.combineLatest(queryObservable.debounce (500, TimeUnit.MILLISECONDS), pageCountObservable.distinctUntilChanged()) { query, pageCount ->
            query to pageCount
        }.doOnNext {
            retrieveFromRetrofit(it.first, it.second)
        }.doOnSubscribe {
            compositeDisposable.add(it)
        }
            .subscribe()
    }


    private fun retrieveFromRetrofit(query: String, pageNumber: Int) {
        repository.retrieveFromApi(query = query, pageNumber = pageNumber)
            .compose(applySchedulers())
            .doOnSubscribe { observerLoadingState.onNext(true) }
            .map { it.response }
            .doOnSuccess {
                Log.i(TAG, "REPOSITORY ")
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


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()

    }


}