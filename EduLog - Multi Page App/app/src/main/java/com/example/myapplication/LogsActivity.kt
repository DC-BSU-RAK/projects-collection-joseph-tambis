package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityLogsBinding

/**
 * Activity that displays the history of study sessions recorded by the user.
 * Users can view their past efforts and clear the history if needed.
 */
class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding
    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var adapter: StudySessionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = SharedPrefsManager(this)

        setupToolbar()
        setupRecyclerView()
        
        // Listener for the "Clear History" button to wipe all session data
        binding.btnClearHistory.setOnClickListener {
            prefsManager.saveStudySessions(emptyList())
            adapter.updateData(emptyList())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        // Back button support to return to the dashboard
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Initializes the RecyclerView with a vertical list layout to show study sessions.
     */
    private fun setupRecyclerView() {
        val sessions = prefsManager.getStudySessions()
        adapter = StudySessionAdapter(sessions)
        binding.rvSessions.layoutManager = LinearLayoutManager(this)
        binding.rvSessions.adapter = adapter
    }
}
