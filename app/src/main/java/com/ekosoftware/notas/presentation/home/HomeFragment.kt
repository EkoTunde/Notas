package com.ekosoftware.notas.presentation.home

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.databinding.FragmentHomeBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.util.hide
import com.ekosoftware.notas.util.show
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var notes: MutableList<Note>

    private lateinit var notesRecyclerViewAdapter: NotesRecyclerViewAdapter
    private lateinit var labelRecyclerAdapter: LabelRecyclerAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initBottomBar()
        fetchData()
    }

    private lateinit var bottomSheet: NavigationView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NavigationView>

    private fun initBottomBar() {
        bottomSheet = binding.navigationView
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        binding.btnAddNote.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                btn to "btn_add_note"
            )
            findNavController().navigate(R.id.addEditNoteFragment, null, null, extras)
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            val bottomNavDrawerFragment =
                BottomNavigationDrawerFragment(object : BottomNavigationDrawerFragment.BottomNavigationDrawerListener {
                    override fun onNewLabel() {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLabelsFragment())
                    }
                })
            bottomNavDrawerFragment.show(requireActivity().supportFragmentManager, bottomNavDrawerFragment.tag)
        }

        // Handle menu item clicks - works different than in an Activity
        binding.bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> {
                    Toast.makeText(requireContext(), R.string.search, Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.more -> {
                    Toast.makeText(requireContext(), R.string.more, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() = binding.rvNotes.apply {

        layoutManager = LinearLayoutManager(requireContext())
        addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        labelRecyclerAdapter = LabelRecyclerAdapter(requireContext()).apply {
            submitNewLabel(
                mainViewModel.currentSelectedLabel.value ?: requireContext().getString(R.string.all_notes_title)
            )
        }
        notesRecyclerViewAdapter = NotesRecyclerViewAdapter(requireContext(), adapterInteraction)

        concatAdapter = ConcatAdapter(labelRecyclerAdapter, notesRecyclerViewAdapter)
        adapter = concatAdapter

        val noteListCallback = NotesListItemTouchHelper(notesRecyclerViewAdapter)
        val itemTouchHelper = ItemTouchHelper(noteListCallback)
        notesRecyclerViewAdapter.setTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(this)
    }

    private val adapterInteraction = object : NotesRecyclerViewAdapter.Interaction {
        override fun onItemSelected(item: Note) {
            findNavController().navigate(R.id.action_homeFragment_to_addEditNoteFragment)
        }

        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            val note = notes[fromPosition]
            notes.removeAt(fromPosition)
            notes.add(toPosition, note)
        }

        override fun onDelete(position: Int) {
            mainViewModel.deleteNote(notes[position])

        }
    }

    private fun fetchData(): Unit = mainViewModel.notes.observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Loading -> {
                binding.progressBar.show()
            }
            is Resource.Success -> {
                binding.progressBar.hide()
                notes = result.data.toMutableList()
                notesRecyclerViewAdapter.submitList(notes)
            }
            is Resource.Failure -> {
                binding.progressBar.hide()
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.error_fecthing_notes, result.exception),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    })

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}