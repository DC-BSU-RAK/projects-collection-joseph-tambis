package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.databinding.ActivityNotesBinding

/**
 * Activity for displaying and managing study notes.
 * Supports searching, category filtering, and pinning notes.
 * Notes are displayed in a staggered grid layout.
 */
class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        setupFilters()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Enable back button navigation
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Initializes the RecyclerView with a staggered grid layout and the note adapter.
     */
    private fun setupRecyclerView() {
        val notes = prefsManager.getNotes()
        noteAdapter = NoteAdapter(
            allNotes = notes,
            onNoteClick = { note -> 
                // Open note editor with the selected note's ID
                val intent = Intent(this, NoteEditorActivity::class.java)
                intent.putExtra("NOTE_ID", note.id)
                startActivity(intent)
            },
            onDeleteClick = { note -> showDeleteConfirmation(note) },
            onPinClick = { note -> togglePinNote(note) }
        )
        // 2-column staggered grid for a "sticky note" look
        binding.rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.rvNotes.adapter = noteAdapter
    }

    private fun setupClickListeners() {
        // FAB to create a new note
        binding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, NoteEditorActivity::class.java))
        }
    }

    /**
     * Configures the search bar to filter notes in real-time as the user types.
     */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                noteAdapter.filterByText(s.toString())
                updateEmptyState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Sets up category chips to filter notes by their labels.
     */
    private fun setupFilters() {
        binding.cgFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val category = when (checkedIds.firstOrNull()) {
                R.id.filterGeneral -> "General"
                R.id.filterIdea -> "Idea"
                R.id.filterTodo -> "To-Do"
                R.id.filterImportant -> "Important"
                else -> "All"
            }
            noteAdapter.filterByCategory(category)
            updateEmptyState()
        }
    }

    /**
     * Toggles the pinned status of a note and refreshes the list.
     */
    private fun togglePinNote(note: Note) {
        val notes = prefsManager.getNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note.copy(isPinned = !note.isPinned)
            prefsManager.saveNotes(notes)
            noteAdapter.updateData(notes)
            // Re-apply current search filter after updating data
            noteAdapter.filterByText(binding.etSearch.text?.toString() ?: "")
        }
    }

    /**
     * Shows/hides the empty state message based on whether there are notes to show.
     */
    private fun updateEmptyState() {
        if (noteAdapter.itemCount == 0) {
            binding.tvEmptyNotes.visibility = View.VISIBLE
            binding.rvNotes.visibility = View.GONE
            if (binding.etSearch.text?.isNotEmpty() == true) {
                binding.tvEmptyNotes.text = "No notes match your search."
            } else {
                binding.tvEmptyNotes.text = "No notes yet. Tap + to add one!"
            }
        } else {
            binding.tvEmptyNotes.visibility = View.GONE
            binding.rvNotes.visibility = View.VISIBLE
        }
    }

    private fun showDeleteConfirmation(note: Note) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                val notes = prefsManager.getNotes().toMutableList()
                notes.removeAll { it.id == note.id }
                prefsManager.saveNotes(notes)
                noteAdapter.updateData(notes)
                noteAdapter.filterByText(binding.etSearch.text?.toString() ?: "")
                updateEmptyState()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Option to wipe all notes at once after confirmation.
     */
    private fun showClearAllConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Clear All Notes")
            .setMessage("This will delete all your notes. This action cannot be undone.")
            .setPositiveButton("Clear All") { _, _ ->
                prefsManager.saveNotes(emptyList())
                noteAdapter.updateData(emptyList())
                updateEmptyState()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notes_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                showClearAllConfirmation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh notes list when returning from the editor
        val notes = prefsManager.getNotes()
        noteAdapter.updateData(notes)
        noteAdapter.filterByText(binding.etSearch.text?.toString() ?: "")
        updateEmptyState()
    }
}
