package com.example.myapplication

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for the Notes RecyclerView.
 * Handles the display of notes in a grid, including filtering by text and category.
 * It also manages the visual state of pinned notes and category badges.
 */
class NoteAdapter(
    private var allNotes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit,
    private val onPinClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    // Internal list that holds only the notes matching the current filters
    private var filteredNotes: List<Note> = sortNotes(allNotes)
    private var currentQuery: String = ""
    private var currentCategory: String = "All"

    class ViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Sorts notes so that pinned notes appear first, then sorted by most recent timestamp.
     */
    private fun sortNotes(notes: List<Note>): List<Note> {
        return notes.sortedWith(compareByDescending<Note> { it.isPinned }.thenByDescending { it.timestamp })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = filteredNotes[position]
        holder.binding.tvNoteTitle.text = note.title
        holder.binding.tvNoteContent.text = note.content
        holder.binding.tvCategory.text = note.category
        
        // Format the timestamp into a readable date string
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.binding.tvNoteDate.text = sdf.format(Date(note.timestamp))

        // Set card background color based on the note's assigned color resource
        holder.binding.root.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, note.colorRes))

        // Dynamic coloring for category badges
        val catColor = when (note.category) {
            "Idea" -> "#FF9800"      // Orange
            "To-Do" -> "#4CAF50"     // Green
            "Important" -> "#F44336" // Red
            else -> "#607D8B"        // Blue Grey
        }
        holder.binding.tvCategory.backgroundTintList = ColorStateList.valueOf(Color.parseColor(catColor))

        // Update pin icon to show if note is currently pinned
        holder.binding.btnPinNote.setImageResource(
            if (note.isPinned) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        )

        // Setup interaction listeners
        holder.binding.root.setOnClickListener { onNoteClick(note) }
        holder.binding.btnDeleteNote.setOnClickListener { onDeleteClick(note) }
        holder.binding.btnPinNote.setOnClickListener { onPinClick(note) }
    }

    override fun getItemCount(): Int = filteredNotes.size

    /**
     * Updates the base data set and re-applies any active filters.
     */
    fun updateData(newNotes: List<Note>) {
        allNotes = newNotes
        applyFilters()
    }

    /**
     * Filters notes by title or content matching the search query.
     */
    fun filterByText(query: String) {
        currentQuery = query
        applyFilters()
    }

    /**
     * Filters notes by their selected category.
     */
    fun filterByCategory(category: String) {
        currentCategory = category
        applyFilters()
    }

    /**
     * Combines category and text filters, then sorts and updates the visible list.
     */
    private fun applyFilters() {
        var filtered = if (currentCategory == "All") {
            allNotes
        } else {
            allNotes.filter { it.category == currentCategory }
        }

        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.title.contains(currentQuery, ignoreCase = true) ||
                it.content.contains(currentQuery, ignoreCase = true)
            }
        }

        filteredNotes = sortNotes(filtered)
        // Notify the adapter that the data set has changed to refresh the UI
        notifyDataSetChanged()
    }
}
