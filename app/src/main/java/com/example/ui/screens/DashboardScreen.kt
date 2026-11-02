package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.network.NetworkUtils
import com.example.network.WifiConnectionInfo
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

data class QuickToolItem(
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val badge: String,
    val color: Color,
    val destination: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    var wifiInfo by remember { mutableStateOf(NetworkUtils.getConnectionInfo(context)) }
    var searchQuery by remember { mutableStateOf("") }

    val quickTools = listOf(
        QuickToolItem(
            titleBn = "রাউটার সেটআপ গাইড",
            titleEn = "Router Config Guides",
            subtitleBn = "TP-Link, Tenda, MikroTik, Huawei...",
            subtitleEn = "TP-Link, Tenda, MikroTik, Huawei...",
            icon = Icons.Default.Router,
            badge = "7 Brands",
            color = TechCyanPrimary,
            destination = "routers"
        ),
        QuickToolItem(
            titleBn = "মাইক্রোটিক জেনারেটর",
            titleEn = "MikroTik Generator",
            subtitleBn = "PPPoE, Hotspot, NAT, Firewall, Queue",
            subtitleEn = "PPPoE, Hotspot, NAT, Firewall, Queue",
            icon = Icons.Default.Terminal,
            badge = "RouterOS",
            color = TechBlueSecondary,
            destination = "mikrotik"
        ),
        QuickToolItem(
            titleBn = "নেটওয়ার্ক ডায়াগনস্টিক",
            titleEn = "Diagnostics & Speed",
            subtitleBn = "Speed Test, Ping, DNS, Traceroute",
            subtitleEn = "Speed Test, Ping, DNS, Traceroute",
            icon = Icons.Default.Speed,
            badge = "Live Tests",
            color = TechGreen,
            destination = "diagnostics"
        ),
        QuickToolItem(
            titleBn = "ল্যান ডিভাইস স্ক্যানার",
            titleEn = "LAN Device Scanner",
            subtitleBn = "লাইভ আইপি, ম্যাক এড্রেস ও ভেন্ডর",
            subtitleEn = "Real IP, MAC address & vendor detection",
            icon = Icons.Default.Devices,
            badge = "ARP Scan",
            color = TechAmber,
            destination = "lan_scanner"
        ),
        QuickToolItem(
            titleBn = "আইএসপি ট্রাবলশুটিং",
            titleEn = "ISP Troubleshooting",
            subtitleBn = "লাল LOS বাতি, PON ও 651 এরর ফিক্স",
            subtitleEn = "Fix Red LOS, PON & 651/691 errors",
            icon = Icons.Default.ReportProblem,
            badge = "Step-by-Step",
            color = TechRed,
            destination = "troubleshooting"
        ),
        QuickToolItem(
            titleBn = "হার্ডওয়্যার ও রিকভারি",
            titleEn = "Repair & Recovery",
            subtitleBn = "TFTP Unbrick, Netinstall, ক্যাপাসিটর",
            subtitleEn = "TFTP Unbrick, Netinstall, Capacitors",
            icon = Icons.Default.Build,
            badge = "Technician",
            color = TechOrange,
            destination = "repair"
        ),
        QuickToolItem(
            titleBn = "সাবনেট ও টেক টুলস",
            titleEn = "Tech Tools & Subnet",
            subtitleBn = "Subnet ক্যালকুলেটর, RJ45 ও ডিফল্ট পাস",
            subtitleEn = "Subnet Calc, RJ45 Wiring, Passwords",
            icon = Icons.Default.Calculate,
            badge = "Utility",
            color = TechCyanPrimary,
            destination = "tools"
        ),
        QuickToolItem(
            titleBn = "সংরক্ষিত ফেভারিট",
            titleEn = "Saved Favorites",
            subtitleBn = "বুকমার্ক করা গাইড ও কমান্ড",
            subtitleEn = "Bookmarked guides & commands",
            icon = Icons.Default.Star,
            badge = "${FavoritesManager.favorites.size} Items",
            color = TechAmber,
            destination = "favorites"
        )
    )

    // Filtered search results
    val searchResults = remember(searchQuery) {
        if (searchQuery.trim().isEmpty()) emptyList()
        else {
            val q = searchQuery.trim().lowercase()
            val list = mutableListOf<Triple<String, String, String>>() // Title, Subtitle, Destination
            // Search guides
            RouterGuidesData.guides.filter {
                it.brand.lowercase().contains(q) || it.title().lowercase().contains(q) || it.popularModels.lowercase().contains(q)
            }.forEach {
                list.add(Triple(it.brand + ": " + it.title(), it.defaultIp, "routers"))
            }
            // Search troubleshooting
            TroubleshootingData.issues.filter {
                it.title().lowercase().contains(q) || it.symptoms().lowercase().contains(q)
            }.forEach {
                list.add(Triple(it.title(), it.severity, "troubleshooting"))
            }
            // Search repair
            RepairGuidesData.guides.filter {
                it.title().lowercase().contains(q) || it.description().lowercase().contains(q)
            }.forEach {
                list.add(Triple(it.title(), it.type, "repair"))
            }
            // Search tools
            TechToolsData.defaultLogins.filter {
                it.brand.lowercase().contains(q) || it.model.lowercase().contains(q) || it.ip.contains(q)
            }.take(4).forEach {
                list.add(Triple("${it.brand} ${it.model}", "IP: ${it.ip} | User: ${it.username}", "tools"))
            }
            list
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Service Badge & Branding Header
        item {
            ServiceBadgeBanner(context = context, onNavigateAbout = { onNavigate("about") })
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = str("রাউটার, এরর বা টুল খুঁজুন...", "Search routers, errors or tools..."),
                        color = TechTextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TechCyanPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TechTextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyanPrimary,
                    unfocusedBorderColor = TechBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )
        }

        // Live Search Results Dropdown
        if (searchResults.isNotEmpty()) {
            item {
                TechCard(
                    borderColor = TechCyanPrimary.copy(alpha = 0.5f),
                    containerColor = TechSurfaceVariant
                ) {
                    Text(
                        text = str("সার্চের ফলাফল (${searchResults.size})", "Search Results (${searchResults.size})"),
                        fontWeight = FontWeight.Bold,
                        color = TechCyanPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    searchResults.take(6).forEach { res ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(res.third) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = res.first, color = TechTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = res.second, color = TechTextSecondary, fontSize = 11.sp)
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TechCyanPrimary, modifier = Modifier.size(16.dp))
                        }
                        HorizontalDivider(color = TechBorder)
                    }
                }
            }
        }

        // Network Live Status Card
        item {
            NetworkStatusCard(
                info = wifiInfo,
                onRefresh = { wifiInfo = NetworkUtils.getConnectionInfo(context) },
                onOpenGateway = {
                    try {
                        val url = if (wifiInfo.gatewayIp.startsWith("http")) wifiInfo.gatewayIp else "http://${wifiInfo.gatewayIp}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                }
            )
        }

        // Section Title: Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = str("প্রয়োজনীয় নেটওয়ার্ক টুলস", "Essential Network Tools"),
                    color = TechTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = str("সব দেখুন", "View All"),
                    color = TechCyanPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigate("tools") }
                )
            }
        }

        // Grid of 8 Main Features
        items(quickTools.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { tool ->
                    Box(modifier = Modifier.weight(1f)) {
                        ToolGridCard(
                            tool = tool,
                            onClick = { onNavigate(tool.destination) }
                        )
                    }
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Technician Contact & Emergency Hotline Card
        item {
            TechnicianHotlineCard(context = context, onNavigateAbout = { onNavigate("about") })
        }
    }
}

@Composable
fun ServiceBadgeBanner(context: Context, onNavigateAbout: () -> Unit) {
    TechCard(
        borderColor = TechCyanPrimary.copy(alpha = 0.35f),
        containerColor = TechSurfaceVariant,
        onClick = onNavigateAbout
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Official Service Badge Image
            Image(
                painter = painterResource(id = R.drawable.router_doctor_logo),
                contentDescription = "Router Doctor BD Badge",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, TechCyanPrimary, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Router Doctor BD",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TechTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(text = "PRO", color = TechAmber)
                }

                Text(
                    text = "Kabir Apps • Kabir Molla",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TechCyanPrimary
                )

                Text(
                    text = str(
                        "Stay Connected... Stay Online...",
                        "Stay Connected... Stay Online..."
                    ),
                    fontSize = 11.sp,
                    color = TechTextSecondary,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Details",
                tint = TechCyanPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun NetworkStatusCard(
    info: WifiConnectionInfo,
    onRefresh: () -> Unit,
    onOpenGateway: () -> Unit
) {
    TechCard(
        borderColor = if (info.isConnected) TechCyanPrimary.copy(alpha = 0.3f) else TechRed.copy(alpha = 0.3f),
        containerColor = TechSurface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (info.isConnected) TechGreen else TechRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (info.isConnected) str("নেটওয়ার্ক সংযুক্ত", "Network Connected") else str("সংযুক্ত নয়", "Disconnected"),
                    color = if (info.isConnected) TechGreen else TechRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = TechCyanPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = info.ssid,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TechTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = str("ডিভাইস আইপি", "Device IP"), color = TechTextMuted, fontSize = 11.sp)
                Text(text = info.localIp, color = TechTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Column {
                Text(text = str("গেটওয়ে / রাউটার আইপি", "Gateway / Router IP"), color = TechTextMuted, fontSize = 11.sp)
                Text(text = info.gatewayIp, color = TechCyanPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = str("লিংক স্পিড", "Link Speed"), color = TechTextMuted, fontSize = 11.sp)
                Text(text = if (info.linkSpeedMbps > 0) "${info.linkSpeedMbps} Mbps" else "N/A", color = TechTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onOpenGateway,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = TechSurfaceVariant,
                contentColor = TechCyanPrimary
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = str("রাউটার অ্যাডমিন পেজ ওপেন করুন (${info.gatewayIp})", "Open Router Admin (${info.gatewayIp})"),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ToolGridCard(
    tool: QuickToolItem,
    onClick: () -> Unit
) {
    TechCard(
        borderColor = TechBorder,
        containerColor = TechSurfaceVariant,
        onClick = onClick,
        modifier = Modifier.height(130.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tool.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = tool.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            StatusBadge(text = tool.badge, color = tool.color)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = str(tool.titleBn, tool.titleEn),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TechTextPrimary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = str(tool.subtitleBn, tool.subtitleEn),
            fontSize = 10.sp,
            color = TechTextSecondary,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun TechnicianHotlineCard(context: Context, onNavigateAbout: () -> Unit) {
    TechCard(
        borderColor = TechAmber.copy(alpha = 0.4f),
        containerColor = TechSurfaceVariant
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(TechAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = null,
                    tint = TechAmber,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = str("রাউটার বা নেটওয়ার্ক সমস্যা? আমরা পাশে আছি", "Need Router or Network Help?"),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechTextPrimary
                )
                Text(
                    text = str("কবির মোল্লা • কোটালীপাড়া, গোপালগঞ্জ", "Kabir Molla • Kotalipara, Gopalganj"),
                    fontSize = 11.sp,
                    color = TechTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01876390839"))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TechGreen),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "01876390839", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/8801876390839"))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TechSurfaceLight),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = TechCyanPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "WhatsApp", color = TechCyanPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
