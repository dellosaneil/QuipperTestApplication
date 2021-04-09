package com.example.quippertrainingapplication.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quippertrainingapplication.api_data.Response
import com.example.quippertrainingapplication.databinding.FragmentHomeBinding
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), HomeAdapter.HomeAdapterListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }
    private val repository: Repository by lazy { Repository() }
    private val homeViewModelFactory: HomeFragmentViewModelFactory by lazy {
        HomeFragmentViewModelFactory(
            repository
        )
    }
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this@HomeFragment,
            homeViewModelFactory
        ).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        homeViewModel.newsArticles()
//            ?.subscribeOn(Schedulers.io())
//            ?.subscribe(newsArticlesObserver)
    }

    private val newsArticlesObserver = object : Observer<Response> {
        override fun onSubscribe(d: Disposable) {
            Log.i(TAG, "onSubscribe: ")
        }

        override fun onNext(t: Response) {
            Log.i(TAG, "onNext: ${t.results}")
        }

        override fun onError(e: Throwable) {
            Log.i(TAG, "onError: ${e.message}")
        }

        override fun onComplete() {
            Log.i(TAG, "onComplete: ")
        }


    }


    private fun createRecyclerView() {
        binding.homeFragmentRv.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun articleClicked(position: Int) {
        TODO("Not yet implemented")
    }
}