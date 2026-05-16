package com.example.myapplication

/**
 * Data model representing a single study session entry.
 */
data class StudySession(
    val id: String = java.util.UUID.randomUUID().toString(), // Unique identifier for the session
    val subject: String,                                     // Subject or topic studied
    val date: String,                                        // Date of the session (e.g., "DD/MM/YYYY")
    val startTime: String,                                   // Starting time (e.g., "HH:mm")
    val endTime: String                                      // Ending time (e.g., "HH:mm")
)
