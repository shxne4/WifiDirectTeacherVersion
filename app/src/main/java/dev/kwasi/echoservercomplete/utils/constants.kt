package dev.kwasi.echoservercomplete.utils

object Constants {
    // Request code for location permissions
    const val LOCATION_PERMISSION_REQUEST_CODE = 100

    // Request code for nearby Wi-Fi devices permissions (Android 13+)
    const val NEARBY_WIFI_DEVICES_PERMISSION_REQUEST_CODE = 101

    // Key for passing message data between activities
    const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

    // Key for attendee name when asking a question
    const val EXTRA_ATTENDEE_NAME = "EXTRA_ATTENDEE_NAME"

    // Default message when there are no connected devices
    const val NO_ATTENDEES_MESSAGE = "No attendees connected."
}
