package dev.kwasi.echoservercomplete.ui

import android.content.Context
import android.widget.TextView
import dev.kwasi.echoservercomplete.R

class UIManager(private val context: Context) {
    // UI components
    private var ssidTextView: TextView? = null
    private var passwordTextView: TextView? = null

    fun initNetworkInfo(ssidTextView: TextView, passwordTextView: TextView) {
        this.ssidTextView = ssidTextView
        this.passwordTextView = passwordTextView
    }

    fun updateNetworkInfo(ssid: String, password: String) {
        ssidTextView?.text = "SSID: $ssid"
        passwordTextView?.text = "Password: $password"
    }
}
