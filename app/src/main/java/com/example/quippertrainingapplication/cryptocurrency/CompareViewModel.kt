package com.example.quippertrainingapplication.cryptocurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.quippertrainingapplication.repository.CryptoRepository
import io.reactivex.schedulers.Schedulers.io

private const val TAG = "CompareViewModel"

class CompareViewModel(private val repository: CryptoRepository) : ViewModel() {

    init {
        repository.retrieveFromCryptoApi()
            .subscribeOn(io())
            .doOnSuccess { Log.i(TAG, "$it: ") }
            .subscribe()
    }

}