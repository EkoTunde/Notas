package com.ekosoftware.notas.presentation.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.databinding.FragmentSearchBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.presentation.home.NotesRecyclerAdapter
import com.ekosoftware.notas.util.hide
import com.ekosoftware.notas.util.show
import com.google.android.material.snackbar.Snackbar


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var searchNotesRecyclerAdapter: SearchNotesRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        initSearchUI()
        initRecyclerView()
        subscribeSearchResultObserver()
    }

    private fun setUpNavigation() = binding.toolbar.apply {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupWithNavController(findNavController(), appBarConfiguration)
    }

    private val TAG = "SearchFragment"
    private fun initSearchUI() {
        val searchItem: MenuItem = binding.toolbar.menu.findItem(R.id.search)
        searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnCloseListener { true }

        val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = getString(R.string.search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainViewModel.submitSearchText(query)
                Log.d(TAG, "onQueryTextSubmit: query is $query")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.submitSearchText(newText)
                Log.d(TAG, "onQueryTextChange: newText is $newText")
                return false
            }
        })
    }

    private fun subscribeSearchResultObserver() = mainViewModel.searchResults.observe(viewLifecycleOwner, { result ->
        when (result) {
            is Resource.Loading -> {
                binding.progressBar.show()
            }
            is Resource.Success -> {
                binding.progressBar.hide()
                searchNotesRecyclerAdapter.submitList(result.data)
            }
            is Resource.Failure -> {
                binding.progressBar.hide()
                Snackbar.make(binding.coordLayout, getString(R.string.error_searching_notes), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    })

    private fun initRecyclerView() = binding.rvResults.apply {
        layoutManager = LinearLayoutManager(requireContext())
        searchNotesRecyclerAdapter = SearchNotesRecyclerAdapter(requireContext(), searchNotesAdapterInteraction)
        adapter = searchNotesRecyclerAdapter
    }

    private val searchNotesAdapterInteraction = object : SearchNotesRecyclerAdapter.Interaction {
        override fun onItemSelected(note: Note) {
            mainViewModel.selectNote(note)
            val action = SearchFragmentDirections.actionSearchFragmentToAddEditNoteFragment(true)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}