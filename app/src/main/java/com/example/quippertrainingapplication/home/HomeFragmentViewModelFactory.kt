package com.example.quippertrainingapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quippertrainingapplication.repository.Repository

@Suppress("UNCHECKED_CAST")
class HomeFragmentViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}