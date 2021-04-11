package com.example.quippertrainingapplication.cryptocurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.crypto.CryptoCurrency
import com.example.quippertrainingapplication.repository.CryptoRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject


private const val TAG = "CompareViewModel"

class CompareViewModel(repository: CryptoRepository) : ViewModel() {

    private val bitcoinStatus: PublishSubject<Set<String>> = PublishSubject.create()
    private val bitcoinPrices = mutableSetOf<String>()

    private val ethereumStatus: PublishSubject<Set<String>> = PublishSubject.create()
    private val ethereumPrices = mutableSetOf<String>()

    private val comparedStatus: PublishSubject<Pair<List<Double>, List<Double>>> =
        PublishSubject.create()

    private val ethereumPercentages = mutableListOf<Double>()
    private val bitcoinPercentages = mutableListOf<Double>()

    private val disposableArray = arrayOf<Disposable?>(null, null, null)


    fun retrieveBitcoinPrice() = bitcoinStatus
    fun retrieveEthereumPrice() = ethereumStatus
    fun retrieveComparisonPercentage() = comparedStatus

    init {
        val repositoryFunctions = arrayOf(
            repository.retrieveFromCryptoApiBitcoin(),
            repository.retrieveFromCryptoApiEthereum()
        )
        val interval = arrayOf(2L, 6L)
        val setPrices = listOf(bitcoinPrices, ethereumPrices)
        val publishStatus = listOf(bitcoinStatus, ethereumStatus)
        repeat(interval.size) { indexNumber ->
            Observable.interval(interval[indexNumber], java.util.concurrent.TimeUnit.SECONDS)
                .subscribeOn(io())
                .doOnSubscribe {
                    disposableArray[indexNumber] = it
                    retrieveCryptoCurrency(
                        repositoryFunctions[indexNumber],
                        setPrices[indexNumber],
                        publishStatus[indexNumber]
                    )
                }
                .doOnNext {
                    retrieveCryptoCurrency(
                        repositoryFunctions[indexNumber],
                        setPrices[indexNumber],
                        publishStatus[indexNumber]
                    )
                }
                .doOnError { error ->
                    Log.i(TAG, "${error.message}: ")
                }
                .subscribe()
        }
        comparisonBetweenCrypto()
    }

    private fun comparisonBetweenCrypto() {
        Observable.zip(bitcoinStatus, ethereumStatus) { bitcoin, ethereum ->
            val first: Double
            val second: Double
            if (bitcoin.size >= 2) {
                first = calculatePercentageChange(bitcoin.last().toDouble(), bitcoin.elementAt(bitcoin.size - 2).toDouble())
                second = calculatePercentageChange(ethereum.last().toDouble(), ethereum.elementAt(ethereum.size - 2).toDouble())
                ethereumPercentages.add(second)
                bitcoinPercentages.add(first)
                bitcoinPercentages to ethereumPercentages
            } else {
                listOf<Double>() to listOf<Double>()
            }
        }.doOnSubscribe {
            disposableArray[2] = it
        }
            .doOnNext { comparedStatus.onNext(it) }
            .doOnError { Log.i(TAG, "comparisonBetweenCrypto: ${it.message}") }
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        disposableArray.forEach{
            it?.dispose()
        }

    }


    private fun calculatePercentageChange(currentPrice: Double, prevPrice: Double): Double {
        return if (currentPrice - prevPrice > 0) {
            (currentPrice.div(prevPrice))
        } else {
            (currentPrice.div(prevPrice)).times(-1.0)
        }
    }

    private fun retrieveCryptoCurrency(
        single: Single<CryptoCurrency>,
        priceSets: MutableSet<String>,
        publishSubject: PublishSubject<Set<String>>
    ) {
        single
            .subscribeOn(io())
            .doOnError { Log.i(TAG, "retrieveCryptoCurrency: ${it.message}") }
            .doOnSuccess {
                if (priceSets.add(it.data.priceUsd)) {
                    publishSubject.onNext(priceSets)
                }
            }
            .subscribe()
    }

}