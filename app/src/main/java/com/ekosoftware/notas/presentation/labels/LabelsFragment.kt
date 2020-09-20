package com.ekosoftware.notas.presentation.labels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.notas.R
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.databinding.FragmentLabelsBinding

class LabelsFragment : Fragment() {
    private var _binding: FragmentLabelsBinding? = null
    private val binding get() = _binding!!

    private lateinit var labelsRecyclerAdapter: LabelsRecyclerAdapter
    private lateinit var addLabelRecyclerAdapter: AddLabelRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLabelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initRecyclerView() = binding.labels.apply {
        layoutManager = LinearLayoutManager(requireContext())
        labelsRecyclerAdapter = LabelsRecyclerAdapter(requireContext(), object : LabelsRecyclerAdapter.Interaction {
            override fun onUpdateItem(label: Label) {
                TODO("Not yet implemented")
            }

            override fun onDeleteItem(label: Label) {
                TODO("Not yet implemented")
            }
        })
        addLabelRecyclerAdapter = AddLabelRecyclerAdapter(object : AddLabelRecyclerAdapter.Interaction {
            override fun onInsertItem(labelName: String) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}