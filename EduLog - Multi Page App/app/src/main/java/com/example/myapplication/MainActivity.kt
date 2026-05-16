package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.DialogLogSessionBinding
import java.util.*

/**
 * The main dashboard of the application.
 * This activity displays the user's progress summary and provides navigation to
 * study logs, notes, and profile settings.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewBinding to access UI elements safely
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize preference manager for data persistence
        prefsManager = SharedPrefsManager(this)

        setupToolbar()
        updateUI()
        setupClickListeners()
    }

    /**
     * Configures the top toolbar/action bar.
     */
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Disable default title to use our custom layout's greeting text
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    /**
     * Updates the dashboard statistics and greeting message from shared preferences.
     */
    private fun updateUI() {
        // Retrieve profile info to personalize the greeting
        val (name, _, _) = prefsManager.getProfile()
        binding.tvToolbarGreeting.text = if (name.isNotEmpty()) "Hello, $name" else "Hello, Scholar"

        // Calculate counts for study sessions and notes to display on cards
        val sessionCount = prefsManager.getStudySessions().size
        val noteCount = prefsManager.getNotes().size
        
        binding.tvTotalSessions.text = sessionCount.toString()
        binding.tvTotalNotes.text = noteCount.toString()
    }

    /**
     * Sets up click listeners for the dashboard buttons and Floating Action Button.
     */
    private fun setupClickListeners() {
        // Navigate to Study Logs history
        binding.btnViewLogs.setOnClickListener {
            startActivity(Intent(this, LogsActivity::class.java))
        }
        
        // Navigate to Notes section
        binding.btnViewNotes.setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }

        // Open dialog to log a new study session
        binding.fabAdd.setOnClickListener {
            showLogSessionDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu items (Profile and Help) in the toolbar
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle menu item clicks
        return when (item.itemId) {
            R.id.action_instructions -> {
                showInstructions()
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Displays a dialog with input fields to record a new study session.
     */
    private fun showLogSessionDialog() {
        val dialogBinding = DialogLogSessionBinding.inflate(layoutInflater)
        val calendar = Calendar.getInstance()

        // Setup Date Picker listener
        dialogBinding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    dialogBinding.etDate.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Setup Start Time Picker listener
        dialogBinding.etStartTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                dialogBinding.etStartTime.setText(String.format("%02d:%02d", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Setup End Time Picker listener
        dialogBinding.etEndTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                dialogBinding.etEndTime.setText(String.format("%02d:%02d", hour, minute))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        // Build and show the final AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Log New Session")
            .setView(dialogBinding.root)
            .setPositiveButton("Log") { _, _ ->
                val subject = dialogBinding.etSubject.text.toString()
                val date = dialogBinding.etDate.text.toString()
                val startTime = dialogBinding.etStartTime.text.toString()
                val endTime = dialogBinding.etEndTime.text.toString()

                // Validate and save the session if valid
                if (subject.isNotBlank() && date.isNotBlank()) {
                    val newSession = StudySession(
                        subject = subject,
                        date = date,
                        startTime = startTime,
                        endTime = endTime
                    )
                    val sessions = prefsManager.getStudySessions().toMutableList()
                    sessions.add(0, newSession) // Add new session at the top
                    prefsManager.saveStudySessions(sessions)
                    updateUI() // Refresh dashboard stats immediately
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Shows a simple instruction guide for first-time users.
     */
    private fun showInstructions() {
        AlertDialog.Builder(this)
            .setTitle("How to use the Tracker")
            .setMessage("1. Set your profile via the edit icon on top right.\n" +
                    "2. Log sessions using the Floating Button.\n" +
                    "3. View and manage your history in 'Study Logs'.\n" +
                    "4. Jot down important points in 'Notes'.")
            .setPositiveButton("Got it", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh UI when returning to this activity (e.g., from Profile or Notes)
        updateUI()
    }
}
