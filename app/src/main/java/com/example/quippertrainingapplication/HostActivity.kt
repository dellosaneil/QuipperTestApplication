package com.example.quippertrainingapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quippertrainingapplication.databinding.ActivityHostBinding

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding
    private val navController: NavController by lazy { findNavController(R.id.fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.compareFragment -> binding.bottomNavigationView.visibility = View.GONE
                else -> binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

    }

}