package com.example.data

import kotlin.math.pow

data class SubnetResult(
    val networkIp: String,
    val broadcastIp: String,
    val netmask: String,
    val wildcardMask: String,
    val firstUsableIp: String,
    val lastUsableIp: String,
    val totalUsableHosts: Long
)

data class DefaultCredential(
    val brand: String,
    val model: String,
    val ip: String,
    val username: String,
    val password: String,
    val wifiSsid: String = "",
    val wifiKey: String = "",
    val notesBn: String = "",
    val notesEn: String = ""
) {
    fun notes(): String = str(notesBn, notesEn)
}

data class Rj45Pin(
    val pinNumber: Int,
    val colorNameBn: String,
    val colorNameEn: String,
    val colorHex: Long,
    val isStriped: Boolean = false,
    val stripeColorHex: Long = 0xFFFFFFFF
)

object TechToolsData {

    fun calculateSubnet(ipStr: String, cidr: Int): SubnetResult? {
        val parts = ipStr.trim().split(".")
        if (parts.size != 4) return null
        val octets = parts.mapNotNull { it.toIntOrNull() }
        if (octets.size != 4 || octets.any { it !in 0..255 }) return null
        if (cidr !in 1..32) return null

        val ipLong = (octets[0].toLong() shl 24) or (octets[1].toLong() shl 16) or (octets[2].toLong() shl 8) or octets[3].toLong()
        val maskLong = if (cidr == 0) 0L else (-1L shl (32 - cidr)) and 0xFFFFFFFFL
        val wildcardLong = maskLong.inv() and 0xFFFFFFFFL
        val networkLong = ipLong and maskLong
        val broadcastLong = networkLong or wildcardLong

        fun longToIp(v: Long): String {
            return "${(v shr 24) and 0xFF}.${(v shr 16) and 0xFF}.${(v shr 8) and 0xFF}.${v and 0xFF}"
        }

        val totalHosts = if (cidr >= 31) {
            if (cidr == 31) 2L else 1L
        } else {
            (2.0.pow(32 - cidr).toLong()) - 2
        }

        val firstUsable = if (cidr >= 31) networkLong else networkLong + 1
        val lastUsable = if (cidr >= 31) broadcastLong else broadcastLong - 1

        return SubnetResult(
            networkIp = longToIp(networkLong),
            broadcastIp = longToIp(broadcastLong),
            netmask = longToIp(maskLong),
            wildcardMask = longToIp(wildcardLong),
            firstUsableIp = longToIp(firstUsable),
            lastUsableIp = longToIp(lastUsable),
            totalUsableHosts = totalHosts
        )
    }

    val rj45T568B = listOf(
        Rj45Pin(1, "সাদা-কমলা (White-Orange)", "White-Orange", 0xFFFF9800, true),
        Rj45Pin(2, "কমলা (Orange)", "Orange", 0xFFFF9800),
        Rj45Pin(3, "সাদা-সবুজ (White-Green)", "White-Green", 0xFF4CAF50, true),
        Rj45Pin(4, "নীল (Blue)", "Blue", 0xFF2196F3),
        Rj45Pin(5, "সাদা-নীল (White-Blue)", "White-Blue", 0xFF2196F3, true),
        Rj45Pin(6, "সবুজ (Green)", "Green", 0xFF4CAF50),
        Rj45Pin(7, "সাদা-বাদামী (White-Brown)", "White-Brown", 0xFF795548, true),
        Rj45Pin(8, "বাদামী (Brown)", "Brown", 0xFF795548)
    )

    val rj45T568A = listOf(
        Rj45Pin(1, "সাদা-সবুজ (White-Green)", "White-Green", 0xFF4CAF50, true),
        Rj45Pin(2, "সবুজ (Green)", "Green", 0xFF4CAF50),
        Rj45Pin(3, "সাদা-কমলা (White-Orange)", "White-Orange", 0xFFFF9800, true),
        Rj45Pin(4, "নীল (Blue)", "Blue", 0xFF2196F3),
        Rj45Pin(5, "সাদা-নীল (White-Blue)", "White-Blue", 0xFF2196F3, true),
        Rj45Pin(6, "কমলা (Orange)", "Orange", 0xFFFF9800),
        Rj45Pin(7, "সাদা-বাদামী (White-Brown)", "White-Brown", 0xFF795548, true),
        Rj45Pin(8, "বাদামী (Brown)", "Brown", 0xFF795548)
    )

    val defaultLogins = listOf(
        DefaultCredential("TP-Link", "Archer C6 / C80 / A6", "192.168.0.1", "admin", "admin (বা প্রথমবার সেট করুন)", "TP-Link_XXXX", "নিচের স্টিকারে দেওয়া"),
        DefaultCredential("TP-Link", "WR840N / WR841N / WR845N", "192.168.0.1", "admin", "admin", "TP-Link_XXXX", "স্টিকারে পিন কোড"),
        DefaultCredential("Tenda", "F3 / F6 / AC10 / TX2", "192.168.0.1", "admin", "(ফাঁকা / কোনো পাসওয়ার্ড নেই)", "Tenda_XXXXXX", "ওপেন (পাসওয়ার্ড ছাড়া)"),
        DefaultCredential("MikroTik", "RouterBOARD / hEX / hAP", "192.168.88.1", "admin", "(পাসওয়ার্ড ছাড়া ফাঁকা)", "MikroTik", "ওপেন"),
        DefaultCredential("Huawei", "HG8245H / EG8141A5 (Admin)", "192.168.100.1", "telecomadmin", "admintelecom", "Huawei-XXXX", "স্টিকার দেখুন"),
        DefaultCredential("Huawei", "HG8145V5 / HG8546M (User)", "192.168.100.1", "root", "adminHW", "Huawei-XXXX", "স্টিকার দেখুন"),
        DefaultCredential("ZTE", "F660 / F670L / F609", "192.168.1.1", "admin", "admin", "ZTE_XXXX", "স্টিকার দেখুন"),
        DefaultCredential("ZTE", "ZXHN F601 / F623 (Super)", "192.168.1.1", "administrator", "ZTE@super", "ZTE-WiFi", "পাসওয়ার্ড স্টিকারে"),
        DefaultCredential("D-Link", "DIR-615 / DIR-825", "192.168.0.1", "admin", "(ফাঁকা অথবা admin)", "dlink-XXXX", "ফাঁকা"),
        DefaultCredential("Netis", "WF2409E / W4 / WF2419", "192.168.1.1", "guest", "guest@netis", "netis", "password"),
        DefaultCredential("Mercusys", "MW301R / MW305R / AC12", "192.168.1.1", "admin", "(প্রথমবার তৈরি করতে হয়)", "MERCUSYS_XXXX", "ওপেন"),
        DefaultCredential("Xiaomi", "Mi Router 4A / 4C / AX3000", "192.168.31.1", "admin", "(প্রথমবার তৈরি করতে হয়)", "Xiaomi_XXXX", "ওপেন"),
        DefaultCredential("Totolink", "N200RE / N300RT / A720R", "192.168.0.1", "admin", "admin", "TOTOLINK", "ওপেন"),
        DefaultCredential("V-SOL", "V2801SG / V2804AX EPON/GPON", "192.168.1.1", "admin", "stdONUnumVsol", "VSOL-WiFi", "12345678"),
        DefaultCredential("BDCOM", "P1501C / GP1702 GEPON", "192.168.1.1", "admin", "admin", "BDCOM-ONU", "12345678"),
        DefaultCredential("C-Data", "FD511G-E-F270 EPON", "192.168.1.1", "admin", "admin", "CData-ONU", "12345678"),
        DefaultCredential("Fiberhome", "AN5506-04 / HG6245D", "192.168.1.1", "admin", "admin", "FiberHome_XXXX", "12345678"),
        DefaultCredential("OpenWrt", "All Models (LuCI Web)", "192.168.1.1", "root", "(পাসওয়ার্ড ছাড়া ফাঁকা)", "OpenWrt", "ওপেন"),
        DefaultCredential("Cisco", "Linksys E1200 / E900", "192.168.1.1", "admin", "admin", "Linksys-XXXX", "স্টিকারে দেওয়া")
    )

    val commonPorts = listOf(
        Pair("8291", "MikroTik Winbox GUI Management Port"),
        Pair("80", "HTTP Web Admin Interface (Router / ONU / Web Server)"),
        Pair("443", "HTTPS Secure Web Management"),
        Pair("53", "DNS Domain Name System (UDP/TCP)"),
        Pair("554", "RTSP Real-Time Streaming Protocol (IP Cameras / CCTV)"),
        Pair("8000", "Hikvision CCTV DVR / NVR Web & Mobile Port"),
        Pair("37777", "Dahua CCTV DVR / NVR Mobile Surveillance Port"),
        Pair("34567", "XM / Xiongmai CCTV DVR Video Port"),
        Pair("22", "SSH Secure Shell Terminal"),
        Pair("23", "Telnet Remote Terminal Protocol"),
        Pair("21", "FTP File Transfer Protocol"),
        Pair("3389", "RDP Windows Remote Desktop Protocol"),
        Pair("8080", "Alternative HTTP Proxy / Router Admin Port"),
        Pair("161", "SNMP Network Management Monitoring Protocol")
    )
}
