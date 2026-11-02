package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@Composable
fun RouterGuidesScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedBrand by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedGuideId by remember { mutableStateOf<String?>("guide_tplink_pppoe") }

    val brands = listOf("All", "TP-Link", "Tenda", "D-Link", "MikroTik", "Huawei", "ZTE", "OpenWrt")

    val filteredGuides = remember(selectedBrand, searchQuery, LanguageManager.currentLanguage) {
        RouterGuidesData.guides.filter { guide ->
            val matchBrand = (selectedBrand == "All" || guide.brand.equals(selectedBrand, ignoreCase = true))
            val q = searchQuery.trim().lowercase()
            val matchQuery = q.isEmpty() ||
                    guide.brand.lowercase().contains(q) ||
                    guide.title().lowercase().contains(q) ||
                    guide.popularModels.lowercase().contains(q) ||
                    guide.defaultIp.lowercase().contains(q)
            matchBrand && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg)
    ) {
        // Search & Filter header
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = str("রাউটার মডেল বা ব্র্যান্ড খুঁজুন...", "Search model or brand..."),
                        color = TechTextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TechCyanPrimary)
                },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TechCyanPrimary,
                    unfocusedBorderColor = TechBorder,
                    focusedContainerColor = TechSurface,
                    unfocusedContainerColor = TechSurface,
                    focusedTextColor = TechTextPrimary,
                    unfocusedTextColor = TechTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Brand Selector Tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(brands) { brand ->
                    val isSelected = selectedBrand == brand
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedBrand = brand },
                        label = {
                            Text(
                                text = brand,
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

        // Guides List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredGuides) { guide ->
                val isExpanded = expandedGuideId == guide.id
                val isFav = FavoritesManager.isFavorite(guide.id)

                TechCard(
                    borderColor = if (isExpanded) TechCyanPrimary.copy(alpha = 0.6f) else TechBorder,
                    containerColor = TechSurface
                ) {
                    // Card Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedGuideId = if (isExpanded) null else guide.id },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = guide.brand, color = TechCyanPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = guide.popularModels,
                                    color = TechTextMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = guide.title(),
                                color = TechTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val favItem = FavoriteItem(
                                        id = guide.id,
                                        titleBn = guide.titleBn,
                                        titleEn = guide.titleEn,
                                        categoryBn = "${guide.brand} গাইড",
                                        categoryEn = "${guide.brand} Guide",
                                        content = guide.steps().joinToString("\n")
                                    )
                                    FavoritesManager.toggleFavorite(context, favItem)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isFav) TechAmber else TechTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = TechCyanPrimary
                            )
                        }
                    }

                    // Expanded Details
                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            HorizontalDivider(color = TechBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Default IP & Credentials Grid
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TechSurfaceVariant)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = str("ডিফল্ট আইপি / URL", "Default IP / URL"), fontSize = 10.sp, color = TechTextMuted)
                                    Text(text = guide.defaultIp, fontSize = 12.sp, color = TechCyanPrimary, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text(text = str("লগইন পাসওয়ার্ড", "Login Credentials"), fontSize = 10.sp, color = TechTextMuted)
                                    Text(text = guide.defaultCredentials, fontSize = 12.sp, color = TechAmber, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = guide.overview(),
                                color = TechTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = str("কনফিগারেশন ধাপসমূহ:", "Step-by-Step Instructions:"),
                                color = TechCyanPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            guide.steps().forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(TechCyanPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = TechCyanPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = step,
                                        color = TechTextPrimary,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Technician Pro Tip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TechAmber.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TechAmber.copy(alpha = 0.4f))
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Tip",
                                        tint = TechAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = guide.tip(),
                                        color = TechAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
