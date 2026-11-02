package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface

data class WifiConnectionInfo(
    val isConnected: Boolean,
    val ssid: String,
    val localIp: String,
    val gatewayIp: String,
    val linkSpeedMbps: Int,
    val frequencyMhz: Int,
    val subnetMask: String
)

object NetworkUtils {

    fun getConnectionInfo(context: Context): WifiConnectionInfo {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val activeNetwork = cm?.activeNetwork
        val caps = cm?.getNetworkCapabilities(activeNetwork)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        var ssid = "<Unknown>"
        var linkSpeed = 0
        var freq = 0
        var gateway = "192.168.0.1"
        var localIp = "127.0.0.1"
        var subnet = "255.255.255.0"

        if (wm != null) {
            val dhcp = wm.dhcpInfo
            if (dhcp != null && dhcp.gateway != 0) {
                gateway = intToIp(dhcp.gateway)
                subnet = intToIp(dhcp.netmask)
            }
            val info = wm.connectionInfo
            if (info != null) {
                val rawSsid = info.ssid ?: ""
                ssid = if (rawSsid.startsWith("\"") && rawSsid.endsWith("\"")) {
                    rawSsid.substring(1, rawSsid.length - 1)
                } else rawSsid
                if (ssid.isEmpty() || ssid == "<unknown ssid>") {
                    ssid = if (isWifi) "Connected WiFi" else "Mobile Data"
                }
                linkSpeed = info.linkSpeed
                freq = info.frequency
            }
        }

        localIp = getLocalIpAddress()

        return WifiConnectionInfo(
            isConnected = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true,
            ssid = ssid,
            localIp = localIp,
            gatewayIp = gateway,
            linkSpeedMbps = linkSpeed,
            frequencyMhz = freq,
            subnetMask = subnet
        )
    }

    fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                if (intf.isLoopback || !intf.isUp) continue
                val addresses = intf.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    private fun intToIp(i: Int): String {
        return "${i and 0xFF}.${(i shr 8) and 0xFF}.${(i shr 16) and 0xFF}.${(i shr 24) and 0xFF}"
    }
}
