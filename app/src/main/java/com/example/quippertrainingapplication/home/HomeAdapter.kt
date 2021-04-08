package com.example.quippertrainingapplication.home

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.quippertrainingapplication.api_data.Result
import com.example.quippertrainingapplication.databinding.ItemListHomeBinding

class HomeAdapter(private val listener : HomeAdapterListener) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var results = listOf<Result>()

    fun updateResultList(newResults : List<Result>){
        val oldResults = results
        val diffResult = DiffUtil.calculateDiff(
            HomeDiffUtil(oldResults, newResults)
        )
        results = newResults
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = results.size


    private class HomeDiffUtil(private val oldItemList : List<Result>, private val newItemList : List<Result>) : DiffUtil.Callback(){
        override fun getOldListSize() = oldItemList.size
        override fun getNewListSize() = newItemList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItemList[oldItemPosition].webUrl == newItemList[newItemPosition].webUrl
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItemList[oldItemPosition] == newItemList[newItemPosition]
    }



    inner class ViewHolder(private val binding : ItemListHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        
    }

    interface HomeAdapterListener{
        fun articleClicked(position : Int)
    }

}










