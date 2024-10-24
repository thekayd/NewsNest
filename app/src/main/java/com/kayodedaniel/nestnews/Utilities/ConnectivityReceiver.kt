package com.kayodedaniel.nestnews.Utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val isConnected = NetworkUtils.isNetworkAvailable(context)
        if (isConnected) {
            Toast.makeText(context, "Back online!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You are offline!", Toast.LENGTH_SHORT).show()
        }
    }
}
