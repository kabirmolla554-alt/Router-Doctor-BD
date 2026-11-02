package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.str
import com.example.network.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class DiagTab(val titleBn: String, val titleEn: String) {
    SPEED("স্পিড টেস্ট", "Speed Test"),
    PING("পিং টেস্ট", "Ping Test"),
    DNS("ডিএনএস লুকআপ", "DNS Lookup"),
    TRACE("ট্রেসরুট", "Traceroute")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen() {
    var selectedTab by remember { mutableStateOf(DiagTab.SPEED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg)
    ) {
        // Tab row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = TechSurface,
            contentColor = TechCyanPrimary,
            divider = { HorizontalDivider(color = TechBorder) }
        ) {
            DiagTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = str(tab.titleBn, tab.titleEn),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TechCyanPrimary else TechTextSecondary
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            DiagTab.SPEED -> SpeedTestTab()
            DiagTab.PING -> PingTestTab()
            DiagTab.DNS -> DnsLookupTab()
            DiagTab.TRACE -> TracerouteTab()
        }
    }
}

@Composable
fun SpeedTestTab() {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(SpeedTestState()) }
    var isRunning by remember { mutableStateOf(false) }

    val animatedSpeed by animateFloatAsState(
        targetValue = state.currentSpeedMbps,
        animationSpec = tween(durationMillis = 250),
        label = "SpeedDial"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Speedometer Dial
        item {
            TechCard(containerColor = TechSurface) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 14.dp.toPx()
                            val arcRadius = (size.minDimension - strokeWidth) / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            // Background Track Arc (180 to 360 + 0) = 240 degrees
                            drawArc(
                                color = Color(0xFF16233B),
                                startAngle = 150f,
                                sweepAngle = 240f,
                                useCenter = false,
                                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                                size = Size(arcRadius * 2, arcRadius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Active Speed Arc (proportional to speed, max 100 Mbps scale)
                            val fraction = (animatedSpeed / 100f).coerceIn(0f, 1f)
                            drawArc(
                                color = TechCyanPrimary,
                                startAngle = 150f,
                                sweepAngle = 240f * fraction,
                                useCenter = false,
                                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                                size = Size(arcRadius * 2, arcRadius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Needle Line
                            val needleAngleDeg = 150f + (240f * fraction)
                            val needleAngleRad = Math.toRadians(needleAngleDeg.toDouble())
                            val needleLength = arcRadius - 10.dp.toPx()
                            val needleEnd = Offset(
                                (center.x + needleLength * cos(needleAngleRad)).toFloat(),
                                (center.y + needleLength * sin(needleAngleRad)).toFloat()
                            )
                            drawLine(
                                color = TechAmber,
                                start = center,
                                end = needleEnd,
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawCircle(color = TechCyanPrimary, radius = 6.dp.toPx(), center = center)
                        }

                        // Center Speed Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format("%.1f", animatedSpeed),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TechTextPrimary
                            )
                            Text(
                                text = "Mbps",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechCyanPrimary
                            )
                            Text(
                                text = when (state.phase) {
                                    SpeedTestPhase.IDLE -> str("রেডি", "Ready")
                                    SpeedTestPhase.MEASURING_PING -> str("পিং টেস্ট হচ্ছে...", "Testing Ping...")
                                    SpeedTestPhase.DOWNLOADING -> str("ডাউনলোড হচ্ছে...", "Downloading...")
                                    SpeedTestPhase.FINISHED -> str("সম্পন্ন", "Complete")
                                    SpeedTestPhase.ERROR -> str("এরর", "Error")
                                },
                                fontSize = 11.sp,
                                color = TechTextMuted
                            )
                        }
                    }

                    if (state.phase == SpeedTestPhase.DOWNLOADING || state.phase == SpeedTestPhase.MEASURING_PING) {
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TechCyanPrimary,
                            trackColor = TechBorder
                        )
                    }
                }
            }
        }

        // Stats Row (Ping, Jitter, Max, Downloaded)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatBox(
                    label = str("পিং (Ping)", "Ping"),
                    value = if (state.pingMs > 0) "${String.format("%.1f", state.pingMs)} ms" else "--",
                    color = TechGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = str("জিটার (Jitter)", "Jitter"),
                    value = if (state.jitterMs > 0) "${String.format("%.1f", state.jitterMs)} ms" else "--",
                    color = TechAmber,
                    modifier = Modifier.weight(1f)
                )
                StatBox(
                    label = str("সর্বোচ্চ (Peak)", "Peak"),
                    value = if (state.maxSpeedMbps > 0) "${String.format("%.1f", state.maxSpeedMbps)} M" else "--",
                    color = TechCyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Start / Retest Button
        item {
            Button(
                onClick = {
                    if (!isRunning) {
                        isRunning = true
                        coroutineScope.launch {
                            SpeedTestExecutor.runSpeedTest().collect { s ->
                                state = s
                                if (s.phase == SpeedTestPhase.FINISHED || s.phase == SpeedTestPhase.ERROR) {
                                    isRunning = false
                                }
                            }
                        }
                    }
                },
                enabled = !isRunning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TechDarkBg, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = str("টেস্ট চলছে...", "Testing..."), color = TechDarkBg, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = TechDarkBg)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.phase == SpeedTestPhase.FINISHED) str("পুনরায় স্পিড টেস্ট করুন", "Run Speed Test Again") else str("রিয়েল স্পিড টেস্ট শুরু করুন", "Start Real Speed Test"),
                        color = TechDarkBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            item {
                Text(text = state.errorMessage ?: "", color = TechRed, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PingTestTab() {
    val coroutineScope = rememberCoroutineScope()
    var targetHost by remember { mutableStateOf("8.8.8.8") }
    var pingCount by remember { mutableStateOf(5) }
    var packets by remember { mutableStateOf<List<PingPacket>>(emptyList()) }
    var summary by remember { mutableStateOf<PingSummary?>(null) }
    var isPinging by remember { mutableStateOf(false) }

    val presets = listOf("8.8.8.8", "1.1.1.1", "192.168.0.1", "google.com", "bdix.net")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("টার্গেট হোস্ট বা আইপি", "Target Host or IP"),
                    color = TechCyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetHost,
                    onValueChange = { targetHost = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TechCyanPrimary,
                        unfocusedBorderColor = TechBorder,
                        focusedContainerColor = TechSurfaceVariant,
                        unfocusedContainerColor = TechSurfaceVariant,
                        focusedTextColor = TechTextPrimary,
                        unfocusedTextColor = TechTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Presets
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { preset ->
                        FilterChip(
                            selected = targetHost == preset,
                            onClick = { targetHost = preset },
                            label = { Text(text = preset, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TechCyanPrimary,
                                selectedLabelColor = TechDarkBg,
                                containerColor = TechSurfaceVariant,
                                labelColor = TechTextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (!isPinging) {
                            isPinging = true
                            packets = emptyList()
                            coroutineScope.launch {
                                PingExecutor.executePing(targetHost, pingCount).collect { (pkts, smry) ->
                                    packets = pkts
                                    summary = smry
                                    if (!smry.isRunning) {
                                        isPinging = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isPinging,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isPinging) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TechDarkBg, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str("পিং পাঠানো হচ্ছে...", "Pinging..."), color = TechDarkBg, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.NetworkPing, contentDescription = null, tint = TechDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str("পিং টেস্ট চালান", "Send Ping"), color = TechDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Card
        summary?.let { s ->
            item {
                TechCard(borderColor = if (s.lossPercentage > 0) TechAmber else TechGreen) {
                    Text(
                        text = str("পিং সারাংশ (${s.host} → ${s.ip})", "Ping Summary (${s.host})"),
                        color = TechTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = str("প্যাকেট", "Packets"), color = TechTextMuted, fontSize = 11.sp)
                            Text(text = "${s.packetsReceived}/${s.packetsSent} (${s.lossPercentage.toInt()}% Loss)", color = if (s.lossPercentage > 0) TechAmber else TechGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = str("মিনিমাম", "Min"), color = TechTextMuted, fontSize = 11.sp)
                            Text(text = "${String.format("%.1f", s.minMs)} ms", color = TechTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text(text = str("গড় (Avg)", "Avg"), color = TechTextMuted, fontSize = 11.sp)
                            Text(text = "${String.format("%.1f", s.avgMs)} ms", color = TechCyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = str("সর্বোচ্চ", "Max"), color = TechTextMuted, fontSize = 11.sp)
                            Text(text = "${String.format("%.1f", s.maxMs)} ms", color = TechTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Packet Log Stream
        items(packets) { pkt ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pkt.rawText,
                        color = if (pkt.timeMs > 0) TechTextPrimary else TechRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                    if (pkt.timeMs > 0) {
                        StatusBadge(
                            text = "${String.format("%.1f", pkt.timeMs)}ms",
                            color = if (pkt.timeMs < 40) TechGreen else if (pkt.timeMs < 120) TechAmber else TechRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DnsLookupTab() {
    val coroutineScope = rememberCoroutineScope()
    var domain by remember { mutableStateOf("google.com") }
    var result by remember { mutableStateOf<DnsLookupResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("ডোমেন ডিএনএস সমাধান (DNS Lookup)", "Resolve Domain DNS Records"),
                    color = TechCyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = domain,
                    onValueChange = { domain = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TechCyanPrimary,
                        unfocusedBorderColor = TechBorder,
                        focusedContainerColor = TechSurfaceVariant,
                        unfocusedContainerColor = TechSurfaceVariant,
                        focusedTextColor = TechTextPrimary,
                        unfocusedTextColor = TechTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            result = DnsLookupExecutor.lookup(domain)
                            isLoading = false
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TechDarkBg)
                    } else {
                        Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = TechDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str("ডিএনএস রেকর্ড খুঁজুন", "Lookup DNS"), color = TechDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        result?.let { r ->
            item {
                TechCard(borderColor = if (r.isSuccess) TechGreen else TechRed) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = r.queryDomain, fontWeight = FontWeight.Bold, color = TechTextPrimary, fontSize = 14.sp)
                        StatusBadge(
                            text = if (r.isSuccess) "${r.responseTimeMs} ms" else "Failed",
                            color = if (r.isSuccess) TechGreen else TechRed
                        )
                    }

                    if (r.records.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        r.records.forEach { rec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = rec.type, color = TechCyanPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = rec.address, color = TechTextPrimary, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = TechBorder)
                        }
                    } else if (r.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = r.errorMessage ?: "", color = TechRed, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TracerouteTab() {
    val coroutineScope = rememberCoroutineScope()
    var targetHost by remember { mutableStateOf("1.1.1.1") }
    var hops by remember { mutableStateOf<List<TracerouteHop>>(emptyList()) }
    var isTracing by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("ট্রেসরুট (ICMP Hop Tracking)", "Traceroute (ICMP Hop Tracking)"),
                    color = TechCyanPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetHost,
                    onValueChange = { targetHost = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TechCyanPrimary,
                        unfocusedBorderColor = TechBorder,
                        focusedContainerColor = TechSurfaceVariant,
                        unfocusedContainerColor = TechSurfaceVariant,
                        focusedTextColor = TechTextPrimary,
                        unfocusedTextColor = TechTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (!isTracing) {
                            isTracing = true
                            hops = emptyList()
                            coroutineScope.launch {
                                TracerouteExecutor.trace(targetHost).collect { list ->
                                    hops = list
                                }
                                isTracing = false
                            }
                        }
                    },
                    enabled = !isTracing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isTracing) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TechDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str("হপ ট্রেস করা হচ্ছে...", "Tracing Hops..."), color = TechDarkBg, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.AltRoute, contentDescription = null, tint = TechDarkBg)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = str("ট্রেসরুট শুরু করুন", "Start Traceroute"), color = TechDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(hops) { hop ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TechSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (hop.isDestination) TechGreen else TechBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (hop.isDestination) TechGreen else TechCyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hop.hopNumber}",
                                color = if (hop.isDestination) TechDarkBg else TechCyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (hop.isTimedOut) "*" else hop.ip,
                                color = if (hop.isTimedOut) TechTextMuted else TechTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (hop.hostname != "*" && hop.hostname != hop.ip) {
                                Text(text = hop.hostname, color = TechTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    if (!hop.isTimedOut) {
                        StatusBadge(
                            text = "${String.format("%.1f", hop.rttMs)} ms",
                            color = if (hop.isDestination) TechGreen else TechCyanPrimary
                        )
                    } else {
                        StatusBadge(text = "Timeout", color = TechTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = TechSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = TechTextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
