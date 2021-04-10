package com.example.quippertrainingapplication.cryptocurrency

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.quippertrainingapplication.R
import com.example.quippertrainingapplication.databinding.FragmentCryptoBinding
import com.example.quippertrainingapplication.databinding.FragmentHomeBinding


class CryptoFragment : Fragment() {
    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        binding.crpytoFragmentLiveGraph.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.compareFragment)
        }
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}