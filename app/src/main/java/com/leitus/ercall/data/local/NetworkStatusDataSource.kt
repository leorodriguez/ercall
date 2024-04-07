package com.leitus.ercall.data.local

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.leitus.ercall.data.model.ConnectionState

interface NetworkStatusDataSource {
    fun getCurrentConnectivityState(): ConnectionState
}

class NetworkStatusDataSourceImpl(private val context: Context) : NetworkStatusDataSource {
    override fun getCurrentConnectivityState(
    ): ConnectionState {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val connected = connectivityManager.allNetworks.any { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        }
        return if (connected) ConnectionState.Available else ConnectionState.Unavailable
    }
}