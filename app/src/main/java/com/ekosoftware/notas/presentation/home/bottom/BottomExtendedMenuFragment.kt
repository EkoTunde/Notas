package com.ekosoftware.notas.presentation.home.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ekosoftware.notas.R
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.databinding.FragmentBottomExtendedMenuBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomExtendedMenuFragment(private val bottomExtendedMenuListener: BottomExtendedMenuListener?) :
    BottomSheetDialogFragment() {

    interface BottomExtendedMenuListener {
        fun onRenameLabel(label: Label)
    }

    private var _binding: FragmentBottomExtendedMenuBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBottomExtendedMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
    }

    private fun initMenu() = binding.navigationView.setNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.rename_label -> {
                bottomExtendedMenuListener?.onRenameLabel(mainViewModel.selectedLabel())
            }
            R.id.delete_label -> {
                mainViewModel.deleteLabel(mainViewModel.selectedLabel())
                //mainViewModel.selectLabel(null)
            }
        }
        this@BottomExtendedMenuFragment.dismiss()
        true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}