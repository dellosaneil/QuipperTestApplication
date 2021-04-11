package com.example.quippertrainingapplication.cryptocurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.crypto.CryptoCurrency
import com.example.quippertrainingapplication.repository.CryptoRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject


private const val TAG = "retrieveDataFromApi()"

class CompareViewModel(repository: CryptoRepository) : ViewModel() {

    private val bitcoinStatus : PublishSubject<Set<String>> = PublishSubject.create()
    private val bitcoinPrices = mutableSetOf<String>()

    private val ethereumStatus : PublishSubject<Set<String>> = PublishSubject.create()
    private val ethereumPrices = mutableSetOf<String>()


    fun retrieveBitcoinPrice() = bitcoinStatus
    fun retrieveEthereumPrice() = ethereumStatus

    init {
        val repositoryFunctions = arrayOf(repository.retrieveFromCryptoApiBitcoin(), repository.retrieveFromCryptoApiEthereum())
        val interval = arrayOf(2L,6L)
        val setPrices = listOf(bitcoinPrices, ethereumPrices)
        val publishStatus = listOf(bitcoinStatus, ethereumStatus)
        repeat(interval.size){ indexNumber ->
            Observable.interval(interval[indexNumber], java.util.concurrent.TimeUnit.SECONDS)
                .subscribeOn(io())
                .doOnSubscribe {
                    retrieveCryptoCurrency(repositoryFunctions[indexNumber], setPrices[indexNumber], publishStatus[indexNumber])
                }
                .doOnNext {
                    retrieveCryptoCurrency(repositoryFunctions[indexNumber], setPrices[indexNumber],publishStatus[indexNumber])
                }
                .doOnError { error ->
                    Log.i(TAG, "${error.message}: ")
                }
                .subscribe()
        }
    }


    private fun retrieveCryptoCurrency(
        single: Single<CryptoCurrency>,
        priceSets: MutableSet<String>,
        publishSubject: PublishSubject<Set<String>>
    ) {
        single
            .subscribeOn(io())
            .doOnSuccess {
                priceSets.add(it.data.priceUsd)
                publishSubject.onNext(priceSets)
            }
            .doOnError { Log.i(TAG, "retrieveDataFromApi: ${it.message}") }
            .subscribe()
    }

}