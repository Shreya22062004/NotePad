package com.example.notepad.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notepad.MainActivity
import com.example.notepad.R
import com.example.notepad.databinding.FragmentEditNoteBinding
import com.example.notepad.model.Note
import com.example.notepad.viewModel.NoteViewModel


class EditNoteFragment : Fragment(R.layout.fragment_edit_note) , MenuProvider {

    private var editNoteFragment: FragmentEditNoteBinding? = null
    private val binding get() = editNoteFragment!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    private val args : EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        editNoteFragment = FragmentEditNoteBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this , viewLifecycleOwner , Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.editNoteTitle.setText(currentNote.noteTitle)
        binding.editNoteDesc.setText(currentNote.noteDesc)

        binding.editNoteFab.setOnClickListener {
            val noteTitle = binding.editNoteTitle.text.toString().trim()
            val noteDesc = binding.editNoteDesc.text.toString().trim()

            if(noteTitle.isNotEmpty()) {
                val note = Note(currentNote.id , noteTitle , noteDesc)
                notesViewModel.updateNote(note)
                view.findNavController().popBackStack(R.id.homeFragment , false)
            }
            else {
                Toast.makeText(context , "Please Enter Note Title" , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteNote() {
        activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle("Delete Note")
                setMessage("Do you want to Delete this note?")
                setPositiveButton("Delete"){ _ , _ ->
                    notesViewModel.deleteNote(currentNote)
                    Toast.makeText(context , "Note Deleted" , Toast.LENGTH_LONG).show()
                    view?.findNavController()?.popBackStack(R.id.homeFragment , false)
                }
                setNegativeButton("Cancel" , null)
            }.create().show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note , menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteNote()
                true
            } else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteFragment = null
    }
}