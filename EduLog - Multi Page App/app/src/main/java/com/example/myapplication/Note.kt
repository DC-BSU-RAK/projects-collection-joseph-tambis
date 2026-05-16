package com.example.myapplication

/**
 * Data model representing a single study note.
 */
data class Note(
    val id: String = java.util.UUID.randomUUID().toString(), // Unique identifier for each note
    val title: String,                                       // User-provided title
    val content: String,                                     // Detailed note content
    val category: String = "General",                        // Label for filtering (General, Idea, To-Do, Important)
    val colorRes: Int = R.color.note_yellow,                 // Background color resource for the note card
    val timestamp: Long = System.currentTimeMillis(),        // Time of creation or last update
    val isPinned: Boolean = false                            // Priority flag to keep note at the top
)
