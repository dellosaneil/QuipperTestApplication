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

    private fun prepareSearchView(){
        val search = binding.homeFragmentToolbar.menu?.findItem(R.id.homeMenu_searchView)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = false
        searchView?.setOnQueryTextListener(this)
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        binding.homeFragmentProgressBar.visibility = View.VISIBLE
        query?.let{ q ->
            homeViewModel.newsArticles(q)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    homeAdapter.updateResultList(it.results)
                    Toast.makeText(requireContext(), it.total.toString(), Toast.LENGTH_LONG).show()
                }
                .doOnComplete{binding.homeFragmentProgressBar.visibility = View.GONE}
                .doOnError{ Log.i(TAG, "onStart: ${it.message}")}
                ?.subscribe()
        }

        return true
    }
}