package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FavoritesManager
import com.example.data.str
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@Composable
fun FavoritesScreen() {
    val context = LocalContext.current
    val favorites = FavoritesManager.favorites

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (favorites.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = TechTextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = str("কোনো ফেভারিট আইটেম সেভ করা নেই", "No favorites saved yet"),
                            color = TechTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = str("রাউটার গাইড বা মাইক্রোটিক স্ক্রিপ্টের স্টার (★) আইকনে চাপ দিয়ে সেভ করে রাখুন।", "Tap the star (★) on any router guide or MikroTik command to save it here."),
                            color = TechTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        items(favorites) { item ->
            TechCard(containerColor = TechSurfaceVariant) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        StatusBadge(text = item.category(), color = TechAmber)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.title(),
                            color = TechTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Router Doctor BD", item.content)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, str("কপি করা হয়েছে!", "Copied to clipboard!"), Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TechCyanPrimary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = {
                                FavoritesManager.toggleFavorite(context, item)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Remove", tint = TechRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CodeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.content,
                        color = TechTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        maxLines = 6,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}
