package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemStudySessionBinding

/**
 * Adapter for the Study Logs RecyclerView.
 * Displays a list of study sessions, showing the subject, date, and duration.
 */
class StudySessionAdapter(private var sessions: List<StudySession>) :
    RecyclerView.Adapter<StudySessionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemStudySessionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout using ViewBinding
        val binding = ItemStudySessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position]
        // Bind session data to UI components
        holder.binding.tvSubject.text = session.subject
        holder.binding.tvDate.text = session.date
        holder.binding.tvTimeRange.text = "${session.startTime} - ${session.endTime}"
    }

    override fun getItemCount(): Int = sessions.size

    /**
     * Updates the session list and refreshes the RecyclerView UI.
     */
    fun updateData(newSessions: List<StudySession>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}
