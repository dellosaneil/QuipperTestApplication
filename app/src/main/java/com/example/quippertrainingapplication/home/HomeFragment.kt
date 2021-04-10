package com.example.quippertrainingapplication.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quippertrainingapplication.R
import com.example.quippertrainingapplication.RecyclerViewDecorator
import com.example.quippertrainingapplication.api_data.Result
import com.example.quippertrainingapplication.databinding.FragmentHomeBinding
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers.io

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), HomeAdapter.HomeAdapterListener, SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }
    private val repository: Repository by lazy { Repository() }
    private val homeViewModelFactory: HomeFragmentViewModelFactory by lazy {
        HomeFragmentViewModelFactory(repository)
    }
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(
            this@HomeFragment,
            homeViewModelFactory
        ).get(HomeViewModel::class.java)
    }

    private var toast: Toast? = null
    private var queryText: String = ""
    private var pageNumber = 1
    private var articleList = listOf<Result>()
    private var maxPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUpRecyclerView()
        prepareSearchView()
        homeViewModel.getNewsArticles()
            .doOnNext {
                homeAdapter.updateResultList(it.results)
                binding.homeFragmentProgressBar.visibility = View.GONE
                articleList = it.results
                maxPage = it.pages
                binding.homeFragmentButton.show()
                binding.homeFragmentRv.smoothScrollToPosition(0)
            }
            .doOnError{
                Log.i(TAG, "onStart: ${it.message}")
            }
            .subscribe()
        homeViewModel.getLoadingState()
            .doOnNext {
                if(it){
                    binding.homeFragmentProgressBar.visibility = View.VISIBLE
                }else{
                    binding.homeFragmentProgressBar.visibility = View.GONE
                }
            }.subscribe()
    }

    override fun onResume() {
        super.onResume()
        binding.homeFragmentButton.setOnClickListener {
            if(pageNumber != maxPage) {
                homeViewModel.newsArticles(queryText, ++pageNumber)
//                binding.homeFragmentProgressBar.visibility = View.VISIBLE
            }else{
                handleToast(getString(R.string.homeFragment_noNextPage))
            }
        }
    }

    private fun prepareSearchView() {
        val search = binding.homeFragmentToolbar.menu?.findItem(R.id.homeMenu_searchView)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
    }

    private fun setUpRecyclerView() {
        binding.homeFragmentRv.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(myScrollListener)
            addItemDecoration(RecyclerViewDecorator(7,10))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun articleClicked(position: Int) {
        Log.i(TAG, "articleClicked: ${articleList[position]}")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let { nonNullQuery ->
            pageNumber = 1
            queryText = nonNullQuery
//            binding.homeFragmentProgressBar.visibility = View.VISIBLE
            homeViewModel.newsArticles(nonNullQuery)
        }
        return true
    }

    private fun handleToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        toast?.show()
    }

    private val myScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.homeFragmentButton.visibility == View.VISIBLE) {
                    binding.homeFragmentButton.hide()
                } else if (dy < 0 && binding.homeFragmentButton.visibility == View.GONE) {
                    binding.homeFragmentButton.show()
                }
            }
        }
}