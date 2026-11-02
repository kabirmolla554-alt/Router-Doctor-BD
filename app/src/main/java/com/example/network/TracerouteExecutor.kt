package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress

data class TracerouteHop(
    val hopNumber: Int,
    val ip: String,
    val hostname: String,
    val rttMs: Float,
    val isDestination: Boolean = false,
    val isTimedOut: Boolean = false
)

object TracerouteExecutor {

    fun trace(targetHost: String, maxHops: Int = 15): Flow<List<TracerouteHop>> = flow {
        val cleanHost = targetHost.trim().replace("https://", "").replace("http://", "").substringBefore("/")
        val hops = mutableListOf<TracerouteHop>()

        var destIp = cleanHost
        try {
            val inet = InetAddress.getByName(cleanHost)
            destIp = inet.hostAddress ?: cleanHost
        } catch (_: Exception) {}

        for (ttl in 1..maxHops) {
            val start = System.currentTimeMillis()
            var hopIp = "*"
            var hopHost = "*"
            var rtt = 0f
            var isDest = false
            var timedOut = true

            try {
                // ping -c 1 -t <ttl> sends packet with specified TTL
                val process = Runtime.getRuntime().exec("ping -c 1 -t $ttl -W 1 $cleanHost")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    val l = line ?: break
                    // Check for Time to live exceeded or from <ip>
                    val fromRegex = Regex("from ([a-zA-Z0-9.-]+)")
                    val match = fromRegex.find(l)
                    if (match != null) {
                        hopIp = match.groupValues[1].replace(":", "")
                        timedOut = false
                        rtt = (System.currentTimeMillis() - start).toFloat().coerceAtLeast(1f)
                    }

                    if (l.contains("bytes from", ignoreCase = true)) {
                        // Reached destination!
                        val timeRegex = Regex("time=([0-9.]+)")
                        val timeVal = timeRegex.find(l)?.groupValues?.get(1)?.toFloatOrNull()
                        if (timeVal != null) rtt = timeVal
                        isDest = true
                        timedOut = false
                    }
                }
                process.waitFor()
            } catch (_: Exception) {}

            if (!timedOut && hopIp != "*") {
                try {
                    val inet = InetAddress.getByName(hopIp)
                    hopHost = inet.hostName
                } catch (_: Exception) {
                    hopHost = hopIp
                }
            }

            if (hopIp == destIp) {
                isDest = true
            }

            hops.add(
                TracerouteHop(
                    hopNumber = ttl,
                    ip = hopIp,
                    hostname = hopHost,
                    rttMs = rtt,
                    isDestination = isDest,
                    isTimedOut = timedOut
                )
            )

            emit(hops.toList())

            if (isDest) break
        }
    }.flowOn(Dispatchers.IO)
}
