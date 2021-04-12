package com.example.quippertrainingapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.guardian.NewsArticles
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
        val observable = Observable.combineLatest(
            queryObservable.debounce(500, TimeUnit.MILLISECONDS),
            pageCountObservable.distinctUntilChanged()
        ) { query, pageCount ->
            query to pageCount
        }
            .observeOn(io())
            .compose(repositoryTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.response }
            .doOnNext {
                publishSubject.onNext(it)
                observerLoadingState.onNext(false)
            }
            .doOnError {
                Log.i(TAG, "newsArticles: ${it.message}")
            }
            .subscribe({
                Log.i(TAG, "observerSubscription: $it")
            }, {
                Log.i(TAG, "observerSubscription: ${it.message}")
            })
        compositeDisposable.add(observable)
    }

    private fun repositoryTransformer(): ObservableTransformer<Pair<String, Int>, NewsArticles> {
        return ObservableTransformer<Pair<String, Int>, NewsArticles>{
            it.flatMap{ queryDetails ->
                repository.retrieveFromApi(query = queryDetails.first, pageNumber = queryDetails.second).toObservable()
            }
        }
    }

    private fun <T> applyObservableSchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> {
            it.subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}