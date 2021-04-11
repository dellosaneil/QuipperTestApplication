package com.example.quippertrainingapplication.cryptocurrency

import android.graphics.Color
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject


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
        val publishSubjectArray = arrayOf(
            compareViewModel.retrieveBitcoinPrice(),
            compareViewModel.retrieveEthereumPrice()
        )
        repeat(publishSubjectArray.size){
            handleGraphing(publishSubjectArray[it], it)
        }
        handleComparisonPercentage()
        return binding.root
    }

    private fun handleGraphing(publishSubject: PublishSubject<Set<String>>, indexNumber: Int) {
        publishSubject
            .compose(applySchedulers())
            .doOnNext {
                plotCryptoCurrencyPoints(it, indexNumber)
            }
            .doOnError {
                Log.i(TAG, "handleGraphing: ${it.message}")
            }
            .subscribe()
    }

    private fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> {
            it.subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }


    private fun plotCryptoCurrencyPoints(cryptoPriceStatus: Set<String>?, indexNumber: Int) {
        val lineChartView = arrayOf(binding.compareFragmentBitcoin, binding.compareFragmentEthereum)
        val lineChartColor = arrayOf(Color.BLUE, Color.RED)
        val lineChartLabel = arrayOf("Bitcoin Price", "Ethereum Price")
        cryptoPriceStatus?.let {
            val entries = mutableListOf<Entry>()
            it.forEachIndexed { index, coinsPrice ->
                entries.add(Entry(index.toFloat(), coinsPrice.toFloat()))
            }
            val dataSet = LineDataSet(entries, lineChartLabel[indexNumber]).apply {
                setDrawValues(false)
                setDrawCircles(false)
                color = lineChartColor[indexNumber]
            }
            val lineData = LineData(dataSet)
            lineChartView[indexNumber].apply {
                description.isEnabled = false
                data = lineData
                axisRight.isEnabled = false
                xAxis.granularity = 1.0f
                xAxis.isGranularityEnabled = true
                invalidate()
            }
        }
    }

    private fun handleComparisonPercentage() {
        compareViewModel.retrieveComparisonPercentage()
            .doOnNext {
                plotPointsPercentageComparision(it)
            }
            .doOnError {
                Log.i(TAG, "handleComparisonPercentage: ${it.message}")
            }
            .subscribe()
    }

    private fun plotPointsPercentageComparision(pair: Pair<List<Double>, List<Double>>) {
        val bitcoinPercentage = mutableListOf<Entry>()
        val ethereumPercentage = mutableListOf<Entry>()
        pair.first.let {
            it.forEachIndexed { index, bitcoin ->
                bitcoinPercentage.add(Entry(index.toFloat(), bitcoin.toFloat()))
            }
        }

        pair.second.let {
            it.forEachIndexed { index, ethereum ->
                ethereumPercentage.add(Entry(index.toFloat(), ethereum.toFloat()))
            }
        }

        val bitcoinDataSet = LineDataSet(bitcoinPercentage, "Bitcoin % Change").apply {
            setDrawValues(false)
            color = Color.BLUE
        }
        val ethereumDataSet = LineDataSet(ethereumPercentage, "Ethreum % Change").apply {
            setDrawValues(false)
            color = Color.RED
        }

        val lineChartData = LineData().apply{
            addDataSet(bitcoinDataSet)
            addDataSet(ethereumDataSet)
        }

        binding.compareFragmentCompare.apply {
            data = lineChartData
            xAxis.granularity = 1.0f
            xAxis.isGranularityEnabled = true
            invalidate()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}