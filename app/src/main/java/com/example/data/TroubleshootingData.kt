package com.example.data

data class TroubleshootingIssue(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val severity: String, // Critical, High, Medium, Info
    val symptomsBn: String,
    val symptomsEn: String,
    val possibleCausesBn: List<String>,
    val possibleCausesEn: List<String>,
    val solutionsBn: List<String>,
    val solutionsEn: List<String>,
    val technicianNoteBn: String,
    val technicianNoteEn: String
) {
    fun title(): String = str(titleBn, titleEn)
    fun symptoms(): String = str(symptomsBn, symptomsEn)
    fun causes(): List<String> = if (LanguageManager.currentLanguage == AppLanguage.BN) possibleCausesBn else possibleCausesEn
    fun solutions(): List<String> = if (LanguageManager.currentLanguage == AppLanguage.BN) solutionsBn else solutionsEn
    fun note(): String = str(technicianNoteBn, technicianNoteEn)
}

object TroubleshootingData {
    val issues = listOf(
        TroubleshootingIssue(
            id = "issue_red_los",
            titleBn = "ONU তে লাল বাতি (LOS Red) জ্বলছে / নিভছে",
            titleEn = "ONU Optical LOS Blinking Red",
            severity = "Critical",
            symptomsBn = "ONU বা রাউটারে LOS (Loss of Signal) লাল বাতি ব্লিংক করছে এবং কোনো ইন্টারনেট কাজ করছে না।",
            symptomsEn = "The LOS light on the ONU is flashing red continuously and all internet connectivity is lost.",
            possibleCausesBn = listOf(
                "ফাইবার অপটিক প্যাচ কর্ড বাঁকানো বা ভেতর থেকে ভেঙে গেছে।",
                "ঘরের বাইরের ড্রপ তার ছিঁড়ে গেছে বা জয়েন্টে পানি ঢুকেছে।",
                "আইএসপির মেইন স্প্লিটার বা ওএলটি (OLT) ডাউন আছে।",
                "নীল রঙের SC-UPC কানেক্টরে ময়লা জমেছে বা আলগা হয়ে গেছে।"
            ),
            possibleCausesEn = listOf(
                "Fiber patch cable is sharply bent or snapped internally.",
                "Outdoor drop cable damaged on the pole or spliced joint compromised.",
                "ISP main splitter or OLT PON port is down/powered off.",
                "SC-UPC connector dirty, scratched or loose inside the ONU port."
            ),
            solutionsBn = listOf(
                "১. ONU এর পেছনে নীল/সবুজ ফাইবার কানেক্টরটি আলতো করে খুলে টিস্যু বা আইসোপ্রোপাইল অ্যালকোহল দিয়ে পরিষ্কার করে শক্ত করে লাগান।",
                "২. ঘরের ভেতরের হলুদ বা কালো ফাইবার প্যাচ কর্ডে কোনো গিঁট বা ৯০ ডিগ্রির বেশি বাঁক আছে কি না পরীক্ষা করুন।",
                "৩. ONU পেজে লগইন করে 'Optical Information' এ Rx Optical Power দেখুন। মান যদি -28 dBm এর বেশি খারাপ (-30, -35 বা লো) হয় তবে আইএসপি হেল্পডেস্কে কল দিন।"
            ),
            solutionsEn = listOf(
                "1. Gently unplug the SC fiber connector from the ONU, inspect the ferrule, clean with optical wipe and reseat firmly.",
                "2. Check if the yellow/black indoor patch cord has sharp kinks, tight bends or pinches under furniture.",
                "3. Log into the ONU web GUI, verify Rx Optical Power. If weaker than -27 dBm, dispatch line technician to check the distribution pole."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: ফিল্ডে সর্বদা VFL (Visual Fault Locator) লেজার পেন দিয়ে ড্রপ ক্যাবল চেক করুন। লাল আলো বের হলে বুঝবেন ভেতরে কোর কাটা আছে।",
            technicianNoteEn = "Field Tip: Use a VFL (Visual Fault Locator) red laser pen to quickly identify micro-bends or broken cores along the drop cable."
        ),
        TroubleshootingIssue(
            id = "issue_pon_blinking",
            titleBn = "PON বাতি অনবরত সবুজ জ্বলছে-নিভছে (Blinking Green)",
            titleEn = "PON Light Blinking Green (Not Solid)",
            severity = "High",
            symptomsBn = "LOS লাল বাতি নিভে আছে, কিন্তু PON বাতি স্থির না হয়ে বারবার সবুজ ব্লিংক করছে।",
            symptomsEn = "LOS red is off, but PON light blinks green continuously without turning solid.",
            possibleCausesBn = listOf(
                "ONU টি আইএসপির OLT এর সাথে রেজিস্টার হতে পারছে না।",
                "নতুন ONU লাগানোর কারণে MAC বা GPON SN আনঅথরাইজড অবস্থায় আছে।",
                "OLT তে কনফিগারেশন মিসম্যাচ অথবা প্রোফাইল অ্যাসাইন করা হয়নি।"
            ),
            possibleCausesEn = listOf(
                "ONU fails to register with the ISP OLT.",
                "Replaced ONU has an unauthorized MAC address or GPON Serial Number (SN).",
                "OLT profile configuration mismatch or unregistered subscriber."
            ),
            solutionsBn = listOf(
                "১. ONU এর নিচের স্টিকার থেকে GPON SN বা MAC এড্রেসটি আইএসপিকে জানান।",
                "২. আইএসপি তাদের ওএলটিতে ONU টি অথরাইজ বা বাইন্ড করলেই PON বাতি স্থির সবুজ হয়ে যাবে।",
                "৩. কিছু ONU তে EPON / GPON অটো-ডিটেক্ট ঠিকমতো না হলে সেটিংস থেকে মোড সিলেক্ট করে রিস্টার্ট দিন।"
            ),
            solutionsEn = listOf(
                "1. Note the GPON SN or MAC address from the ONU label and supply it to the ISP NOC.",
                "2. Once authorized on the OLT, the PON light will turn solid green.",
                "3. If using hybrid XPON ONU, verify whether OLT is EPON or GPON and force the correct mode if needed."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: হুয়াওয়ে OLT তে 'display ont autofind 0' কমান্ড দিয়ে নতুন আনরেজিস্টারড ONU এর SN খুঁজে পাওয়া যায়।",
            technicianNoteEn = "Tech Tip: On Huawei OLTs, run 'display ont autofind 0' to discover the pending unregistered ONU serial."
        ),
        TroubleshootingIssue(
            id = "issue_pppoe_651",
            titleBn = "PPPoE এরর 651 / 678 (The modem has reported an error)",
            titleEn = "PPPoE Error 651 / 678 (Modem Reported Error)",
            severity = "High",
            symptomsBn = "ডায়াল-আপ করার সময় অথবা রাউটার স্ট্যাটাসে 'PPPoE Dialing Timeout / Error 651' দেখাচ্ছে।",
            symptomsEn = "PPPoE connection fails with timeout or Error 651 on dial-up.",
            possibleCausesBn = listOf(
                "ONU ও রাউটারের মধ্যকার LAN কেবল সংযোগ বিচ্ছিন্ন বা আলগা।",
                "আইএসপির সার্ভার/সুইচ পোর্ট ডাউন অথবা VLAN ট্যাগিং ভুল।",
                "রাউটারের WAN পোর্টে ফিজিক্যাল লিংক বা লাইট জ্বলছে না।"
            ),
            possibleCausesEn = listOf(
                "Loose or broken patch cable connecting ONU LAN to Router WAN.",
                "ISP aggregation switch port down or VLAN tag mismatch.",
                "No physical link light on the Router WAN port."
            ),
            solutionsBn = listOf(
                "১. রাউটারের WAN পোর্টের বাতি জ্বলছে কি না নিশ্চিত হন। না জ্বললে কেবল পরিবর্তন করুন।",
                "২. ONU এর LAN পোর্ট থেকে সরাসরি পিসিতে কেবল লাগিয়ে উইন্ডোজ ব্রডব্যান্ড ডায়াল-আপ করে দেখুন।",
                "৩. রাউটারের WAN সেটিংসে VLAN ID এনেবল/ডিজেবল করে আইএসপির সঠিক VLAN দিন।"
            ),
            solutionsEn = listOf(
                "1. Verify the Router WAN LED is glowing. Replace the Cat6 patch cord if dead.",
                "2. Connect PC directly to ONU LAN port and create a Windows PPPoE dialer to isolate the router.",
                "3. Ensure WAN VLAN ID is set according to ISP specifications (e.g. VLAN 100/500)."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: কেবল টেস্টার দিয়ে ৮টি পিনই ঠিকভাবে পাঞ্চ হয়েছে কি না চেক করে নিন।",
            technicianNoteEn = "Tech Tip: Test the patch cable with an RJ45 cable continuity tester to detect open pairs."
        ),
        TroubleshootingIssue(
            id = "issue_pppoe_691",
            titleBn = "PPPoE এরর 691 (Username or Password Invalid)",
            titleEn = "PPPoE Error 691 (Access Denied / Invalid Credentials)",
            severity = "Medium",
            symptomsBn = "রাউটারের WAN স্ট্যাটাসে 'Authentication Failed' বা পিসিতে এরর 691 দেখাচ্ছে।",
            symptomsEn = "WAN status shows 'Authentication Failed' or Windows displays Error 691.",
            possibleCausesBn = listOf(
                "ইউজারনেম বা পাসওয়ার্ডে বড়/ছোট হাতের অক্ষরের ভুল।",
                "মাসিক বিল বকেয়া থাকায় আইএসপি অ্যাকাউন্ট ব্লক বা সাসপেন্ড করেছে।",
                "আইএসপির রেডিয়াস সার্ভারে ম্যাক বাইন্ডিং লক হয়ে আছে (অন্য ডিভাইসে ট্রাই করলে)।"
            ),
            possibleCausesEn = listOf(
                "Case-sensitive typo in username or password.",
                "Account suspended/blocked due to pending bill.",
                "Radius server MAC address lock mismatch after router replacement."
            ),
            solutionsBn = listOf(
                "১. ইউজারনেম ও পাসওয়ার্ড বড়/ছোট হাতের অক্ষর সাবধানে পুনরায় টাইপ করুন।",
                "২. আইএসপি কাস্টমার কেয়ারে ফোন দিয়ে অ্যাকাউন্ট স্ট্যাটাস ও ম্যাক রিলিজ (MAC Reset) করতে বলুন।",
                "৩. পুরোনো রাউটারের ম্যাক এড্রেসটি নতুন রাউটারে 'MAC Clone' অপশনে বসিয়ে দিন।"
            ),
            solutionsEn = listOf(
                "1. Carefully retype username and password ensuring caps lock is off.",
                "2. Call ISP support to check billing status and request a RADIUS MAC binding reset.",
                "3. Clone the previous router's MAC address in the new router's WAN settings."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: রাউটার পরিবর্তন করলে আইএসপি পোর্টে পুরোনো ম্যাক ক্যাশ থেকে যায়। আইএসপি রিলিজ না করলে ম্যাক ক্লোন দ্রুততম সমাধান।",
            technicianNoteEn = "Tech Tip: MAC cloning resolves stale RADIUS lease locks instantly without waiting for ISP NOC queues."
        ),
        TroubleshootingIssue(
            id = "issue_wifi_no_internet",
            titleBn = "WiFi কানেক্টেড কিন্তু নো ইন্টারনেট (No Internet / IP Conflict)",
            titleEn = "WiFi Connected But No Internet / IP Conflict",
            severity = "Medium",
            symptomsBn = "ফোনে ওয়াইফাই সিগন্যাল ফুল দেখাচ্ছে কিন্তু 'Connected, without internet' লেখা উঠছে।",
            symptomsEn = "Device is connected to WiFi with full bars, but status shows 'Connected, no internet'.",
            possibleCausesBn = listOf(
                "ONU এবং রাউটার উভয়ের আইপি এক হয়ে গেছে (উভয়টিতে 192.168.0.1 বা 192.168.1.1)।",
                "DNS সার্ভার ফেইল করেছে।",
                "রাউটার WAN আইপি পায়নি বা আইএসপি গেটওয়ে ডাউন।"
            ),
            possibleCausesEn = listOf(
                "IP Conflict: Both ONU and Router are using the same gateway IP (e.g. 192.168.1.1).",
                "DNS resolution failure on the client or router.",
                "Router did not obtain a valid public/private WAN IP."
            ),
            solutionsBn = listOf(
                "১. রাউটারের LAN IP পরিবর্তন করে অন্য সাবনেটে দিন (যেমন 192.168.10.1 বা 192.168.2.1)।",
                "২. রাউটারের DHCP সার্ভারে প্রাইমারি ডিএনএস 8.8.8.8 এবং সেকেন্ডারি ডিএনএস 1.1.1.1 বসান।",
                "৩. রাউটার এবং ONU উভয়টি ৫ মিনিটের জন্য বন্ধ করে আবার চালু করুন।"
            ),
            solutionsEn = listOf(
                "1. Change router LAN IP to a distinct subnet (e.g., 192.168.20.1) to avoid collision with ONU 192.168.1.1.",
                "2. Manually override DNS servers with Google DNS 8.8.8.8 and Cloudflare 1.1.1.1.",
                "3. Power cycle both the ONU and router for 30 seconds."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: সাবনেট কনফ্লিক্ট বাংলাদেশের টেকনিশিয়ানদের সবচেয়ে পরিচিত ভুল। ONU ও রাউটারের LAN IP কখনোই একই হতে পারবে না।",
            technicianNoteEn = "Tech Tip: Subnet collision is the #1 cause of no-internet. Keep ONU on 192.168.1.1 and Router LAN on 192.168.0.1 or 192.168.11.1."
        ),
        TroubleshootingIssue(
            id = "issue_high_ping_bufferbloat",
            titleBn = "গেমিংয়ে হাই পিং ও প্যাকেট লস (BDIX & International Lag)",
            titleEn = "High Gaming Latency & Packet Loss (Bufferbloat)",
            severity = "Medium",
            symptomsBn = "Free Fire, PUBG, Valorant এ পিং 150-300ms বা রেড পিং হচ্ছে, বাফারব্লোট এবং প্যাকেট লস।",
            symptomsEn = "High in-game ping (150-300ms) with jitter and packet loss during multiplayer gaming.",
            possibleCausesBn = listOf(
                "অন্য কোনো ডিভাইস ব্যাকগ্রাউন্ডে টরেন্ট বা ইউটিউব ফুল ব্যান্ডউইথ টানছে (বাফারব্লোট)।",
                "আইএসপির বিডিআইএক্স (BDIX) বা ইন্টারন্যাশনাল সাবমেরিন ক্যাবল রাউটিং জ্যাম।",
                "রাউটারে ভুল MTU সাইজ (১৫০০ এর বদলে ১৪৮০/১৪৯২ না রাখা)।",
                "২.৪ গিগাহার্টজ ওয়াইফাই চ্যানেল ইন্টারফেয়ারেন্স।"
            ),
            possibleCausesEn = listOf(
                "Bufferbloat: Bandwidth saturation from background downloads causing router queue bloat.",
                "Sub-optimal ISP routing to gaming servers or congested BDIX peering link.",
                "MTU mismatch causing packet fragmentation.",
                "2.4GHz WiFi channel interference from neighboring routers."
            ),
            solutionsBn = listOf(
                "১. ৫ গিগাহার্টজ ওয়াইফাই অথবা সরাসরি LAN তার দিয়ে পিসি/মোবাইলে খেলুন।",
                "২. রাউটারে QoS / Bandwidth Control এনেবল করে নিজের ডিভাইসের আইপিকে সর্বোচ্চ প্রায়োরিটি দিন।",
                "৩. রাউটারের WAN MTU সাইজ ১৪৮০ সেট করুন।"
            ),
            solutionsEn = listOf(
                "1. Switch from congested 2.4GHz to 5GHz AC/AX band or use a wired Cat6 cable.",
                "2. Configure Bandwidth Control / QoS or SQM Cake to prevent bufferbloat under load.",
                "3. Adjust WAN MTU to 1480 to prevent packet fragmentation."
            ),
            technicianNoteBn = "টেকনিশিয়ান টিপ: MikroTik এ FastTrack চালু থাকলে এবং Simple Queue থাকলে FastTrack বাইপাস করতে হবে QoS কার্যকর করার জন্য।",
            technicianNoteEn = "Tech Tip: Ensure FastTrack is disabled for specific marked traffic if queuing/QoS is applied in MikroTik."
        )
    )
}
