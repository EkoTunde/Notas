package com.ekosoftware.notas.presentation.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.databinding.FragmentHomeBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.presentation.home.adapter.NotesListItemTouchHelper
import com.ekosoftware.notas.presentation.home.adapter.NotesRecyclerAdapter
import com.ekosoftware.notas.presentation.home.adapter.SpaceRecyclerAdapter
import com.ekosoftware.notas.presentation.home.bottom.BottomExtendedMenuFragment
import com.ekosoftware.notas.presentation.home.bottom.BottomNavigationDrawerFragment
import com.ekosoftware.notas.util.hide
import com.ekosoftware.notas.util.show
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        private const val STATE_LOADING = "loading"
        private const val STATE_NOT_LOADING = "notLoading"
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

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
                val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                findNavController().navigate(action)
                true
            }
            R.id.more -> {
                if (mainViewModel.selectedLabel().id != null) {
                    val bottomExtendedMenuFragment =
                        BottomExtendedMenuFragment(bottomExtendedMenuFragmentListener)
                    bottomExtendedMenuFragment.show(
                        requireActivity().supportFragmentManager,
                        bottomExtendedMenuFragment.tag
                    )
                }
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

    private val bottomExtendedMenuFragmentListener = object : BottomExtendedMenuFragment.BottomExtendedMenuListener {
        override fun onRenameLabel(label: Label) {
            val action = HomeFragmentDirections.actionHomeFragmentToEditLabelNameFragment(label)
            findNavController().navigate(action)
        }

        override fun onDeleteLabel(label: Label) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_label_question)
                .setMessage(R.string.delete_label_explanation)
                .setPositiveButton(R.string.delete) { dialog, which ->
                    mainViewModel.deleteLabel(label)
                    dialog.dismiss()
                }.setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }.setCancelable(true).show()
        }
    }

    private fun initRecyclerView() = binding.rvNotes.apply {

        layoutManager = LinearLayoutManager(requireContext())

        notesRecyclerAdapter = NotesRecyclerAdapter(requireContext(), adapterInteraction)
        concatAdapter = ConcatAdapter(notesRecyclerAdapter, SpaceRecyclerAdapter(requireContext()))
        adapter = concatAdapter

        val noteListCallback = NotesListItemTouchHelper(notesRecyclerAdapter)
        val itemTouchHelper = ItemTouchHelper(noteListCallback)
        notesRecyclerAdapter.setTouchHelper(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(this)
    }

    private val adapterInteraction = object : NotesRecyclerAdapter.Interaction {
        override fun onItemSelected(item: Note) {
            mainViewModel.selectNote(item)
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditNoteFragment(true)
            findNavController().navigate(action)
        }

        override fun onDelete(position: Int) {
            if (position < notes.size) {

                val noteToDelete = notes[position]
                mainViewModel.deleteNote(noteToDelete)

                Snackbar.make(
                    binding.coordLayout,
                    getString(R.string.event_msg, getString(R.string.deleted)),
                    Snackbar.LENGTH_LONG
                ).apply {
                    anchorView = binding.btnAddNote
                    setAction(R.string.undo) {
                        mainViewModel.insertNote(noteToDelete)
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

    private fun observeCurrentLabel() =
        mainViewModel.currentSelectedLabelName.observe(viewLifecycleOwner, { labelName ->
            val name = labelName ?: requireContext().getString(R.string.all_notes_title)
            binding.toolbar.title = name
            binding.collapsingToolbarLayout.title = name
        })

    private fun fetchData(): Unit = mainViewModel.notes.observe(viewLifecycleOwner, { result ->
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
                arrayOf(bottomAppBar, btnAddNote).forEach { it.isEnabled = false }
            }
            else -> {
                progressBar.hide()
                rvNotes.show()
                arrayOf(bottomAppBar, btnAddNote).forEach { it.isEnabled = true }
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