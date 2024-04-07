package com.leitus.ercall.data.model


sealed class ConnectionState {
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}