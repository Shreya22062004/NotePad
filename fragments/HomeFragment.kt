package com.example.notepad.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notepad.MainActivity
import com.example.notepad.R
import com.example.notepad.adapters.NoteAdapter
import com.example.notepad.databinding.FragmentHomeBinding
import com.example.notepad.model.Note
import com.example.notepad.viewModel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home) , SearchView.OnQueryTextListener , MenuProvider{

    private var homeBinding : FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container , false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()

        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }
    }
        fun updateUI(note: List<Note>?) {
            if(note != null){
                if(note.isNotEmpty()){
                    binding.emptyNotesImage.visibility = View.GONE
                    binding.homeRecyclerView.visibility = View.VISIBLE
                }
                else {
                    binding.emptyNotesImage.visibility = View.VISIBLE
                    binding.homeRecyclerView.visibility = View.GONE
                }
            }
        }

        private fun setupHomeRecyclerView() {
            noteAdapter = NoteAdapter()
            binding.homeRecyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL)
                setHasFixedSize(true)
                adapter = noteAdapter
            }

            activity?.let {
                notesViewModel.getAllNotes().observe(viewLifecycleOwner) { note ->
                    noteAdapter.differ.submitList(note)
                    updateUI(note)
                }
            }
        }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query"

        notesViewModel.searchNote(searchQuery).observe(this) { list ->
            noteAdapter.differ.submitList(list)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null) {
            searchNote(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu , menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }
}