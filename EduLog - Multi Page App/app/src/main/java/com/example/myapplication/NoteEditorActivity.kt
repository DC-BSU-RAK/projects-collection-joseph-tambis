package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityNoteEditorBinding

/**
 * Activity for creating or editing a specific note.
 * Users can input a title, content, select a category, and toggle the pinned status.
 * It also supports sharing the note's content via external apps.
 */
class NoteEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var prefsManager: SharedPrefsManager
    private var noteId: String? = null
    private var isPinned: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        // Check if we are editing an existing note or creating a new one
        noteId = intent.getStringExtra("NOTE_ID")
        setupToolbar()
        loadNote()

        // Save button listener
        binding.btnSave.setOnClickListener {
            saveNote()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Back button to discard changes or go back
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        // Update title based on whether it's a new or existing note
        supportActionBar?.title = if (noteId == null) "New Note" else "Edit Note"
    }

    /**
     * If editing, loads the note details from local storage into the UI.
     */
    private fun loadNote() {
        noteId?.let { id ->
            val notes = prefsManager.getNotes()
            val note = notes.find { it.id == id }
            note?.let {
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
                isPinned = it.isPinned
                
                // Select the correct category chip
                when (it.category) {
                    "Idea" -> binding.chipIdea.isChecked = true
                    "To-Do" -> binding.chipTodo.isChecked = true
                    "Important" -> binding.chipImportant.isChecked = true
                    else -> binding.chipGeneral.isChecked = true
                }
            }
        }
    }

    /**
     * Validates and persists the note data.
     */
    private fun saveNote() {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        // Basic validation: don't save if both fields are empty
        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Cannot save empty note", Toast.LENGTH_SHORT).show()
            return
        }

        // Determine category from selected chip
        val category = when (binding.cgCategory.checkedChipId) {
            R.id.chipIdea -> "Idea"
            R.id.chipTodo -> "To-Do"
            R.id.chipImportant -> "Important"
            else -> "General"
        }

        val notes = prefsManager.getNotes().toMutableList()
        if (noteId == null) {
            // Create a new Note object and add to list
            val newNote = Note(
                title = title, 
                content = content, 
                category = category,
                isPinned = isPinned
            )
            notes.add(0, newNote)
        } else {
            // Update the existing note in the list
            val index = notes.indexOfFirst { it.id == noteId }
            if (index != -1) {
                notes[index] = notes[index].copy(
                    title = title,
                    content = content,
                    category = category,
                    timestamp = System.currentTimeMillis(),
                    isPinned = isPinned
                )
            }
        }

        // Save updated list back to SharedPreferences
        prefsManager.saveNotes(notes)
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu with Pin and Share options
        menuInflater.inflate(R.menu.note_editor_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        // Dynamically update the pin icon based on state
        val pinItem = menu.findItem(R.id.action_pin)
        pinItem?.setIcon(if (isPinned) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                shareNote()
                true
            }
            R.id.action_pin -> {
                // Toggle pin state and refresh menu icon
                isPinned = !isPinned
                invalidateOptionsMenu()
                val status = if (isPinned) "Pinned" else "Unpinned"
                Toast.makeText(this, "Note $status", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Share the note text using Android's system share intent.
     */
    private fun shareNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etContent.text.toString()
        val shareBody = if (title.isNotEmpty()) "$title\n\n$content" else content

        if (shareBody.isEmpty()) {
            Toast.makeText(this, "Nothing to share", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}
