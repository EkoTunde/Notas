package com.ekosoftware.notas.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.ekosoftware.notas.R
import com.ekosoftware.notas.databinding.FragmentBottomSheetNavDrawerBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomNavigationDrawerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetNavDrawerBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBottomSheetNavDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val LISTS_NAMES_ARGS = "lists names args"
        const val SELECTED_LIST_ARG = "selected list arg"
    }

    private lateinit var listsNames: List<String>
    private var selectedListIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            listsNames = it.getStringArrayList(LISTS_NAMES_ARGS)!!
        }
    }

    private val TAG = "BottomNavigationDrawerF"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = binding.navigationView.menu

        menu.add(R.id.group_one, 0, 0, requireContext().getString(R.string.all_notes_title))
        menu.getItem(0).isChecked = true

        /*menu.setGroupCheckable(R.id.group_one, true, true)
        menu.setGroupVisible(R.id.group_one, true)*/

        val selectListTitle = mainViewModel.selectedLabel()

        for (i in listsNames.indices) {
            menu.add(R.id.group_one, i+1, (i+1)*100, listsNames[i])
            if (listsNames[i] == selectListTitle) menu.getItem(i+1).isChecked = true
        }

        menu.add(1, listsNames.size+1, 1, requireContext().getString(R.string.create_new_label))
        menu.getItem(listsNames.size+1).setIcon(R.drawable.ic_add_24)

        menu.setGroupCheckable(R.id.group_one, true, true)
        menu.setGroupVisible(R.id.group_one, true)

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            /*if (menuItem.itemId == R.id.all_notes) {
                // muestra todas las notas...
            } else {
                // muestra las notas de la lista especificada
            }*/
            //Toast.makeText(requireContext(), menuItem.title, Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "${menuItem.itemId}", Toast.LENGTH_SHORT).show()
            //mainViewModel.saveSelectedListId(menuItem.itemId)
            mainViewModel.saveSelectedLabel(menuItem.title.toString())
            this@BottomNavigationDrawerFragment.dismiss()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}