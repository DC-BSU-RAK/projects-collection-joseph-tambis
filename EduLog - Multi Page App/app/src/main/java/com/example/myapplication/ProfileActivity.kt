package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityProfileBinding

/**
 * Activity for managing user profile information.
 * Allows users to set their name, gender, and current study level.
 */
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefsManager: SharedPrefsManager

    // Predefined list of study levels for the spinner
    private val studyLevels = arrayOf("Undergraduate", "Postgraduate", "Doctorate", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        setupToolbar()
        setupSpinner()
        loadProfileData()

        // Listener for the Save button to persist user changes
        binding.btnSaveProfile.setOnClickListener {
            saveProfileData()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Back button to return to the dashboard
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Initializes the study level Spinner with the predefined array.
     */
    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, studyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLevel.adapter = adapter
    }

    /**
     * Populates the UI fields with existing data from Shared Preferences.
     */
    private fun loadProfileData() {
        val (name, gender, level) = prefsManager.getProfile()
        binding.etName.setText(name)

        // Select the appropriate RadioButton based on saved gender
        when (gender) {
            "Male" -> binding.rbMale.isChecked = true
            "Female" -> binding.rbFemale.isChecked = true
            "Other" -> binding.rbOther.isChecked = true
        }

        // Set the spinner to the previously saved study level
        val levelIndex = studyLevels.indexOf(level)
        if (levelIndex >= 0) {
            binding.spinnerLevel.setSelection(levelIndex)
        }
    }

    /**
     * Validates and saves the user's profile information.
     */
    private fun saveProfileData() {
        val name = binding.etName.text.toString()
        val gender = when {
            binding.rbMale.isChecked -> "Male"
            binding.rbFemale.isChecked -> "Female"
            binding.rbOther.isChecked -> "Other"
            else -> ""
        }
        val level = binding.spinnerLevel.selectedItem.toString()

        // Simple validation: name must not be empty
        if (name.isBlank()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SharedPreferences and notify user
        prefsManager.saveProfile(name, gender, level)
        Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
        // Return to the previous screen (MainActivity)
        finish()
    }
}
