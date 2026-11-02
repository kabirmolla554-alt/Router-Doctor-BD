package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.str
import com.example.network.LanDevice
import com.example.network.LanScanner
import com.example.network.NetworkUtils
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LanScannerScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val connInfo = remember { NetworkUtils.getConnectionInfo(context) }

    var devices by remember { mutableStateOf<List<LanDevice>>(emptyList()) }
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Scanner Header Card
        item {
            TechCard(containerColor = TechSurface) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = str("লোকাল এরিয়া নেটওয়ার্ক (LAN) স্ক্যানার", "Local Area Network (LAN) Scanner"),
                            color = TechCyanPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${str("সাবনেট", "Subnet")}: ${connInfo.gatewayIp.substringBeforeLast(".")}.0/24",
                            color = TechTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    StatusBadge(
                        text = "${devices.size} ${str("ডিভাইস", "Devices")}",
                        color = if (devices.isNotEmpty()) TechGreen else TechTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isScanning) {
                    Column {
                        LinearProgressIndicator(
                            progress = { scanProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TechCyanPrimary,
                            trackColor = TechBorder
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${str("আইপি রেঞ্জ স্ক্যান হচ্ছে...", "Scanning IP range...")} ${(scanProgress * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = TechTextMuted
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isScanning = true
                            scanProgress = 0f
                            coroutineScope.launch {
                                LanScanner.scanSubnet(connInfo.gatewayIp, connInfo.localIp).collect { (list, prog) ->
                                    devices = list
                                    scanProgress = prog
                                    if (prog >= 1.0f) {
                                        isScanning = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = TechDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (devices.isEmpty()) str("ল্যান ডিভাইস স্ক্যান শুরু করুন", "Start LAN Scan") else str("পুনরায় স্ক্যান করুন", "Scan Subnet Again"),
                            color = TechDarkBg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // List of Discovered Devices
        if (devices.isEmpty() && !isScanning) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DevicesOther,
                            contentDescription = null,
                            tint = TechTextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = str("ওয়াইফাইতে কানেক্টেড সকল রাউটার, ফোন, পিসি ও সিসিটিভি ক্যামেরা খুঁজতে স্ক্যান বাটনে চাপ দিন।", "Press Scan to discover all connected routers, phones, PCs and CCTV cameras on this WiFi."),
                            color = TechTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        items(devices) { device ->
            TechCard(
                borderColor = if (device.isGateway) TechCyanPrimary else TechBorder,
                containerColor = TechSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (device.isGateway) TechCyanPrimary.copy(alpha = 0.2f) else TechSurfaceLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (device.isGateway) Icons.Default.Router else if (device.openPorts.contains(554) || device.openPorts.contains(8000)) Icons.Default.Videocam else Icons.Default.Devices,
                                contentDescription = null,
                                tint = if (device.isGateway) TechCyanPrimary else TechTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = device.ip,
                                    color = TechTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (device.isGateway) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    StatusBadge(text = str("গেটওয়ে", "Gateway"), color = TechCyanPrimary)
                                }
                            }
                            Text(
                                text = device.vendor,
                                color = TechAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (device.latencyMs > 0) {
                        StatusBadge(text = "${device.latencyMs}ms", color = TechGreen)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "MAC Address", color = TechTextMuted, fontSize = 10.sp)
                        Text(
                            text = if (device.mac.isNotEmpty()) device.mac else "ARP Cached",
                            color = TechTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (device.hostname.isNotEmpty()) {
                        Column {
                            Text(text = "Hostname", color = TechTextMuted, fontSize = 10.sp)
                            Text(text = device.hostname, color = TechTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                if (device.openPorts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            device.openPorts.forEach { port ->
                                val portLabel = when (port) {
                                    8291 -> "8291 Winbox"
                                    80 -> "80 HTTP"
                                    443 -> "443 HTTPS"
                                    554 -> "554 RTSP"
                                    8000 -> "8000 Hikvision"
                                    22 -> "22 SSH"
                                    else -> "$port"
                                }
                                StatusBadge(text = portLabel, color = TechCyanPrimary)
                            }
                        }

                        if (device.openPorts.contains(80) || device.openPorts.contains(443)) {
                            TextButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://${device.ip}"))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp), tint = TechCyanPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Web Admin", fontSize = 11.sp, color = TechCyanPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
