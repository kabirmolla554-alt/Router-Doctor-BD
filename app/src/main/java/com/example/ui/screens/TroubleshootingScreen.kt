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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@Composable
fun TroubleshootingScreen() {
    val context = LocalContext.current
    var selectedSeverity by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedId by remember { mutableStateOf<String?>("issue_red_los") }

    val severities = listOf("All", "Critical", "High", "Medium")

    val filteredIssues = remember(selectedSeverity, searchQuery, LanguageManager.currentLanguage) {
        TroubleshootingData.issues.filter { issue ->
            val matchSeverity = (selectedSeverity == "All" || issue.severity.equals(selectedSeverity, ignoreCase = true))
            val q = searchQuery.trim().lowercase()
            val matchQuery = q.isEmpty() ||
                    issue.title().lowercase().contains(q) ||
                    issue.symptoms().lowercase().contains(q)
            matchSeverity && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = str("সমস্যা বা এরর কোড খুঁজুন (যেমন: LOS, 651)...", "Search issue or error (e.g. LOS, 651)..."),
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

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(severities) { sev ->
                    val isSelected = selectedSeverity == sev
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSeverity = sev },
                        label = { Text(text = sev, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (sev == "Critical") TechRed else if (sev == "High") TechOrange else TechCyanPrimary,
                            selectedLabelColor = TechDarkBg,
                            containerColor = TechSurface,
                            labelColor = TechTextPrimary
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredIssues) { issue ->
                val isExpanded = expandedId == issue.id
                val isFav = FavoritesManager.isFavorite(issue.id)
                val badgeColor = when (issue.severity) {
                    "Critical" -> TechRed
                    "High" -> TechOrange
                    else -> TechAmber
                }

                TechCard(
                    borderColor = if (isExpanded) badgeColor.copy(alpha = 0.5f) else TechBorder,
                    containerColor = TechSurface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedId = if (isExpanded) null else issue.id },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusBadge(text = issue.severity, color = badgeColor)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = issue.title(),
                                color = TechTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val fav = FavoriteItem(
                                        id = issue.id,
                                        titleBn = issue.titleBn,
                                        titleEn = issue.titleEn,
                                        categoryBn = "আইএসপি ট্রাবলশুট",
                                        categoryEn = "ISP Troubleshoot",
                                        content = issue.solutions().joinToString("\n")
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

                            // Symptoms
                            Text(text = str("লক্ষণ (Symptoms):", "Symptoms:"), color = TechAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = issue.symptoms(), color = TechTextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Causes
                            Text(text = str("সম্ভাব্য কারণসমূহ:", "Possible Causes:"), color = TechRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            issue.causes().forEach { cause ->
                                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                    Text(text = "• ", color = TechRed, fontWeight = FontWeight.Bold)
                                    Text(text = cause, color = TechTextPrimary, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Solutions
                            Text(text = str("সমাধানের নির্দেশিকা:", "Resolution Steps:"), color = TechGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            issue.solutions().forEach { sol ->
                                Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(TechGreen)
                                            .padding(top = 6.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = sol, color = TechTextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Pro Tip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TechSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyanPrimary.copy(alpha = 0.3f))
                            ) {
                                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                    Icon(imageVector = Icons.Default.Handyman, contentDescription = null, tint = TechCyanPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = issue.note(), color = TechCyanPrimary, fontSize = 11.sp, lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
