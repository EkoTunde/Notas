package com.ekosoftware.notas.presentation.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.databinding.FragmentBottomSheetNavDrawerBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomNavigationDrawerFragment(private val bottomNavigationDrawerListener: BottomNavigationDrawerListener) :
    BottomSheetDialogFragment() {

    interface BottomNavigationDrawerListener {
        fun onNewLabel()
    }

    private var _binding: FragmentBottomSheetNavDrawerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var addBtnIndex = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBottomSheetNavDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        setItemSelectedListener()
    }

    private fun initMenu() = binding.navigationView.menu.apply {
        mainViewModel.labels.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Success -> {
                    addAllNotesItem()
                    val labels = result.data
                    val selectedLabel = mainViewModel.currentSelectedLabel.value
                    addLabels(labels, selectedLabel)
                    //addAddingBtn(labels.size)
                    setGroupCheckable(R.id.group_one, true, true)
                    setGroupVisible(R.id.group_one, true)
                }
                is Resource.Failure -> {
                    this@BottomNavigationDrawerFragment.dismiss()
                }
            }

        })
    }

    private fun setItemSelectedListener() = binding.navigationView.setNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            0 -> mainViewModel.setLabel(null)
            R.id.add_label -> bottomNavigationDrawerListener.onNewLabel()
            else -> mainViewModel.setLabel(menuItem.title.toString())
        }
        this@BottomNavigationDrawerFragment.dismiss()
        true
    }

    private fun Menu.addAllNotesItem() {
        add(R.id.group_one, 0, 0, requireContext().getString(R.string.all_notes_title))
        getItem(0).isChecked = true
    }

    private fun Menu.addLabels(labels: List<Label>, selectedLabel: String?) {
        for (i in labels.indices) {
            add(R.id.group_one, i + 1, (i + 1) * 100, labels[i].name)
            if (labels[i].name == selectedLabel) {
                getItem(i + 1).isChecked = true
            }
        }
    }

    private fun Menu.addAddingBtn(labelsCount: Int) {
        add(1, labelsCount + 1, 1, requireContext().getString(R.string.create_new_label))
        getItem(labelsCount + 1).setIcon(R.drawable.ic_add_24)
        addBtnIndex = labelsCount + 1
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}