package com.ekosoftware.notas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ekosoftware.notas.databinding.ActivityMainBinding
import com.ekosoftware.notas.presentation.Event
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavController()
        subscribeEventObserver()
    }

    private fun setUpNavController() {
        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id != R.id.addEditRecipeFragment) {
                mainViewModel.cancelImageUpload()
            }

            if (destination.id == R.id.homeFragment) {
                //binding.btnAddRecipe.visibility = View.VISIBLE
                binding.btnAddRecipe.show()
            } else {
                binding.btnAddRecipe.hide()
                //binding.btnAddRecipe.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun subscribeEventObserver() = mainViewModel.eventReceiver.observe(this, { event ->
        val eventMsg = when (event) {
            Event.INSERT -> getString(R.string.inserted)
            Event.UPDATE -> getString(R.string.updated)
            Event.DELETE -> getString(R.string.deleted)
            else -> ""
        }
        Snackbar.make(binding.root, getString(R.string.event_msg, eventMsg), Snackbar.LENGTH_SHORT).show()
    })
}