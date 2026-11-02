package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@Composable
fun RepairScreen() {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("All") }
    var expandedId by remember { mutableStateOf<String?>("repair_tftp_unbrick") }

    val filtered = remember(selectedType, LanguageManager.currentLanguage) {
        RepairGuidesData.guides.filter { guide ->
            selectedType == "All" || guide.type.equals(selectedType, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg)
    ) {
        // Tab Filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Firmware", "Hardware").forEach { type ->
                val isSelected = selectedType == type
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedType = type },
                    label = { Text(text = type, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (type == "Hardware") TechOrange else TechCyanPrimary,
                        selectedLabelColor = TechDarkBg,
                        containerColor = TechSurface,
                        labelColor = TechTextPrimary
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filtered) { guide ->
                val isExpanded = expandedId == guide.id
                val isFav = FavoritesManager.isFavorite(guide.id)
                val typeColor = if (guide.type == "Hardware") TechOrange else TechCyanPrimary

                TechCard(
                    borderColor = if (isExpanded) typeColor.copy(alpha = 0.5f) else TechBorder,
                    containerColor = TechSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedId = if (isExpanded) null else guide.id },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = guide.type, color = typeColor)
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(text = guide.difficulty, color = TechAmber)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = guide.title(),
                                color = TechTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val fav = FavoriteItem(
                                        id = guide.id,
                                        titleBn = guide.titleBn,
                                        titleEn = guide.titleEn,
                                        categoryBn = "${guide.type} মেরামত",
                                        categoryEn = "${guide.type} Repair",
                                        content = guide.steps().joinToString("\n")
                                    )
                                    FavoritesManager.toggleFavorite(context, fav)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Favorite",
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

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = TechBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = guide.description(), color = TechTextSecondary, fontSize = 12.sp, lineHeight = 17.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Required Tools Chips
                            Text(text = str("প্রয়োজনীয় যন্ত্রপাতি ও সরঞ্জাম:", "Tools & Software Required:"), color = TechCyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                guide.tools().forEach { tool ->
                                    StatusBadge(text = tool, color = TechTextPrimary, backgroundColor = TechSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Repair Steps
                            Text(text = str("মেরামত বা ফ্ল্যাশিং পদ্ধতি:", "Repair / Flashing Procedure:"), color = TechAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            guide.steps().forEach { step ->
                                Text(
                                    text = step,
                                    color = TechTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Safety Warning
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TechRed.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TechRed.copy(alpha = 0.4f))
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = TechRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = guide.safety(), color = TechRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
