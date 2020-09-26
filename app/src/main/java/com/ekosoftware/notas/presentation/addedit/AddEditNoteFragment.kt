package com.ekosoftware.notas.presentation.addedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionInflater
import com.ekosoftware.notas.R
import com.ekosoftware.notas.core.Resource
import com.ekosoftware.notas.data.model.Label
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.databinding.FragmentAddEditNoteBinding
import com.ekosoftware.notas.presentation.MainViewModel
import com.ekosoftware.notas.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditNoteFragment : Fragment() {

    companion object {
        const val ARG_ACTION = "add or edit action argument"
    }

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var receivedNote: Note? = null

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var labels: List<Label>

    private var selectedLabel: Label? = null

    private val args: AddEditNoteFragmentArgs by navArgs()

    private val unSelectedLabelText by lazy {
        getString(R.string.no_selection_list_item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedLabel = Label(
            mainViewModel.currentSelectedLabelId.value,
            mainViewModel.currentSelectedLabelName.value ?: ""
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpNavigation()
        binding.txtContent.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                hideKeyboard()
                binding.bottomBar.menu.setGroupEnabled(0, false)
            } else {
                binding.bottomBar.menu.setGroupEnabled(0, true)
            }
        }
        fetchLabels()
        if (args.edit) fetchNote()

        binding.bottomBar.setOnMenuItemClickListener {
            Toast.makeText(requireContext(), R.string.upcoming_feature, Toast.LENGTH_SHORT).show()
            true
        }

        binding.btnAddLabel.setOnClickListener {
            val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToLabelsFragment()
            findNavController().navigate(action)
        }
    }

    private fun setUpNavigation() = binding.toolbar.apply {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupWithNavController(findNavController(), appBarConfiguration)
        this.setNavigationOnClickListener {
            hideKeyboard()
            save()
            mainViewModel.selectNote(null)
            findNavController().navigateUp()
        }
    }

    private fun fetchLabels() {
        /*mutableListOf(
            Label(1, "Ejemplo 1"),
            Label(2, "Ejemplo 2"),
            Label(3, "Ejemplo 3")
        ).let {
            labels = it.toList()
            it.loadInUI()
        }*/
        mainViewModel.labels.observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    labels = result.data
                    labels.loadInUI()
                }
                is Resource.Failure -> {
                }
            }
        })
    }

    private fun fetchNote() = mainViewModel.selectedNote.observe(viewLifecycleOwner, {
        receivedNote = it
        receivedNote?.loadInUI()
    })

    private fun Note.loadInUI() {
        binding.txtTitleLayout.editText?.setText(this.title)
        binding.txtContentLayout.editText?.setText(this.content)
    }

    private fun List<Label>.loadInUI() {
        mutableListOf<String>().apply {
            add(unSelectedLabelText)
            addAll(this@loadInUI.map { label -> label.name!! })
        }.also { items -> items.setUpDropdownList() }
    }

    private fun MutableList<String>.setUpDropdownList() =
        (binding.txtLabelLayout.editText as? AutoCompleteTextView)?.apply {
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, this@setUpDropdownList)
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                selectedLabel = if (position != 0) labels[position - 1] else {
                    setText("", false)
                    null
                }
            }
            selectedLabel?.let { setText(it.name, false) }
        }

    private fun updatedNote() = Note(
        id = receivedNote?.id,
        title = binding.txtTitle.text.toString(),
        content = binding.txtContent.text.toString(),
        labelId = selectedLabel?.id
    )

    private fun save() = updatedNote().takeIf {
        (!it.title.isNullOrEmpty() || !it.content.isNullOrEmpty())
                && (receivedNote?.title != it.title
                || receivedNote?.content != it.content
                || receivedNote?.labelId != it.labelId)
    }?.let {
        when (args.edit) {
            true -> mainViewModel.updateNote(it)
            false -> mainViewModel.insertNote(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}