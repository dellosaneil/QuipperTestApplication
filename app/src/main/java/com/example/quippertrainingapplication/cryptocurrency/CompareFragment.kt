package com.example.quippertrainingapplication.cryptocurrency

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.quippertrainingapplication.R
import com.example.quippertrainingapplication.databinding.FragmentCompareBinding
import com.example.quippertrainingapplication.databinding.FragmentCryptoBinding
import com.example.quippertrainingapplication.home.HomeFragmentViewModelFactory
import com.example.quippertrainingapplication.home.HomeViewModel
import com.example.quippertrainingapplication.repository.CryptoRepository
import com.example.quippertrainingapplication.repository.Repository


class CompareFragment : Fragment() {
    private var _binding: FragmentCompareBinding? = null
    private val binding get() = _binding!!
    private val repository: CryptoRepository by lazy { CryptoRepository() }
    private val compareViewModelFactory: CompareFragmentViewModelFactory by lazy {
        CompareFragmentViewModelFactory(repository)
    }

    private val compareViewModel: CompareViewModel by lazy {
        ViewModelProvider(
            this@CompareFragment,
            compareViewModelFactory
        ).get(CompareViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompareBinding.inflate(inflater, container, false)
        compareViewModel
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}