package com.example.quippertrainingapplication.cryptocurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.crypto.Bitcoin
import com.example.quippertrainingapplication.repository.CryptoRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "retrieveDataFromApi()"

class CompareViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val cryptoStatus : PublishSubject<List<Bitcoin>> = PublishSubject.create()
    private val cryptoList = mutableListOf<Bitcoin>()


    fun retrieveCryptoStatus() = cryptoStatus

    init {
        Observable.interval(3, java.util.concurrent.TimeUnit.SECONDS)
            .subscribeOn(io())
            .doOnSubscribe {
                retrieveDataFromApi()
            }
            .doOnNext {
                retrieveDataFromApi()
            }
            .doOnError {
                Log.i(TAG, "${it.message}: ")
            }
            .subscribe()
    }

    private fun retrieveDataFromApi(){
        repository.retrieveFromCryptoApi()
            .subscribeOn(io())
            .doOnSuccess {
                cryptoList.add(it)
                cryptoStatus.onNext(cryptoList)
            }
            .doOnError { Log.i(TAG, "retrieveDataFromApi: ${it.message}") }
            .subscribe()
    }
    private fun Long.toDate() = SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(Date(this))

}