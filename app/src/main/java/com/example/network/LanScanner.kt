package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

data class LanDevice(
    val ip: String,
    val mac: String,
    val vendor: String,
    val hostname: String,
    val openPorts: List<Int>,
    val isGateway: Boolean = false,
    val latencyMs: Long = 0L
)

object LanScanner {

    private val commonPorts = intArrayOf(80, 443, 8291, 554, 8000, 22, 53, 8080)

    fun scanSubnet(gatewayIp: String, localIp: String): Flow<Pair<List<LanDevice>, Float>> = flow {
        val baseIp = gatewayIp.substringBeforeLast(".")
        val arpMap = readArpTable()
        val discovered = mutableListOf<LanDevice>()
        val totalHosts = 254
        val semaphore = Semaphore(28) // Concurrent workers

        emit(Pair(discovered.toList(), 0.05f))

        val jobs = (1..254).map { i ->
            val targetIp = "$baseIp.$i"
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).async {
                semaphore.withPermit {
                    probeHost(targetIp, targetIp == gatewayIp, arpMap[targetIp])
                }
            }
        }

        var completed = 0
        jobs.forEach { job ->
            val device = job.await()
            completed++
            if (device != null) {
                synchronized(discovered) {
                    discovered.add(device)
                    discovered.sortByDescending { it.isGateway }
                }
            }
            if (completed % 10 == 0 || completed == totalHosts) {
                emit(Pair(discovered.toList(), completed.toFloat() / totalHosts.toFloat()))
            }
        }

        // Re-read ARP table at the end because kernel ARP cache populates after socket attempts
        val updatedArp = readArpTable()
        val finalized = discovered.map { dev ->
            val mac = if (dev.mac == "00:00:00:00:00:00" || dev.mac.isEmpty()) {
                updatedArp[dev.ip] ?: "00:00:00:00:00:00"
            } else dev.mac
            dev.copy(mac = mac, vendor = resolveVendor(mac, dev.openPorts))
        }

        emit(Pair(finalized.sortedByDescending { it.isGateway }, 1.0f))
    }.flowOn(Dispatchers.IO)

    private fun probeHost(ip: String, isGateway: Boolean, preArpMac: String?): LanDevice? {
        val start = System.currentTimeMillis()
        var reachable = false
        val openPorts = mutableListOf<Int>()

        // 1. Check socket on common ports
        for (port in commonPorts) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(ip, port), 250)
                socket.close()
                reachable = true
                openPorts.add(port)
            } catch (_: Exception) {}
        }

        // 2. Fallback ICMP reachable
        if (!reachable) {
            try {
                val inet = InetAddress.getByName(ip)
                if (inet.isReachable(350)) {
                    reachable = true
                }
            } catch (_: Exception) {}
        }

        // If pre-ARP exists or host is gateway, it's alive
        if (preArpMac != null && preArpMac != "00:00:00:00:00:00") {
            reachable = true
        }

        if (!reachable && !isGateway) return null

        val latency = (System.currentTimeMillis() - start).coerceAtLeast(1L)
        val mac = preArpMac ?: "00:00:00:00:00:00"
        val vendor = resolveVendor(mac, openPorts)

        var hostname = ""
        try {
            val inet = InetAddress.getByName(ip)
            val h = inet.canonicalHostName
            if (h != ip) hostname = h
        } catch (_: Exception) {}

        return LanDevice(
            ip = ip,
            mac = mac,
            vendor = vendor,
            hostname = hostname,
            openPorts = openPorts,
            isGateway = isGateway,
            latencyMs = latency
        )
    }

    private fun readArpTable(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val br = BufferedReader(FileReader("/proc/net/arp"))
            var line: String?
            // Skip header
            br.readLine()
            while (br.readLine().also { line = it } != null) {
                val tokens = line?.trim()?.split(Regex("\\s+")) ?: continue
                if (tokens.size >= 4) {
                    val ip = tokens[0]
                    val mac = tokens[3]
                    if (mac != "00:00:00:00:00:00") {
                        map[ip] = mac.uppercase()
                    }
                }
            }
            br.close()
        } catch (_: Exception) {}
        return map
    }

    private fun resolveVendor(mac: String, ports: List<Int>): String {
        val clean = mac.replace(":", "").replace("-", "").uppercase()
        if (ports.contains(8291)) return "MikroTik (RouterOS Winbox)"
        if (ports.contains(554) || ports.contains(37777)) return "CCTV / IP Camera"
        if (ports.contains(8000)) return "Hikvision Device"

        if (clean.length >= 6) {
            val prefix = clean.substring(0, 6)
            when (prefix) {
                // TP-Link
                "000AEB", "001478", "0019E0", "002127", "0023CD", "002586", "10FEED", "14CC20", "1C3BF3", "30B5C2", "50C7BF", "704F57", "984827", "AC84C6", "C025E9", "E4C32A" -> return "TP-Link Technologies"
                // Tenda
                "00B00C", "C83A35", "502B73", "0495E6", "CC3429", "0840F3" -> return "Tenda Technology"
                // MikroTik
                "000C42", "488F5A", "64D154", "CC2DE0", "B869F4", "789A18", "2C56DC", "18FD74" -> return "MikroTik RouterBOARD"
                // Huawei
                "001E10", "00259E", "04FE8D", "0819A6", "0C37DC", "101B54", "200889", "4846FB", "7054F5", "BC7670", "F4C479" -> return "Huawei Technologies"
                // ZTE
                "0019C6", "001E73", "002293", "208986", "34E0CF", "681AEC", "9C3DDF", "D87495" -> return "ZTE Corporation"
                // D-Link
                "00055D", "000D88", "001195", "0015E9", "00179A", "001B11", "14D64D", "28107B", "78321B", "B0C554" -> return "D-Link International"
                // Xiaomi
                "04CF4B", "0C1DAF", "185936", "286C07", "348062", "50642B", "640980", "7811DC", "ACF7F3" -> return "Xiaomi Communications"
                // Apple
                "000393", "000A27", "0010FA", "0017F2", "001E52", "040CCE", "18AF61", "28CFE9", "380F4A", "406C8F", "7C04D0", "ACBC32", "BC6C21", "F01898" -> return "Apple Inc."
                // Samsung
                "0007AB", "001247", "001599", "001A8A", "08373D", "14B484", "244B03", "342387", "400E85", "508569", "78471D", "8425DB", "B857D8" -> return "Samsung Electronics"
                // Intel
                "0002B3", "000347", "000423", "000E0C", "001302", "001500", "001B21", "3413E8", "3C970E", "5C514F", "808600" -> return "Intel Corporation"
                // Espressif (IoT)
                "240AC4", "246F28", "2C3AE8", "30AEA4", "483FDA", "5C02BF", "68C63A", "840D8E", "94B97E", "A4CF12", "B4E62D", "DC4F22" -> return "Espressif IoT Device"
                // Dahua
                "3C15FB", "4C11BF", "E0508B", "9002A9" -> return "Dahua Technology (CCTV)"
                // Hikvision
                "4419B6", "5803FB", "BC3B38", "C42F90", "C8028F" -> return "Hikvision Digital (CCTV)"
            }
        }

        if (ports.contains(80) || ports.contains(443)) return "Network Router / Web Device"
        if (ports.contains(22)) return "Linux / SSH Server"
        return "Network Device"
    }
}
