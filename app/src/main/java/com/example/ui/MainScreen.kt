package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.str
import com.example.ui.components.TechTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*

data class NavDestination(
    val route: String,
    val titleBn: String,
    val titleEn: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf("dashboard") }

    val bottomNavItems = listOf(
        NavDestination("dashboard", "হোম", "Home", Icons.Default.Dashboard),
        NavDestination("routers", "রাউটার", "Routers", Icons.Default.Router),
        NavDestination("mikrotik", "মাইক্রোটিক", "MikroTik", Icons.Default.Terminal),
        NavDestination("diagnostics", "ডায়াগনস্টিক", "Tests", Icons.Default.Speed),
        NavDestination("tools", "টুলস", "Tools", Icons.Default.Build)
    )

    // Handle back button to return to dashboard
    if (currentRoute != "dashboard") {
        BackHandler {
            currentRoute = "dashboard"
        }
    }

    val screenTitle = when (currentRoute) {
        "dashboard" -> "Router Doctor BD"
        "routers" -> str("রাউটার সেটআপ গাইড", "Router Config Guides")
        "mikrotik" -> str("মাইক্রোটিক RouterOS জেনারেটর", "MikroTik Generator")
        "diagnostics" -> str("নেটওয়ার্ক টেস্ট ও ডায়াগনস্টিক", "Network Diagnostics")
        "lan_scanner" -> str("ল্যান ডিভাইস স্ক্যানার", "LAN Device Scanner")
        "troubleshooting" -> str("আইএসপি ট্রাবলশুটিং", "ISP Troubleshooting")
        "repair" -> str("ফার্মওয়্যার ও মেরামত", "Repair & Unbricking")
        "tools" -> str("টেকনিশিয়ান টুলস ও সাবনেট", "Technician Tools")
        "favorites" -> str("সংরক্ষিত ফেভারিট", "Saved Favorites")
        "about" -> str("ডেভেলপার ও টেকনিক্যাল প্রোফাইল", "About Technician")
        else -> "Router Doctor BD"
    }

    Scaffold(
        topBar = {
            TechTopBar(
                title = screenTitle,
                canNavigateBack = currentRoute != "dashboard",
                onNavigateBack = { currentRoute = "dashboard" },
                actions = {
                    if (currentRoute != "favorites") {
                        IconButton(onClick = { currentRoute = "favorites" }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorites",
                                tint = TechAmber
                            )
                        }
                    }
                    if (currentRoute != "about") {
                        IconButton(onClick = { currentRoute = "about" }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = TechCyanPrimary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TechSurface,
                contentColor = TechCyanPrimary,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentRoute = item.route },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleEn,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = str(item.titleBn, item.titleEn),
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechDarkBg,
                            selectedTextColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary,
                            unselectedIconColor = TechTextMuted,
                            unselectedTextColor = TechTextMuted
                        )
                    )
                }
            }
        },
        containerColor = TechDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "dashboard" -> DashboardScreen(onNavigate = { route -> currentRoute = route })
                "routers" -> RouterGuidesScreen(onNavigateBack = { currentRoute = "dashboard" })
                "mikrotik" -> MikrotikGeneratorScreen()
                "diagnostics" -> DiagnosticsScreen()
                "lan_scanner" -> LanScannerScreen()
                "troubleshooting" -> TroubleshootingScreen()
                "repair" -> RepairScreen()
                "tools" -> TechToolsScreen()
                "favorites" -> FavoritesScreen()
                "about" -> AboutTechnicianScreen()
                else -> DashboardScreen(onNavigate = { route -> currentRoute = route })
            }
        }
    }
}
