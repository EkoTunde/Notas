package com.ekosoftware.notas

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ekosoftware.notas.databinding.ActivityMainBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //subscribeEventObserver()
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { _, _, _ ->
            hideKeyboard()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        hideKeyboard()
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    /*private fun subscribeEventObserver() = mainViewModel.eventReceiver.observe(this, { event ->
        val eventMsg = when (event) {
            Event.INSERT -> getString(R.string.inserted)
            Event.UPDATE -> getString(R.string.updated)
            Event.DELETE -> getString(R.string.deleted)
            else -> ""
        }
        val snackbar = Snackbar.make(binding.root, getString(R.string.event_msg, eventMsg), Snackbar.LENGTH_SHORT)

        val view: View = snackbar.view
        val params: CoordinatorLayout.LayoutParams = view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.FILL_HORIZONTAL or Gravity.BOTTOM
        view.layoutParams = params
        snackbar.show()
    })*/
}