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
import com.example.quippertrainingapplication.databinding.FragmentHomeBinding
import com.example.quippertrainingapplication.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val TAG = "HomeFragment"

class HomeFragment : Fragment(), HomeAdapter.HomeAdapterListener, SearchView.OnQueryTextListener {


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

    private var toast: Toast? = null


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
        prepareSearchView()
    }

    private fun prepareSearchView() {
        val search = binding.homeFragmentToolbar.menu?.findItem(R.id.homeMenu_searchView)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
    }

    private fun createRecyclerView() {
        binding.homeFragmentRv.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addOnScrollListener(myScrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun articleClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let { nonNullQuery ->
            homeViewModel.newsArticles(nonNullQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.results
                }
                .doOnSubscribe {
                    binding.homeFragmentProgressBar.visibility = View.VISIBLE
                }
                .doOnNext {
                    homeAdapter.updateResultList(it)
                }
                .doOnComplete {
                    binding.homeFragmentProgressBar.visibility = View.GONE
                    binding.homeFragmentRv.smoothScrollToPosition(0)
                }
                .doOnError {
                    Log.i(TAG, "onQueryTextChange: ${it.message}")
                }
                ?.subscribe()
        }
        return true
    }

    private fun handleToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
        toast?.show()
    }

    private val myScrollListener : RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
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