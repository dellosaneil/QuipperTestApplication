package com.example.quippertrainingapplication.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quippertrainingapplication.R
import com.example.quippertrainingapplication.api_data.guardian.Result
import com.example.quippertrainingapplication.databinding.ItemListHomeBinding
import java.util.*

class HomeAdapter(private val listener: HomeAdapterListener) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private var results = listOf<Result>()

    fun updateResultList(newResults: List<Result>) {
        val oldResults = results
        val diffResult = DiffUtil.calculateDiff(
            HomeDiffUtil(oldResults, newResults)
        )
        results = newResults
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ItemListHomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount() = results.size


    private class HomeDiffUtil(
        private val oldItemList: List<Result>,
        private val newItemList: List<Result>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldItemList.size
        override fun getNewListSize() = newItemList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItemList[oldItemPosition].webUrl == newItemList[newItemPosition].webUrl
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItemList[oldItemPosition] == newItemList[newItemPosition]
    }

    inner class HomeViewHolder(private val binding: ItemListHomeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.homeRVContainer.setOnClickListener(this)
        }

        fun bind(result: Result) {
            binding.homeRVArticleTitle.text = result.webTitle
            binding.homeRVPillarName.text = result.type.capitalize(Locale.ROOT)
            binding.homeRVPublishedDate.text = result.webPublicationDate.substring(0, 10)
            imagePillar(pillarDrawable(result.type))

        }

        private fun pillarDrawable(typeName: String): Int {
            return when (typeName.toLowerCase(Locale.ROOT)){
                "article" -> R.drawable.ic_article_85
                "liveblog" -> R.drawable.ic_liveblog_85
                "interactive" -> R.drawable.ic_interactive_85
                "picture" -> R.drawable.ic_picture_85
                "audio" -> R.drawable.ic_audio_85
                "gallery" -> R.drawable.ic_gallery_85
                else -> R.drawable.ic_default_85
            }
        }

        private fun imagePillar(type: Int) {
            Glide.with(binding.root)
                .load(type)
                .into(binding.homeRVImage)
        }

        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.homeRV_container -> listener.articleClicked(adapterPosition)
            }
        }
    }

    interface HomeAdapterListener {
        fun articleClicked(position: Int)
    }

}










