package com.example.quippertrainingapplication.cryptocurrency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quippertrainingapplication.api_data.crypto.Bitcoin
import com.example.quippertrainingapplication.databinding.FragmentCompareBinding
import com.example.quippertrainingapplication.repository.CryptoRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.reactivex.android.schedulers.AndroidSchedulers


private const val TAG = "CompareFragment"
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
        compareViewModel.retrieveCryptoStatus()
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnNext{
                plotPoints(it)
            }.subscribe()
        return binding.root
    }

    private fun plotPoints(bitcoinList: List<Bitcoin>?) {
        bitcoinList?.let{
            val entries = mutableListOf<Entry>()
            it.forEachIndexed { index, bitcoin ->
                entries.add(Entry(index.toFloat(), bitcoin.data.priceUsd.toFloat()))
            }
            val dataSet = LineDataSet(entries, "Bitcoin Price")
            val lineData = LineData(dataSet)
            binding.compareFragmentCryptoStatus.data = lineData
            binding.compareFragmentCryptoStatus.invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}