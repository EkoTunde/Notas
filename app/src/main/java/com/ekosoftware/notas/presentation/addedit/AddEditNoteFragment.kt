package com.ekosoftware.notas.presentation.addedit

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.ekosoftware.notas.data.model.Note
import com.ekosoftware.notas.databinding.FragmentAddEditNoteBinding
import com.ekosoftware.notas.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.hypot
import kotlin.math.max


@AndroidEntryPoint
class AddEditNoteFragment : Fragment() {

    /*val items = listOf("Material", "Design", "Components", "Android")
    val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
    (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)*/

    companion object {
        const val ARG_ACTION = "add or edit action argument"
    }

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var receivedNote: Note? = null

    private val mainViewModel by activityViewModels<MainViewModel>()

    private var edit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        requireArguments().let {
            edit = it.getBoolean(ARG_ACTION)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (edit) fetchData()
    }

    private fun fetchData() {
        mainViewModel.selectedNote.observe(viewLifecycleOwner, {
            receivedNote = it
        })
    }

    private fun save() : Unit = if (edit) {
        TODO("Updates -> id is known")
    } else {
        TODO("Inserts -> id is unknown?")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}