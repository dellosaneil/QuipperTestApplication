package com.example.quippertrainingapplication.cryptocurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quippertrainingapplication.repository.CryptoRepository


@Suppress("UNCHECKED_CAST")
class CompareFragmentViewModelFactory(private val repository: CryptoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CompareViewModel(repository) as T
    }
}