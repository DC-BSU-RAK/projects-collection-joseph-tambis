package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Helper class to manage local data persistence using SharedPreferences.
 * Uses GSON to serialize/deserialize complex objects like lists of sessions and notes.
 */
class SharedPrefsManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("study_tracker_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_NAME = "user_name"
        private const val KEY_GENDER = "user_gender"
        private const val KEY_LEVEL = "user_level"
        private const val KEY_SESSIONS = "study_sessions"
        private const val KEY_NOTES_LIST = "study_notes_list"
    }

    /**
     * Saves user profile details to local storage.
     */
    fun saveProfile(name: String, gender: String, level: String) {
        sharedPreferences.edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_GENDER, gender)
            putString(KEY_LEVEL, level)
            apply()
        }
    }

    /**
     * Retrieves saved profile information.
     * @return A Triple containing Name, Gender, and Study Level.
     */
    fun getProfile(): Triple<String, String, String> {
        val name = sharedPreferences.getString(KEY_NAME, "") ?: ""
        val gender = sharedPreferences.getString(KEY_GENDER, "") ?: ""
        val level = sharedPreferences.getString(KEY_LEVEL, "") ?: ""
        return Triple(name, gender, level)
    }

    /**
     * Saves the entire list of study sessions to SharedPreferences as a JSON string.
     */
    fun saveStudySessions(sessions: List<StudySession>) {
        val json = gson.toJson(sessions)
        sharedPreferences.edit().putString(KEY_SESSIONS, json).apply()
    }

    /**
     * Retrieves the list of study sessions from SharedPreferences.
     */
    fun getStudySessions(): List<StudySession> {
        val json = sharedPreferences.getString(KEY_SESSIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<StudySession>>() {}.type
        return gson.fromJson(json, type)
    }

    /**
     * Saves the list of notes to local storage.
     */
    fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        sharedPreferences.edit().putString(KEY_NOTES_LIST, json).apply()
    }

    /**
     * Retrieves the list of saved notes.
     */
    fun getNotes(): List<Note> {
        val json = sharedPreferences.getString(KEY_NOTES_LIST, null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, type)
    }
}
