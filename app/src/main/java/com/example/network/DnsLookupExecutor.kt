package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress

data class DnsRecord(
    val type: String, // A or AAAA
    val address: String,
    val host: String,
    val latencyMs: Long
)

data class DnsLookupResult(
    val queryDomain: String,
    val isSuccess: Boolean,
    val responseTimeMs: Long,
    val records: List<DnsRecord>,
    val errorMessage: String? = null
)

object DnsLookupExecutor {

    suspend fun lookup(domain: String): DnsLookupResult = withContext(Dispatchers.IO) {
        val clean = domain.trim().replace("https://", "").replace("http://", "").substringBefore("/")
        val start = System.currentTimeMillis()
        try {
            val addresses = InetAddress.getAllByName(clean)
            val elapsed = System.currentTimeMillis() - start

            val records = addresses.map { addr ->
                val type = when (addr) {
                    is Inet4Address -> "A (IPv4)"
                    is Inet6Address -> "AAAA (IPv6)"
                    else -> "IP"
                }
                DnsRecord(
                    type = type,
                    address = addr.hostAddress ?: "",
                    host = addr.canonicalHostName,
                    latencyMs = elapsed
                )
            }

            DnsLookupResult(
                queryDomain = clean,
                isSuccess = records.isNotEmpty(),
                responseTimeMs = elapsed,
                records = records
            )
        } catch (e: Exception) {
            DnsLookupResult(
                queryDomain = clean,
                isSuccess = false,
                responseTimeMs = System.currentTimeMillis() - start,
                records = emptyList(),
                errorMessage = e.message ?: "Domain not found / DNS lookup failed"
            )
        }
    }
}
