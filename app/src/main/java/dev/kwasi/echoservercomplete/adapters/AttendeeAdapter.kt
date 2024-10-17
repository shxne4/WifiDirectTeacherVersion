package dev.kwasi.echoservercomplete.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kwasi.echoservercomplete.R

class AttendeeAdapter(
    private val context: Context,
    private val attendees: List<String>,
    private val onAskQuestionClick: (String) -> Unit
) : RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder>() {

    class AttendeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val attendeeName: TextView = itemView.findViewById(R.id.attendee_name)
        val askQuestionButton: Button = itemView.findViewById(R.id.ask_question_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendeeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_attendee, parent, false)
        return AttendeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendeeViewHolder, position: Int) {
        val attendee = attendees[position]
        holder.attendeeName.text = attendee
        holder.askQuestionButton.setOnClickListener {
            onAskQuestionClick(attendee) // Trigger the action when the button is clicked
        }
    }

    override fun getItemCount(): Int = attendees.size
}
