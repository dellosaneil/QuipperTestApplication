package com.example.quippertrainingapplication.cryptocurrency

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        handleBitcoinData()
        handleEthereumData()
        return binding.root
    }

    private fun handleEthereumData() {
        compareViewModel.retrieveBitcoinPrice()
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnNext{
                plotPointsEthereum(it)
            }
            .doOnError { Log.i(TAG, "onCreateView: ${it.message}")
            }.subscribe()
    }

    private fun handleBitcoinData(){
        compareViewModel.retrieveBitcoinPrice()
            .subscribeOn(AndroidSchedulers.mainThread())
            .doOnNext{
                plotPointsBitcoin(it)
            }
            .doOnError { Log.i(TAG, "onCreateView: ${it.message}")
            }
            .subscribe()
    }

    private fun plotPointsEthereum(cryptoPriceStatus: Set<String>?) {
        cryptoPriceStatus?.let{
            val entries = mutableListOf<Entry>()
            it.forEachIndexed { index, ethereum ->
                entries.add(Entry(index.toFloat(), ethereum.toFloat()))
            }
            val dataSet = LineDataSet(entries, "Ethereum Price").apply{
                setDrawValues(false)
                setDrawCircles(false)
            }
            val lineData = LineData(dataSet)
            binding.compareFragmentEthereum.data = lineData
            binding.compareFragmentEthereum.invalidate()
        }
    }



    private fun plotPointsBitcoin(cryptoPriceStatus: Set<String>?) {
        cryptoPriceStatus?.let{
            val entries = mutableListOf<Entry>()
            it.forEachIndexed { index, bitcoin ->
                entries.add(Entry(index.toFloat(), bitcoin.toFloat()))
            }
            val dataSet = LineDataSet(entries, "Bitcoin Price").apply{
                setDrawValues(false)
                setDrawCircles(false)
            }
            val lineData = LineData(dataSet)
            binding.compareFragmentBitcoin.data = lineData
            binding.compareFragmentBitcoin.invalidate()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}