package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.str
import com.example.ui.components.StatusBadge
import com.example.ui.components.TechCard
import com.example.ui.theme.*

@Composable
fun AboutTechnicianScreen() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TechDarkBg),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Official Badge & Profile
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(3.dp, TechCyanPrimary, CircleShape)
                        .background(TechSurface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.router_doctor_logo),
                        contentDescription = "Router Doctor BD Kabir Apps Badge",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Router Doctor BD",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TechTextPrimary
                )

                Text(
                    text = "Developer: Kabir Molla (Kabir Apps)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechCyanPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = str(
                        "রাউটার সমস্যা? চিন্তা নেই! আমরাই আছি আপনার পাশে",
                        "Stay Connected... Stay Online... We Are Here For You"
                    ),
                    fontSize = 12.sp,
                    color = TechAmber,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Direct Action Contact Buttons
        item {
            TechCard(borderColor = TechAmber.copy(alpha = 0.5f), containerColor = TechSurfaceVariant) {
                Text(
                    text = str("জরুরী টেকনিক্যাল সাপোর্ট ও সার্ভিস যোগাযোগ", "Direct Technical Support & Contact"),
                    color = TechTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(18.dp))
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
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = TechCyanPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "WhatsApp", color = TechCyanPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = TechRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = str("কোটালীপাড়া, গোপালগঞ্জ, বাংলাদেশ", "Kotalipara, Gopalganj, Bangladesh"),
                        color = TechTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Services Provided Grid
        item {
            TechCard(containerColor = TechSurface) {
                Text(
                    text = str("আমাদের সেবাসমূহ (Our Services):", "Services Provided:"),
                    color = TechCyanPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                ServiceItem(
                    icon = Icons.Default.Router,
                    title = str("Router Setup & Repair", "Router Setup & Repair"),
                    desc = str("টিপি-লিংক, টেন্ডা, ডি-লিংক, মাইক্রোটিক, হুয়াওয়ে, জেডটিই রাউটার কনফিগারেশন ও ফার্মওয়্যার রিকভারি।", "All branded router configuration, unbricking, and hardware maintenance.")
                )
                ServiceItem(
                    icon = Icons.Default.Videocam,
                    title = str("CCTV Service", "CCTV Service"),
                    desc = str("আইপি ক্যামেরা, ডিভিআর, এনভিআর অনলাইন রিমোট ভিউয়িং ও পোর্ট ফরওয়ার্ডিং সেটআপ।", "IP Camera, DVR/NVR setup, remote mobile streaming and port forwarding.")
                )
                ServiceItem(
                    icon = Icons.Default.Computer,
                    title = str("Computer Service", "Computer Service"),
                    desc = str("ডেস্কটপ ও ল্যাপটপ হার্ডওয়্যার ট্রাবলশুটিং, ওএস ইনস্টলেশন ও নেটওয়ার্কিং সল্যুশন।", "Desktop & laptop repairs, OS installations and office LAN networking.")
                )
                ServiceItem(
                    icon = Icons.Default.Speed,
                    title = str("Internet Diagnostics", "Internet Diagnostics"),
                    desc = str("লাইভ ইন্টারনেট স্পিড টেস্ট, পিং অপ্টিমাইজেশন ও বিডিআইএক্স গেমিং ল্যাগ সমাধান।", "Live broadband speed testing, ping optimization and fiber optical dBm inspection.")
                )
            }
        }

        // App Info & Version
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Router Doctor BD v1.0",
                    color = TechTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Crafted for Bangladesh ISP Technicians & Network Engineers",
                    color = TechTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ServiceItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(TechCyanPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TechCyanPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, color = TechTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, color = TechTextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
    HorizontalDivider(color = TechBorder)
}
