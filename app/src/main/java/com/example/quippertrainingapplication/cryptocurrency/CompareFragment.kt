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
    private val bitcoinPrices = mutableListOf<Double>()
    private val ethereumPrices = mutableListOf<Double>()
    private val comparisonPercentages = Pair<MutableList<Double>, MutableList<Double>>(mutableListOf(), mutableListOf())


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
        val cryptoDataArray = arrayOf(bitcoinPrices, ethereumPrices)
        repeat(publishSubjectArray.size){
            handleGraphing(publishSubjectArray[it],cryptoDataArray, it)
        }
        handleComparisonPercentage()
        return binding.root
    }

    private fun handleGraphing(
        newestEmittedItem: PublishSubject<Double>,
        cryptoDataArray: Array<MutableList<Double>>,
        indexNumber: Int
    ) {
        newestEmittedItem
            .compose(applySchedulers())
            .doOnNext {
                cryptoDataArray[indexNumber].add(it.toDouble())
                plotCryptoCurrencyPoints(cryptoDataArray[indexNumber], indexNumber)
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


    private fun plotCryptoCurrencyPoints(cryptoPriceStatus: MutableList<Double>, indexNumber: Int) {
        val lineChartView = arrayOf(binding.compareFragmentBitcoin, binding.compareFragmentEthereum)
        val lineChartColor = arrayOf(Color.BLUE, Color.RED)
        val lineChartLabel = arrayOf("Bitcoin Price", "Ethereum Price")
        cryptoPriceStatus.let {
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
            .compose(applySchedulers())
            .doOnNext {
                comparisonPercentages.first.add(it.first)
                comparisonPercentages.second.add(it.second)
                plotPointsPercentageComparision()
            }
            .doOnError {
                Log.i(TAG, "handleComparisonPercentage: ${it.message}")
            }
            .subscribe()
    }

    private fun plotPointsPercentageComparision() {
        val bitcoinPercentage = mutableListOf<Entry>()
        val ethereumPercentage = mutableListOf<Entry>()
        comparisonPercentages.first.let {
            it.forEachIndexed { index, bitcoin ->
                bitcoinPercentage.add(Entry(index.toFloat(), bitcoin.toFloat()))
            }
        }

        comparisonPercentages.second.let {
            it.forEachIndexed { index, ethereum ->
                ethereumPercentage.add(Entry(index.toFloat(), ethereum.toFloat()))
            }
        }

        val bitcoinDataSet = LineDataSet(bitcoinPercentage, "Bitcoin % Change").apply {
            setDrawValues(false)
            setCircleColor(Color.BLUE)
            circleHoleColor = Color.BLUE
            color = Color.BLUE
        }
        val ethereumDataSet = LineDataSet(ethereumPercentage, "Ethreum % Change").apply {
            setDrawValues(false)
            setCircleColor(Color.RED)
            circleHoleColor = Color.RED
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