package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.CodeBlockView
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MikrotikGeneratorScreen() {
    val context = LocalContext.current
    var selectedModule by remember { mutableStateOf(MikrotikModule.PPPOE) }

    // Parameters
    var wanInterface by remember { mutableStateOf("ether1") }
    var lanInterface by remember { mutableStateOf("bridge-LAN") }
    var lanSubnet by remember { mutableStateOf("192.168.88.0/24") }
    var gatewayIp by remember { mutableStateOf("192.168.88.1") }
    var poolRange by remember { mutableStateOf("192.168.88.10-192.168.88.254") }
    var dnsServers by remember { mutableStateOf("8.8.8.8,1.1.1.1") }

    // PPPoE Specific
    var pppoeUser by remember { mutableStateOf("bd_client01") }
    var pppoePass by remember { mutableStateOf("pass@123") }
    var isPppoeServer by remember { mutableStateOf(false) }

    // Hotspot Specific
    var hotspotDnsName by remember { mutableStateOf("wifi.login") }
    var hotspotRateLimit by remember { mutableStateOf("5M/10M") }

    // NAT / Port Forwarding Specific
    var forwardDstPort by remember { mutableStateOf("8000") }
    var forwardToIp by remember { mutableStateOf("192.168.88.200") }
    var forwardToPort by remember { mutableStateOf("8000") }

    // Queue Specific
    var targetIp by remember { mutableStateOf("192.168.88.50/32") }
    var maxLimitUpload by remember { mutableStateOf("10M") }
    var maxLimitDownload by remember { mutableStateOf("20M") }

    // Generate script dynamically
    val generatedScript = remember(
        selectedModule, wanInterface, lanInterface, lanSubnet, gatewayIp, poolRange, dnsServers,
        pppoeUser, pppoePass, isPppoeServer, hotspotDnsName, hotspotRateLimit,
        forwardDstPort, forwardToIp, forwardToPort, targetIp, maxLimitUpload, maxLimitDownload
    ) {
        val params = MikrotikParam(
            wanInterface = wanInterface,
            lanInterface = lanInterface,
            lanSubnet = lanSubnet,
            gatewayIp = gatewayIp,
            poolRange = poolRange,
            dnsServers = dnsServers,
            pppoeUser = pppoeUser,
            pppoePass = pppoePass,
            isPppoeServer = isPppoeServer,
            hotspotDnsName = hotspotDnsName,
            hotspotRateLimit = hotspotRateLimit,
            forwardDstPort = forwardDstPort,
            forwardToIp = forwardToIp,
            forwardToPort = forwardToPort,
            targetIp = targetIp,
            maxLimitUpload = maxLimitUpload,
            maxLimitDownload = maxLimitDownload
        )
        MikrotikCommandGenerator.generate(selectedModule, params)
    }

    val scriptId = "mikrotik_script_${selectedModule.name}"
    val isFavorited = FavoritesManager.isFavorite(scriptId)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Module selector chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MikrotikModule.values()) { module ->
                    val isSelected = selectedModule == module
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedModule = module },
                        label = {
                            Text(
                                text = str(module.titleBn, module.titleEn),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanPrimary,
                            selectedLabelColor = TechDarkBg,
                            containerColor = TechSurface,
                            labelColor = TechTextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) TechCyanPrimary else TechBorder
                        )
                    )
                }
            }
        }

        // Module Input Parameters Card
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("কনফিগারেশন প্যারামিটার", "Configuration Parameters"),
                    color = TechCyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Common Interface fields
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wanInterface,
                        onValueChange = { wanInterface = it },
                        label = { Text("WAN Interface", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = lanInterface,
                        onValueChange = { lanInterface = it },
                        label = { Text("LAN / Bridge", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = fieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Module-specific fields
                when (selectedModule) {
                    MikrotikModule.PPPOE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !isPppoeServer,
                                onClick = { isPppoeServer = false },
                                colors = RadioButtonDefaults.colors(selectedColor = TechCyanPrimary)
                            )
                            Text(text = str("PPPoE Client (ডায়াল-আউট)", "PPPoE Client (Dial-out)"), color = TechTextPrimary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            RadioButton(
                                selected = isPppoeServer,
                                onClick = { isPppoeServer = true },
                                colors = RadioButtonDefaults.colors(selectedColor = TechCyanPrimary)
                            )
                            Text(text = str("PPPoE Server (আইএসপি)", "PPPoE Server (ISP)"), color = TechTextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pppoeUser,
                                onValueChange = { pppoeUser = it },
                                label = { Text("PPPoE Username", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = pppoePass,
                                onValueChange = { pppoePass = it },
                                label = { Text("Password", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                        }
                    }

                    MikrotikModule.HOTSPOT -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hotspotDnsName,
                                onValueChange = { hotspotDnsName = it },
                                label = { Text("DNS Login Hostname", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = hotspotRateLimit,
                                onValueChange = { hotspotRateLimit = it },
                                label = { Text("Rate Limit (Up/Down)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                        }
                    }

                    MikrotikModule.DHCP -> {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gatewayIp,
                                onValueChange = { gatewayIp = it },
                                label = { Text("Gateway IP", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = lanSubnet,
                                onValueChange = { lanSubnet = it },
                                label = { Text("Subnet (CIDR)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = poolRange,
                            onValueChange = { poolRange = it },
                            label = { Text("DHCP IP Pool Range", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = fieldColors()
                        )
                    }

                    MikrotikModule.NAT -> {
                        Text(
                            text = str("CCTV DVR / NVR পোর্ট ফরওয়ার্ডিং (DST-NAT):", "Port Forwarding (CCTV / Server):"),
                            fontSize = 11.sp,
                            color = TechAmber,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = forwardDstPort,
                                onValueChange = { forwardDstPort = it },
                                label = { Text("WAN Port", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = forwardToIp,
                                onValueChange = { forwardToIp = it },
                                label = { Text("DVR/Server IP", fontSize = 10.sp) },
                                modifier = Modifier.weight(1.4f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = forwardToPort,
                                onValueChange = { forwardToPort = it },
                                label = { Text("To Port", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                        }
                    }

                    MikrotikModule.FIREWALL -> {
                        Text(
                            text = str(
                                "ডিফল্টভাবে FastTrack অ্যাক্টিভেশন, ইনভ্যালিড প্যাকেট ড্রপ এবং বহিরাগত WAN DNS UDP/TCP পোর্ট ৫৩ অ্যাটাক প্রতিরোধ করার ফিল্টার অন্তর্ভুক্ত থাকবে।",
                                "Includes FastTrack, Invalid packet drop, and blocks open WAN port 53 DNS amplification attacks."
                            ),
                            fontSize = 11.sp,
                            color = TechTextSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    MikrotikModule.QUEUE -> {
                        OutlinedTextField(
                            value = targetIp,
                            onValueChange = { targetIp = it },
                            label = { Text("Target IP / Subnet", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = fieldColors()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = maxLimitUpload,
                                onValueChange = { maxLimitUpload = it },
                                label = { Text("Max Upload (e.g. 10M)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                            OutlinedTextField(
                                value = maxLimitDownload,
                                onValueChange = { maxLimitDownload = it },
                                label = { Text("Max Download (e.g. 20M)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = fieldColors()
                            )
                        }
                    }
                }
            }
        }

        // Generated Code View
        item {
            CodeBlockView(
                code = generatedScript,
                isFavorited = isFavorited,
                onSaveFavorite = {
                    val item = FavoriteItem(
                        id = scriptId,
                        titleBn = "মাইক্রোটিক: ${selectedModule.titleBn}",
                        titleEn = "MikroTik: ${selectedModule.titleEn}",
                        categoryBn = "RouterOS স্ক্রিপ্ট",
                        categoryEn = "RouterOS Script",
                        content = generatedScript
                    )
                    FavoritesManager.toggleFavorite(context, item)
                }
            )
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TechCyanPrimary,
    unfocusedBorderColor = TechBorder,
    focusedContainerColor = TechSurfaceVariant,
    unfocusedContainerColor = TechSurfaceVariant,
    focusedTextColor = TechTextPrimary,
    unfocusedTextColor = TechTextPrimary
)
