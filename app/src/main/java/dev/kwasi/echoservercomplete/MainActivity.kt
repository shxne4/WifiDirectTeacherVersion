package dev.kwasi.echoservercomplete

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.kwasi.echoservercomplete.communication.CommunicationActivity


class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Start button
        startButton = findViewById(R.id.start_button)

        // Set up the click listener for the Start button
        startButton.setOnClickListener {
            navigateToCommunicationActivity()
        }
    }

    private fun navigateToCommunicationActivity() {
        val intent = Intent(this, CommunicationActivity::class.java)
        startActivity(intent)
    }
}
