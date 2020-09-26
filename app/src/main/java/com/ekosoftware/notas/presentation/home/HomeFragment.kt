package com.ekosoftware.notas.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        private const val STATE_LOADING = "loading"
        private const val STATE_NOT_LOADING = "notLoading"
    }

    private fun paraHacer() {
        TODO(
            "Tengo que traer:" +
                    "- Labels -> para el bottom sheet dialog" +
                    "Tengo que hacer:" +
                    "- ver las notas" +
                    "- lanzar bottom sheet dialog" +
                    "- activar busqueda" +
                    "- Agregar/editar notas" +
                    "- Crear (y editar) labels"
        )
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private lateinit var notes: MutableList<Note>

    private lateinit var notesRecyclerAdapter: NotesRecyclerAdapter
    private lateinit var concatAdapter: ConcatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initBottomBar()
        observeCurrentLabel()
        fetchData()
    }

    private fun initBottomBar() = binding.apply {
        btnAddNote.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditNoteFragment(false)
            findNavController().navigate(action)
        }

        bottomAppBar.apply {
            initNavigation()
            initMenuListener()
        }
    }

    private fun BottomAppBar.initNavigation() = this.setNavigationOnClickListener {
        val bottomNavDrawerFragment =
            BottomNavigationDrawerFragment(bottomNavigationDrawerListener)
        bottomNavDrawerFragment.show(requireActivity().supportFragmentManager, bottomNavDrawerFragment.tag)
    }

    // Handle menu item clicks - works different than in an Activity
    private fun BottomAppBar.initMenuListener() = this.setOnMenuItemClickListener {
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

    private val bottomNavigationDrawerListener =
        object : BottomNavigationDrawerFragment.BottomNavigationDrawerListener {
            override fun onNewLabel() {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLabelsFragment())
            }
        }

    private fun initRecyclerView() = binding.rvNotes.apply {

        layoutManager = LinearLayoutManager(requireContext())
        //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        notesRecyclerAdapter = NotesRecyclerAdapter(requireContext(), adapterInteraction)

        val spaceRecyclerAdapter = SpaceRecyclerAdapter(requireContext())

        concatAdapter = ConcatAdapter(notesRecyclerAdapter, spaceRecyclerAdapter)
        adapter = concatAdapter

        val noteListCallback = NotesListItemTouchHelper(notesRecyclerAdapter)
        val itemTouchHelper = ItemTouchHelper(noteListCallback)
        notesRecyclerAdapter.setTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(this)
    }

    private val adapterInteraction = object : NotesRecyclerAdapter.Interaction {
        override fun onItemSelected(item: Note) {
            viewModel.selectNote(item)
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditNoteFragment(true)
            findNavController().navigate(action)
        }

        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            val note = notes[fromPosition]
            notes.removeAt(fromPosition)
            notes.add(toPosition, note)
        }

        override fun onDelete(position: Int) {
            if (position < notes.size) {

                val noteToDelete = notes[position]
                viewModel.deleteNote(noteToDelete)

                Snackbar.make(
                    binding.coordLayout,
                    getString(R.string.event_msg, getString(R.string.deleted)),
                    Snackbar.LENGTH_LONG
                ).apply {
                    anchorView = binding.btnAddNote
                    setAction(R.string.undo) {
                        viewModel.insertNote(noteToDelete)
                        try {
                            binding.rvNotes.smoothScrollToPosition(position)
                            notesRecyclerAdapter.notifyItemRangeChanged(0, position)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "error $e", Toast.LENGTH_SHORT).show()
                        }
                    }
                    show()
                }
            }
        }
    }

    private fun observeCurrentLabel() = viewModel.currentSelectedLabelName.observe(viewLifecycleOwner, { labelName ->
        val name = labelName ?: requireContext().getString(R.string.all_notes_title)
        binding.toolbar.title = name
        binding.collapsingToolbarLayout.title = name
    })

    private fun fetchData(): Unit = viewModel.notes.observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Loading -> changeState(STATE_LOADING)
            is Resource.Success -> {
                changeState(STATE_NOT_LOADING)
                notes = result.data.toMutableList()
                showNotesNotFound(notes.isEmpty())
                notesRecyclerAdapter.submitList(notes)
            }
            is Resource.Failure -> {
                changeState(STATE_NOT_LOADING)
                Snackbar.make(
                    binding.coordLayout,
                    getString(R.string.error_fecthing_notes, result.exception),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    })

    private fun changeState(state: String) = binding.apply {
        when (state) {
            STATE_LOADING -> {
                progressBar.show()
                rvNotes.hide()
                arrayOf(bottomAppBar, transparentFab, btnAddNote).forEach { it.isEnabled = false }
            }
            else -> {
                progressBar.hide()
                rvNotes.show()
                arrayOf(bottomAppBar, transparentFab, btnAddNote).forEach { it.isEnabled = true }
            }
        }
    }

    private fun showNotesNotFound(showBackground: Boolean) = binding.apply {
        if (showBackground) {
            rvNotes.hide()
            noNotesAddedBackground.show()
        } else {
            rvNotes.show()
            noNotesAddedBackground.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}