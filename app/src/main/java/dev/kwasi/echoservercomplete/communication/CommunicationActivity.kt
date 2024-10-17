package dev.kwasi.echoservercomplete.communication

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kwasi.echoservercomplete.R
import dev.kwasi.echoservercomplete.adapters.AttendeeAdapter
import dev.kwasi.echoservercomplete.wifidirect.WifiDirectInterface
import dev.kwasi.echoservercomplete.wifidirect.WifiDirectManager
import dev.kwasi.echoservercomplete.ui.UIManager
import dev.kwasi.echoservercomplete.utils.Message

class CommunicationActivity : AppCompatActivity(), WifiDirectInterface {

    private lateinit var wifiDirectManager: WifiDirectManager
    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var wifiChannel: WifiP2pManager.Channel
    private lateinit var intentFilter: IntentFilter

    private lateinit var attendeesRecyclerView: RecyclerView
    private lateinit var chatHeader: TextView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var ssidTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var uiManager: UIManager

    private val attendees = mutableListOf<WifiP2pDevice>() // List to hold connected WifiP2pDevice objects
    private val chatMessages = mutableListOf<Message>() // List to hold chat messages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communication)

        // Initialize Wi-Fi Direct components
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        wifiChannel = wifiP2pManager.initialize(this, mainLooper, null)

        wifiDirectManager = WifiDirectManager(wifiP2pManager, wifiChannel, this)

        // Initialize UI components
        attendeesRecyclerView = findViewById(R.id.attendees_recycler_view)
        chatHeader = findViewById(R.id.chat_header)
        chatInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)
        ssidTextView = findViewById(R.id.ssid_text_view)
        passwordTextView = findViewById(R.id.password_text_view)

        // Initialize the UIManager with the SSID and Password TextViews
        uiManager = UIManager(this)
        uiManager.initNetworkInfo(ssidTextView, passwordTextView)

        // Set up the RecyclerView
        attendeesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set the adapter with the list of attendees and a click listener for the button
        val adapter = AttendeeAdapter(this, attendees) { selectedDevice ->
            connectToSelectedPeer(selectedDevice)  // Connect to the selected peer
        }
        attendeesRecyclerView.adapter = adapter

        // IntentFilter to listen for Wi-Fi Direct actions
        intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

        // Automatically start discovering peers
        discoverPeers()

        // Set up send button click listener
        sendButton.setOnClickListener {
            val messageContent = chatInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val message = Message(sender = "You", content = messageContent)
                chatMessages.add(message)
                chatInput.text.clear() // Clear the input field
                updateChatDisplay()
            } else {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateChatDisplay() {
        val chatDisplay = StringBuilder()
        for (message in chatMessages) {
            chatDisplay.append("${message.sender}: ${message.content}\n")
        }
        chatHeader.text = chatDisplay.toString()
    }

    override fun onResume() {
        super.onResume()
        // Register the BroadcastReceiver
        registerReceiver(wifiDirectManager, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the BroadcastReceiver
        unregisterReceiver(wifiDirectManager)
    }

    override fun onWiFiDirectStateChanged(isEnabled: Boolean) {
        Toast.makeText(this, "Wi-Fi Direct is ${if (isEnabled) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
    }

    override fun onPeerListUpdated(peers: Collection<WifiP2pDevice>) {
        // Clear and update attendees list with connected devices
        attendees.clear()
        attendees.addAll(peers)  // Store WifiP2pDevice objects directly
        // Notify the adapter that the data has changed
        (attendeesRecyclerView.adapter as AttendeeAdapter).notifyDataSetChanged()
        Toast.makeText(this, "Peers Updated: ${attendees.size} devices found", Toast.LENGTH_SHORT).show()
    }

    override fun onGroupStatusChanged(group: WifiP2pGroup?) {
        group?.let {
            val ssid = it.networkName
            val passphrase = it.passphrase ?: "N/A"
            // Update the UI with SSID and passphrase using UIManager
            uiManager.updateNetworkInfo(ssid, passphrase)
            Toast.makeText(this, "Group Formed: SSID=$ssid, Password=$passphrase", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(this, "Disconnected from Group", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeviceStatusChanged(thisDevice: WifiP2pDevice) {
        // Display information about the device
        Toast.makeText(this, "Device Status Changed: ${thisDevice.deviceName}", Toast.LENGTH_SHORT).show()
    }

    // Discover nearby peers
    private fun discoverPeers() {
        wifiDirectManager.discoverPeers()
    }

    // Connect to the selected peer device
    private fun connectToSelectedPeer(selectedDevice: WifiP2pDevice) {
        wifiDirectManager.connectToPeer(selectedDevice) // Use the existing connectToPeer method
    }

    // This method will be called when a peer is successfully connected
    override fun onPeerConnected() {
        createWifiDirectGroup() // Call to create a Wi-Fi Direct group
    }

    // Create a Wi-Fi Direct group as the Group Owner (GO)
    private fun createWifiDirectGroup() {
        wifiDirectManager.createGroup() // Calls the method in WifiDirectManager
    }

    private fun askQuestion(attendeeName: String) {
        val question = chatInput.text.toString()
        if (question.isNotEmpty()) {
            // Send the question to the selected attendee
            Toast.makeText(this, "Sent to $attendeeName: $question", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter a question.", Toast.LENGTH_SHORT).show()
        }
    }
}
