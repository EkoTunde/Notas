package com.ekosoftware.notas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.notas.databinding.ActivityMainBinding
import com.ekosoftware.notas.presentation.Event
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.presentation.home.HomeFragmentDirections
import com.ekosoftware.notas.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeEventObserver()
    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
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