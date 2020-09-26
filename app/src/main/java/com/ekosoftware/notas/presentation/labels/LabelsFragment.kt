package com.ekosoftware.notas.presentation.labels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.getLabelByName
import com.ekosoftware.notas.databinding.FragmentLabelsBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.util.hideKeyboard
import com.google.android.material.snackbar.Snackbar

class LabelsFragment : Fragment() {
    private var _binding: FragmentLabelsBinding? = null
    private val binding get() = _binding!!

    private lateinit var labelsRecyclerAdapter: LabelsRecyclerAdapter
    private lateinit var addLabelRecyclerAdapter: AddLabelRecyclerAdapter
    private lateinit var concatAdapter: ConcatAdapter

    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var labels: MutableList<Label>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLabelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        initRecyclerView()
        fetchData()
    }

    private fun fetchData() = viewModel.labels.observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Success -> {
                labels = result.data.toMutableList()
                labelsRecyclerAdapter.submitList(labels)
                addLabelRecyclerAdapter.submitExistingNamesList(labels.map { it.name })
                labelsRecyclerAdapter.notifyDataSetChanged()
            }
            is Resource.Failure -> {
                Snackbar.make(binding.root, R.string.error_fetching_labels, Snackbar.LENGTH_LONG).show()
            }
        }
    })

    private fun setUpNavigation() = binding.toolbar.apply {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupWithNavController(findNavController(), appBarConfiguration)
    }

    private fun initRecyclerView() = binding.labels.apply {
        layoutManager = LinearLayoutManager(requireContext())
        addLabelRecyclerAdapter = AddLabelRecyclerAdapter(requireContext(), addLabelAdapterInteraction)
        labelsRecyclerAdapter = LabelsRecyclerAdapter(requireContext(), labelAdapterInteraction)
        concatAdapter = ConcatAdapter(addLabelRecyclerAdapter, labelsRecyclerAdapter)
        adapter = concatAdapter
    }

    private val labelAdapterInteraction = object : LabelsRecyclerAdapter.Interaction {
        override fun onUpdateItem(label: Label, position: Int) {
            viewModel.updateLabel(label)
        }

        override fun onDeleteItem(label: Label) {
            viewModel.deleteLabel(label)
        }

        override fun focusLost() = this@LabelsFragment.hideKeyboard()
    }

    private val addLabelAdapterInteraction = object : AddLabelRecyclerAdapter.Interaction {
        override fun onAddLabel(name: String) {
            this@LabelsFragment.hideKeyboard()
            if (!name.alreadyExists()) {
                labels.getLabelByName(name)
                viewModel.addLabel(Label(name = name))
                labelsRecyclerAdapter.notifyDataSetChanged()
            }
        }

        override fun focusLost() = this@LabelsFragment.hideKeyboard()
    }

    private fun String.alreadyExists(): Boolean {
        for (label in labels) {
            if (label.name == this) return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}