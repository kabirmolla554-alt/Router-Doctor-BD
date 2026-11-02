package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

enum class TechToolTab(val titleBn: String, val titleEn: String) {
    SUBNET("সাবনেট ক্যালকুলেটর", "Subnet Calc"),
    RJ45("RJ45 ক্যাবল কালার", "RJ45 Pinout"),
    LOGINS("ডিফল্ট পাসওয়ার্ড", "Default Logins"),
    FIBER("ফাইবার অপটিক্যাল dBm", "Fiber Optical")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechToolsScreen() {
    var selectedTab by remember { mutableStateOf(TechToolTab.SUBNET) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = TechSurface,
            contentColor = TechCyanPrimary,
            edgePadding = 16.dp,
            divider = { HorizontalDivider(color = TechBorder) }
        ) {
            TechToolTab.values().forEach { tab ->
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
            TechToolTab.SUBNET -> SubnetCalculatorTab()
            TechToolTab.RJ45 -> Rj45PinoutTab()
            TechToolTab.LOGINS -> DefaultLoginsTab()
            TechToolTab.FIBER -> FiberOpticalTab()
        }
    }
}

@Composable
fun SubnetCalculatorTab() {
    var ipInput by remember { mutableStateOf("192.168.1.0") }
    var cidrInput by remember { mutableStateOf(24) }

    val result = remember(ipInput, cidrInput) {
        TechToolsData.calculateSubnet(ipInput, cidrInput)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("IPv4 সাবনেট ও মাস্ক ক্যালকুলেটর", "IPv4 Subnet & Mask Calculator"),
                    color = TechCyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ipInput,
                        onValueChange = { ipInput = it },
                        label = { Text("IP Address", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        colors = inputFieldColors()
                    )
                    OutlinedTextField(
                        value = "/$cidrInput",
                        onValueChange = {
                            val v = it.replace("/", "").toIntOrNull()
                            if (v != null && v in 1..32) cidrInput = v
                        },
                        label = { Text("CIDR", fontSize = 11.sp) },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true,
                        colors = inputFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CIDR Slider
                Text(
                    text = "${str("প্রিফিক্স লেন্থ", "Prefix Length")}: /$cidrInput",
                    color = TechTextSecondary,
                    fontSize = 12.sp
                )
                Slider(
                    value = cidrInput.toFloat(),
                    onValueChange = { cidrInput = it.toInt() },
                    valueRange = 8f..30f,
                    steps = 21,
                    colors = SliderDefaults.colors(
                        thumbColor = TechCyanPrimary,
                        activeTrackColor = TechCyanPrimary,
                        inactiveTrackColor = TechBorder
                    )
                )
            }
        }

        result?.let { r ->
            item {
                TechCard(borderColor = TechCyanPrimary.copy(alpha = 0.5f), containerColor = TechSurfaceVariant) {
                    Text(
                        text = str("সাবনেট হিসাবের ফলাফল:", "Calculation Results:"),
                        color = TechCyanPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SubnetFieldRow(label = str("নেটওয়ার্ক আইপি", "Network IP"), value = r.networkIp, highlight = true)
                    SubnetFieldRow(label = str("সাবনেট মাস্ক", "Subnet Mask"), value = r.netmask)
                    SubnetFieldRow(label = str("ওয়াইল্ডকার্ড মাস্ক", "Wildcard Mask"), value = r.wildcardMask)
                    SubnetFieldRow(label = str("ব্রডকাস্ট আইপি", "Broadcast IP"), value = r.broadcastIp)
                    SubnetFieldRow(label = str("ব্যবহারযোগ্য প্রথম আইপি", "First Usable Host"), value = r.firstUsableIp)
                    SubnetFieldRow(label = str("ব্যবহারযোগ্য শেষ আইপি", "Last Usable Host"), value = r.lastUsableIp)
                    SubnetFieldRow(
                        label = str("মোট ব্যবহারযোগ্য হোস্ট", "Total Usable Hosts"),
                        value = "${r.totalUsableHosts} IPs",
                        highlight = true
                    )
                }
            }
        }
    }
}

@Composable
fun SubnetFieldRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TechTextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = if (highlight) TechCyanPrimary else TechTextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold
        )
    }
    HorizontalDivider(color = TechBorder)
}

@Composable
fun Rj45PinoutTab() {
    var selectedStandard by remember { mutableStateOf("T568B") }
    val pins = if (selectedStandard == "T568B") TechToolsData.rj45T568B else TechToolsData.rj45T568A

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("RJ45 নেটওয়ার্ক কেবল কালার কোড (Crimping)", "RJ45 Color Code Crimping Standards"),
                    color = TechCyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedStandard == "T568B",
                        onClick = { selectedStandard = "T568B" },
                        label = { Text(text = "T-568B (স্ট্যান্ডার্ড / সাধারণ)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanPrimary,
                            selectedLabelColor = TechDarkBg,
                            containerColor = TechSurfaceVariant,
                            labelColor = TechTextPrimary
                        )
                    )
                    FilterChip(
                        selected = selectedStandard == "T568A",
                        onClick = { selectedStandard = "T568A" },
                        label = { Text(text = "T-568A (ক্রসওভার)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyanPrimary,
                            selectedLabelColor = TechDarkBg,
                            containerColor = TechSurfaceVariant,
                            labelColor = TechTextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (selectedStandard == "T568B")
                        str("বাংলাদেশে ৯৯% আইএসপি এবং রাউটার-অনুর মধ্যকার প্যাচ ক্যাবলে T-568B স্ট্যান্ডার্ড ব্যবহৃত হয়।", "T-568B is the standard used for 99% of ethernet patch cables.")
                    else
                        str("এক মাথা T-568A এবং অন্য মাথা T-568B পাঞ্চ করলে ক্রসওভার (Crossover) ক্যাবল তৈরি হয়।", "Wiring one end T-568A and other end T-568B creates a crossover cable."),
                    color = TechAmber,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Visual Pinout Bars
        item {
            TechCard(containerColor = TechSurfaceVariant) {
                Text(
                    text = str("পিন ১ থেকে ৮ ক্রম (RJ45 ক্লিপ নিচে রেখে):", "Pin 1 to 8 Order (Clip facing down):"),
                    color = TechTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                pins.forEach { pin ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(TechDarkBg)
                                .border(1.dp, TechBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${pin.pinNumber}", color = TechCyanPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Colored Wire Bar Indicator
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(pin.colorHex))
                                .border(1.dp, if (pin.isStriped) Color.White else Color.Transparent, RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = str(pin.colorNameBn, pin.colorNameEn),
                            color = TechTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultLoginsTab() {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery) {
        if (searchQuery.trim().isEmpty()) TechToolsData.defaultLogins
        else {
            val q = searchQuery.trim().lowercase()
            TechToolsData.defaultLogins.filter {
                it.brand.lowercase().contains(q) || it.model.lowercase().contains(q) || it.ip.contains(q)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = str("রাউটার ব্র্যান্ড বা মডেল খুঁজুন (যেমন: TP-Link, Huawei, ZTE)...", "Search router brand or model..."),
                        color = TechTextMuted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TechCyanPrimary)
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = inputFieldColors()
            )
        }

        items(filtered) { item ->
            TechCard(containerColor = TechSurfaceVariant) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${item.brand} ${item.model}", fontWeight = FontWeight.Bold, color = TechTextPrimary, fontSize = 13.sp)
                    StatusBadge(text = item.ip, color = TechCyanPrimary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "Username", color = TechTextMuted, fontSize = 10.sp)
                        Text(text = item.username, color = TechGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text(text = "Password", color = TechTextMuted, fontSize = 10.sp)
                        Text(text = item.password, color = TechAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                    }
                    if (item.wifiSsid.isNotEmpty()) {
                        Column {
                            Text(text = "Default SSID", color = TechTextMuted, fontSize = 10.sp)
                            Text(text = item.wifiSsid, color = TechTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiberOpticalTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("ফাইবার অপটিক্যাল পাওয়ার রেঞ্জ গাইড (Rx Power dBm)", "Optical Fiber Power (Rx dBm) Range Guide"),
                    color = TechCyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = str("আইএসপি অপটিক্যাল পাওয়ার মিটার (OPM) বা ONU ওয়েব স্ট্যাটাসে প্রদর্শিত মানের ব্যাখ্যা:", "Interpretation of Optical Power Meter (OPM) & ONU Rx dBm:"),
                    color = TechTextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // dBm Table
                DbmRangeCard(
                    range = "-8.00 dBm থেকে -24.00 dBm",
                    status = str("আদর্শ ও চমৎকার (Ideal)", "Ideal / Optimal"),
                    desc = str("ইন্টারনেট ফুল স্পিডে চলবে, কোনো প্যাকেট লস হবে না।", "Perfect signal, zero packet loss, stable connection."),
                    color = TechGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                DbmRangeCard(
                    range = "-24.01 dBm থেকে -27.00 dBm",
                    status = str("সতর্কতা / বর্ডারলাইন (Borderline)", "Borderline Warning"),
                    desc = str("নেট চলবে তবে বৃষ্টি হলে বা ফাইবার নাড়া লাগলে ডিসকানেক্ট হতে পারে।", "Functional but susceptible to micro-bends and weather attenuation."),
                    color = TechAmber
                )

                Spacer(modifier = Modifier.height(8.dp))

                DbmRangeCard(
                    range = "-27.01 dBm বা তার নিচে (-30, -35 dBm)",
                    status = str("সংকটাপন্ন / সিগন্যাল লস (LOS Red)", "Critical / Loss of Signal"),
                    desc = str("ONU তে লাল বাতি জ্বলবে বা PON কাঁপবে। স্প্লিটার বা প্যাচ কর্ড পরিবর্তন দরকার।", "Red LOS alarm, flashing PON. Splitter/core splice inspection required."),
                    color = TechRed
                )
            }
        }

        item {
            TechCard(containerColor = TechSurfaceVariant) {
                Text(
                    text = str("কানেক্টর পার্থক্য: SC/UPC (নীল) বনাম SC/APC (সবুজ)", "SC/UPC (Blue) vs SC/APC (Green)"),
                    color = TechTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = str(
                        "• SC-UPC (নীল রঙের কানেক্টর): আল্ট্রা ফিজিক্যাল কন্টাক্ট (০ ডিগ্রি ফ্ল্যাট পলিশ)। সাধারণ ব্রডব্যান্ড ডেটা ONU তে সর্বাধিক ব্যবহৃত হয়।\n• SC-APC (সবুজ রঙের কানেক্টর): অ্যাঙ্গেলড ফিজিক্যাল কন্টাক্ট (৮ ডিগ্রি কৌণিক পলিশ)। ক্যাটভি (CATV) ও দীর্ঘ দূরত্বের জি-পন অপটিক্সে ব্যবহৃত হয়।\n\nসতর্কতা: নীল কানেক্টরের সাথে সরাসরি সবুজ অ্যাডাপ্টার জোড়া লাগাবেন না, ফাইবার ফেয়াররুল ভেঙে যাবে!",
                        "• SC-UPC (Blue): Ultra Physical Contact (flat 0° polish). Standard for broadband ONU data.\n• SC-APC (Green): Angled Physical Contact (8° angle). Preferred for CATV video and low back-reflection.\n\nCaution: Never mate Blue UPC directly into Green APC sockets to prevent ferrule core chipping."
                    ),
                    color = TechTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun DbmRangeCard(range: String, status: String, desc: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = range, color = TechTextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                StatusBadge(text = status, color = color)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, color = TechTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun inputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = TechCyanPrimary,
    unfocusedBorderColor = TechBorder,
    focusedContainerColor = TechSurfaceVariant,
    unfocusedContainerColor = TechSurfaceVariant,
    focusedTextColor = TechTextPrimary,
    unfocusedTextColor = TechTextPrimary
)
