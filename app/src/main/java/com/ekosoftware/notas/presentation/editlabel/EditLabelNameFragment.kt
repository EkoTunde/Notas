package com.ekosoftware.notas.presentation.editlabel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ekosoftware.notas.R
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.databinding.FragmentEditLabelNameBinding
import com.ekosoftware.notas.presentation.MainViewModel

class EditLabelNameFragment : Fragment() {
    private var _binding: FragmentEditLabelNameBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val args: EditLabelNameFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentEditLabelNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtName.setText(args.label.name)
        setUpNavigation()
    }

    private fun setUpNavigation() = binding.toolbar.apply {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupWithNavController(findNavController(), appBarConfiguration)
        this.setOnMenuItemClickListener {
            if (it.itemId == R.id.save) {
                mainViewModel.updateLabel(updatedLabel())
                mainViewModel.selectLabel(updatedLabel())
                findNavController().navigateUp()
            }
            true
        }
    }

    private fun updatedLabel() = Label(
        args.label.id,
        binding.txtName.text.toString()
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}