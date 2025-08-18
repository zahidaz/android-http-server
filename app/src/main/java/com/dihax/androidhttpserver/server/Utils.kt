package com.dihax.androidhttpserver.server

import java.net.Inet4Address
import java.net.NetworkInterface

fun getLocalIpAddress(): String = try {
    NetworkInterface.getNetworkInterfaces().toList()
        .flatMap { it.inetAddresses.toList() }
        .firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
        ?.hostAddress ?: "127.0.0.1"
} catch (_: Exception) {
    "127.0.0.1"
}