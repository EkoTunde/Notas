package com.ekosoftware.notas.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var notes: MutableList<Note>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        fetchData()
    }

    private lateinit var recyclerViewAdapter: NotesRecyclerViewAdapter

    private fun initRecyclerView() = binding.rvNotes.apply {
        layoutManager = LinearLayoutManager(requireContext())
        addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerViewAdapter = NotesRecyclerViewAdapter(requireContext(), adapterInteraction)
        adapter = recyclerViewAdapter
        val noteListCallback = NotesListItemTouchHelper(recyclerViewAdapter)
        val itemTouchHelper = ItemTouchHelper(noteListCallback)
        recyclerViewAdapter.setTouchHelper(itemTouchHelper)
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
            mainViewModel.saveIds(notes)
        }

        override fun onDelete(position: Int) {
            mainViewModel.deleteNote(notes[position])
            notes.apply { removeAt(position) }.also { mainViewModel.saveIds(it) }
        }
    }

    private fun fetchData(): Unit = mainViewModel.getAllNotes().observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Loading -> {
                binding.progressBar.show()
            }
            is Resource.Success -> {
                binding.progressBar.hide()
                notes = result.data.toMutableList()
                recyclerViewAdapter.submitList(notes)
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