package com.example.quippertrainingapplication.cryptocurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.api_data.crypto.CryptoCurrency
import com.example.quippertrainingapplication.repository.CryptoRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject


private const val TAG = "CompareViewModel"

class CompareViewModel(private val repository: CryptoRepository) : ViewModel() {

    private val bitcoinStatus: PublishSubject<Double> = PublishSubject.create()

    private val ethereumStatus: PublishSubject<Double> = PublishSubject.create()

    private val comparedStatus: PublishSubject<Pair<Double, Double>> =
        PublishSubject.create()

    private val compositeDisposable = CompositeDisposable()

    private val prevValueArray = arrayOf(1.0,1.0)
    private var firstCalculation = true

    private val distinctValueCheck = arrayOf(0.0, 0.0)






    fun retrieveBitcoinPrice() = bitcoinStatus
    fun retrieveEthereumPrice() = ethereumStatus
    fun retrieveComparisonPercentage() = comparedStatus


    init {
        initializeCrypto()
        comparisonBetweenCrypto()
    }

    private fun initializeCrypto(){
        val repositoryFunctions = arrayOf(
            repository.retrieveFromCryptoApiBitcoin(),
            repository.retrieveFromCryptoApiEthereum()
        )
        val interval = arrayOf(2L, 6L)
        val publishStatus = listOf(bitcoinStatus, ethereumStatus)
        repeat(interval.size) { indexNumber ->
            Observable.interval(interval[indexNumber], java.util.concurrent.TimeUnit.SECONDS)
                .subscribeOn(io())

                .doOnSubscribe {
                    compositeDisposable.add(it)
                    retrieveCryptoCurrency(
                        repositoryFunctions[indexNumber],
                        publishStatus[indexNumber],
                        distinctValueCheck[indexNumber],
                        indexNumber
                    )
                }
                .doOnNext {
                    retrieveCryptoCurrency(
                        repositoryFunctions[indexNumber],
                        publishStatus[indexNumber],
                        distinctValueCheck[indexNumber],
                        indexNumber
                    )
                }
                .doOnError { error ->
                    Log.i(TAG, "${error.message}: ")
                }
                .subscribe()
        }
    }


    private fun comparisonBetweenCrypto() {
        Observable.zip(bitcoinStatus, ethereumStatus) { bitcoin, ethereum ->
            if(!firstCalculation){
                val first: Double = calculatePercentageChange(bitcoin, prevValueArray[0])
                val second: Double = calculatePercentageChange(ethereum, prevValueArray[1])
                prevValueArray[0] = bitcoin
                prevValueArray[1] = ethereum
                first to second
            }else{
                firstCalculation = false
                prevValueArray[0] = bitcoin
                prevValueArray[1] = ethereum
                0.0 to 0.0
            }
        }
            .subscribeOn(io())
            .doOnSubscribe {
                compositeDisposable.add(it)
            }
            .doOnNext {
                if(it.first != 0.0 && it.second != 0.0) {
                    Log.i(TAG, "prevBitcoin: ${it.first}\t\t\t\tprevEth: ${it.second} ")
                    comparedStatus.onNext(it)
                }
            }
            .doOnError { Log.i(TAG, "comparisonBetweenCrypto: ${it.message}") }
            .subscribe()
    }

    private fun calculatePercentageChange(currentPrice: Double, prevPrice: Double): Double {
        return (currentPrice - prevPrice).div(prevPrice)
    }

    private fun retrieveCryptoCurrency(
        single: Single<CryptoCurrency>,
        publishSubject: PublishSubject<Double>,
        previousValue: Double,
        indexNumber: Int
    ) {
        single
            .subscribeOn(io())
            .doOnError { Log.i(TAG, "retrieveCryptoCurrency: ${it.message}") }
            .map{
                it.data.priceUsd.toDouble()
            }
            .doOnSuccess {
                if(previousValue != it){
                    publishSubject.onNext(it)
                    distinctValueCheck[indexNumber] = it
                }
            }
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }



}