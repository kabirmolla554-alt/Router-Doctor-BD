package com.example.data

data class RepairGuide(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val type: String, // "Firmware" or "Hardware"
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val toolsRequiredBn: List<String>,
    val toolsRequiredEn: List<String>,
    val descriptionBn: String,
    val descriptionEn: String,
    val stepsBn: List<String>,
    val stepsEn: List<String>,
    val safetyWarningBn: String,
    val safetyWarningEn: String
) {
    fun title(): String = str(titleBn, titleEn)
    fun description(): String = str(descriptionBn, descriptionEn)
    fun tools(): List<String> = if (LanguageManager.currentLanguage == AppLanguage.BN) toolsRequiredBn else toolsRequiredEn
    fun steps(): List<String> = if (LanguageManager.currentLanguage == AppLanguage.BN) stepsBn else stepsEn
    fun safety(): String = str(safetyWarningBn, safetyWarningEn)
}

object RepairGuidesData {
    val guides = listOf(
        RepairGuide(
            id = "repair_tftp_unbrick",
            titleBn = "ব্রিকড রাউটার রিকভারি: TFTP ফ্ল্যাশিং পদ্ধতি",
            titleEn = "Unbrick Dead Router: TFTP Recovery Method",
            type = "Firmware",
            difficulty = "Intermediate",
            toolsRequiredBn = listOf("উইন্ডোজ পিসি/ল্যাপটপ", "LAN কেবল", "Tftpd64 সফটওয়্যার", "অফিসিয়াল বিনারি (.bin) ফার্মওয়্যার ফাইল"),
            toolsRequiredEn = listOf("Windows PC", "Cat6 LAN Cable", "Tftpd64 tool", "Stock Factory .bin Firmware"),
            descriptionBn = "বিদ্যুৎ চলে যাওয়া বা ভুল ফাইল ফ্ল্যাশ করার পর রাউটারের সব বাতি এক সাথে জ্বলে থাকা অথবা রিবুট লুপে পড়া রাউটার উদ্ধার করার পদ্ধতি।",
            descriptionEn = "Revive soft-bricked routers stuck in boot loops or all-LED states using the bootloader TFTP client.",
            stepsBn = listOf(
                "১. প্রস্তুত ফার্মওয়্যার ফাইলটি রিনেম করুন (যেমন TP-Link WR840N এর জন্য 'tp_recovery.bin')।",
                "২. পিসির ল্যান কার্ডে স্ট্যাটিক আইপি বসান: IP 192.168.0.66, Subnet Mask 255.255.255.0, Gateway ফাঁকা রাখুন।",
                "৩. পিসিতে Tftpd64 সফটওয়্যার ওপেন করে সার্ভার ইন্টারফেস '192.168.0.66' সিলেক্ট করুন এবং ফার্মওয়্যার ফাইলের ফোল্ডার ব্রাউজ করুন।",
                "৪. পিসির সাথে রাউটারের LAN1 পোর্টে ক্যাবল লাগান।",
                "৫. রাউটারের Reset বাটন টুথপিক দিয়ে চেপে ধরে রেখে অ্যাডাপ্টারের পাওয়ার অন করুন। ১০ সেকেন্ড ধরে রাখুন।",
                "৬. Tftpd64 স্ক্রিনে ফার্মওয়্যার ডাউনলোড প্রগ্রেস বার আসবে। ফাইল পাঠানো শেষ হলে ২ মিনিট অপেক্ষা করুন, রাউটার একা একাই রিবুট হয়ে ঠিক হয়ে যাবে।"
            ),
            stepsEn = listOf(
                "1. Rename official firmware to the required bootloader name (e.g. tp_recovery.bin).",
                "2. Assign static IP on your PC: IP 192.168.0.66, Netmask 255.255.255.0, leave gateway empty.",
                "3. Launch Tftpd64, bind server to 192.168.0.66, and point Current Directory to the firmware folder.",
                "4. Connect Ethernet cable from PC to Router LAN Port 1.",
                "5. Hold down the Reset button using a pin, then plug in the power supply. Keep holding for 8-10 seconds.",
                "6. Observe TFTP transfer progress. Wait 3 minutes for automated flash write and reboot."
            ),
            safetyWarningBn = "সতর্কতা: প্রসেস চলাকালীন বিদ্যুৎ বা ক্যাবল যেন বিচ্ছিন্ন না হয়।",
            safetyWarningEn = "Warning: Never unplug power during the flash write process."
        ),
        RepairGuide(
            id = "repair_mikrotik_netinstall",
            titleBn = "MikroTik Netinstall: পাসওয়ার্ড ভুলে যাওয়া ও সফটওয়্যার রিস্টোর",
            titleEn = "MikroTik Netinstall: Complete Reinstall & Password Reset",
            type = "Firmware",
            difficulty = "Advanced",
            toolsRequiredBn = listOf("MikroTik Netinstall EXE", "রাউটারের আর্কিটেকচার প্যাকেজ (.npk)", "ল্যাপটপ ও LAN কেবল"),
            toolsRequiredEn = listOf("MikroTik Netinstall tool", "Matching .npk RouterOS package (mipsbe, arm, etc.)", "Ethernet Cable"),
            descriptionBn = "মাইক্রোটিকের পাসওয়ার্ড ভুলে গেলে বা ওএস ক্র্যাশ করলে ফ্যাক্টরি লাইসেন্স অক্ষুণ্ণ রেখে নতুন করে ওএস ইনস্টল করার অফিশিয়াল পদ্ধতি।",
            descriptionEn = "Official recovery procedure to reinstall clean RouterOS via BOOTP/PXE without losing Software-ID or RouterBOARD license.",
            stepsBn = listOf(
                "১. পিসির সকল ওয়াইফাই ও অ্যান্টিভাইরাস/ফায়ারওয়াল সাময়িকভাবে বন্ধ করুন।",
                "২. পিসির ইথারনেট আইপি দিন: 192.168.88.2, সাবনেট 255.255.255.0।",
                "৩. Netinstall রান করে 'Net booting' এ ক্লিক করুন। 'Boot Server enabled' টিক দিয়ে Client IP দিন: 192.168.88.3।",
                "৪. মাইক্রোটিকের Ether1 (PoE/BOOT) পোর্টে পিসির তার লাগান।",
                "৫. রাউটারের Reset বাটন চেপে ধরে পাওয়ার প্লাগ ইন করুন। প্রায় ২০-২৫ সেকেন্ড চেপে ধরে রাখুন যতক্ষণ না Netinstall এর 'Routers/Drives' লিস্টে রাউটারটি দেখা যায়।",
                "৬. রাউটার সিলেক্ট করুন, .npk প্যাকেজ ব্রাউজ করুন এবং 'Install' বাটনে চাপ দিন।"
            ),
            stepsEn = listOf(
                "1. Disable all Windows Firewalls, antivirus and active WiFi adapters.",
                "2. Set PC IP: 192.168.88.2, Subnet: 255.255.255.0.",
                "3. Open Netinstall > Net Booting > check Boot Server enabled and set Client IP: 192.168.88.3.",
                "4. Plug LAN cable from PC into MikroTik Ether1 (BOOT port).",
                "5. Hold down Reset button, plug in DC power, hold for ~20 seconds until router appears under Routers list.",
                "6. Select the device, check RouterOS package, and click Install."
            ),
            safetyWarningBn = "সতর্কতা: উইন্ডোজ ডিফেন্ডার বা থার্ড পার্টি ফায়ারওয়াল অন থাকলে Netinstall প্যাকেট ব্লক করে দেয়।",
            safetyWarningEn = "Warning: Windows Firewall must be completely stopped for BOOTP UDP packets to pass through."
        ),
        RepairGuide(
            id = "repair_capacitor_replacement",
            titleBn = "ফোলা ক্যাপাসিটর পরিবর্তন (Capacitor Replacement)",
            titleEn = "Blown Capacitor Repair (Fix Random Restarts)",
            type = "Hardware",
            difficulty = "Intermediate",
            toolsRequiredBn = listOf("সোল্ডারিং আয়রন (30W-60W)", "লিড ও রজন", "ডি-সোল্ডারিং উইক/পাম্প", "নতুন ক্যাপাসিটর (16V 470uF বা 1000uF Low-ESR)"),
            toolsRequiredEn = listOf("Soldering Iron (30W-60W)", "Solder wire & flux", "Desoldering pump", "Replacement Low-ESR Capacitors (16V 470uF/1000uF)"),
            descriptionBn = "রাউটার চালু হয়ে কিছুক্ষণ পর একা একা রিস্টার্ট দেওয়া, ওয়াইফাই অন হলেই বন্ধ হয়ে যাওয়া বা পাওয়ার লাইট টিমটিম করার প্রধান কারণ ফোলা ক্যাপাসিটর।",
            descriptionEn = "Fix random restarts, low WiFi range, and unstable power caused by degraded or bulging electrolytic capacitors on the PCB.",
            stepsBn = listOf(
                "১. রাউটারের নিচের স্ক্রু খুলে কভার সাবধানে আলাদা করুন।",
                "২. মাদারবোর্ডে সিলিন্ডার আকৃতির ক্যাপাসিটরগুলো খেয়াল করুন—উপরের মাথা ফুলে যাওয়া বা তলা ফেটে যাওয়া কোনো ক্যাপাসিটর আছে কি না।",
                "৩. ত্রুটিযুক্ত ক্যাপাসিটরের ভোল্টেজ ও ক্যাপাসিট্যান্স মান নোট করুন (যেমন 16V 470uF)।",
                "৪. সোল্ডারিং আয়রন দিয়ে পেছনের জয়েন্ট গরম করে ডি-সোল্ডারিং পাম্প দিয়ে পুরোনো ক্যাপাসিটরটি তুলে ফেলুন।",
                "৫. নতুন Low-ESR ক্যাপাসিটর লাগানোর সময় পোলারিটি (মাইনাস দাগ বোর্ডে চিহ্নিত দাগের সাথে) মিলিয়ে লাগান।",
                "৬. সোল্ডারিং শেষ করে অতিরিক্ত পিন কেটে ফেলুন এবং পাওয়ার দিয়ে ভোল্টেজ চেক করুন।"
            ),
            stepsEn = listOf(
                "1. Disassemble the router housing by removing bottom rubber feet screws.",
                "2. Visually inspect the electrolytic capacitors for bulging tops or leaked electrolyte.",
                "3. Note capacitance and voltage ratings (e.g., 16V 470µF or 1000µF).",
                "4. Heat solder joints with soldering iron and remove old capacitor with solder sucker.",
                "5. Insert new Low-ESR capacitor observing strict polarity (negative stripe aligned with shaded PCB silkscreen).",
                "6. Solder firmly, trim protruding leads, and bench test with load."
            ),
            safetyWarningBn = "সতর্কতা: ক্যাপাসিটরের পজিটিভ ও নেগেটিভ উল্টো লাগালে ক্যাপাসিটর বিস্ফোরণ ঘটবে।",
            safetyWarningEn = "Warning: Reverse polarity will cause electrolytic capacitors to burst."
        ),
        RepairGuide(
            id = "repair_adapter_voltage_drop",
            titleBn = "পাওয়ার অ্যাডাপ্টার ড্রপ ও বারবার রিবুট সমস্যা",
            titleEn = "Power Adapter Voltage Drop Diagnosis",
            type = "Hardware",
            difficulty = "Beginner",
            toolsRequiredBn = listOf("ডিজিটাল মাল্টিমিটার", "৯ভি বা ১২ভি ১এ ভালো পাওয়ার অ্যাডাপ্টার"),
            toolsRequiredEn = listOf("Digital Multimeter", "Known-good 9V/12V 1A DC Adapter"),
            descriptionBn = "বাইরে থেকে অ্যাডাপ্টার ঠিক দেখালেও ভেতরে কারেন্ট ক্যাপাসিটি কমে গেলে রাউটার বারবার রিবুট নেয়।",
            descriptionEn = "Diagnose failing DC wall warts that measure nominal voltage without load but collapse under WiFi RF transmission load.",
            stepsBn = listOf(
                "১. মাল্টিমিটার দিয়ে নো-লোড অবস্থায় ডিসি জ্যাকের ভেতর পিন ও বাইরে ভোল্টেজ মাপুন (৯ভি বা ১২ভি দেখাবে)।",
                "২. এবার রাউটারে প্লাগ ইন করা অবস্থায় ভেতরে প্রোব দিয়ে লোড ভোল্টেজ মাপুন।",
                "৩. যদি লোড অবস্থায় ভোল্টেজ ১২ভি থেকে নেমে ৭ভি বা ৮ভিতে নেমে আসে, তবে নিশ্চিত অ্যাডাপ্টারের ভেতরের ফিল্টার ক্যাপাসিটর নষ্ট।",
                "৪. সবসময় একই ভোল্টেজ ও সমান বা বেশি অ্যাম্পিয়ার (যেমন 12V 1.5A বা 2A) এর জেনুইন অ্যাডাপ্টার ব্যবহার করুন।"
            ),
            stepsEn = listOf(
                "1. Measure no-load DC voltage with multimeter (typically 9V or 12V DC).",
                "2. Measure operating voltage across the DC jack solder pads while powering the router.",
                "3. If 12V collapses below 10.5V when WiFi starts transmitting, the adapter internal electrolytic caps have dried up.",
                "4. Replace with an authentic switching adapter meeting or exceeding the required amperage (e.g. 12V 1.5A)."
            ),
            safetyWarningBn = "সতর্কতা: কখনও ভুল ভোল্টেজের (যেমন ৯ ভোল্টের রাউটারে ২৪ ভোল্ট) অ্যাডাপ্টার লাগাবেন না, চিপ পুড়ে যাবে।",
            safetyWarningEn = "Warning: Never connect a higher voltage adapter than specified to avoid instant IC burnout."
        ),
        RepairGuide(
            id = "repair_wan_port_surge",
            titleBn = "বজ্রপাতে নষ্ট WAN পোর্ট রিপেয়ার ও ল্যান-টু-ওয়ান মোড",
            titleEn = "Surge Damaged WAN Port & LAN-to-WAN Remapping",
            type = "Hardware",
            difficulty = "Intermediate",
            toolsRequiredBn = listOf("মাল্টিমিটার", "ওপেনডাব্লিউআরটি অথবা রাউটার ওয়েব কনসোল"),
            toolsRequiredEn = listOf("Multimeter", "OpenWrt / Managed router firmware"),
            descriptionBn = "বজ্রপাত বা হাই ভোল্টেজ সার্চে রাউটারের নীল রঙের WAN পোর্ট পুড়ে গেলে ভালো থাকা LAN1 পোর্টকে WAN পোর্টে রূপান্তর করার উপায়।",
            descriptionEn = "Surge/lightning strikes often blow the WAN isolation transformer or PHY. Learn how to convert a healthy LAN port into a full WAN port.",
            stepsBn = listOf(
                "১. WAN পোর্টে কেবল লাগালে কোনো বাতি না জ্বললে বুঝবেন ট্রান্সফর্মার আইসি বা PHY নষ্ট। কিন্তু রাউটারের ভেতরের সিপিইউ ও ওয়াইফাই পুরোপুরি সচল।",
                "২. যদি রাউটারে OpenWrt বা DD-WRT থাকে, Network > Switch এ গিয়ে WAN VLAN (সাধারণত VLAN 2) কে LAN1 পোর্টের সাথে যুক্ত করুন এবং পোড়া WAN আনচেক করুন।",
                "৩. মাইক্রোটিক হলে খুব সহজ: Interfaces এ গিয়ে ether2 কে WAN বানিয়ে PPPoE Client সেটিতে ডায়াল করান।",
                "৪. সাধারণ রাউটারে যদি 'Multi-WAN' বা 'IPTV' পোর্ট থাকে, সেটি সক্রিয় করে সেটিতে ইন্টারনেট কেবল লাগান।"
            ),
            stepsEn = listOf(
                "1. If WAN LED never lights up even with a tested cable, the magnetic transformer or PHY chip was struck by surge.",
                "2. In OpenWrt: go to Network > Switch, remove the damaged physical WAN, and map LAN1 as untagged on VLAN 2.",
                "3. In MikroTik RouterOS: reassign PPPoE Client to ether2 and use ether2 as your new WAN port.",
                "4. In stock firmware: enable IPTV mode to designate LAN1/LAN2 as an independent WAN port."
            ),
            safetyWarningBn = "টিপ: এ পদ্ধতিতে শত শত ফেলে দেওয়া রাউটার পুনরায় ব্যবহারের উপযোগী করা সম্ভব।",
            safetyWarningEn = "Tech Tip: This method salvages lightning-struck routers for years of continued service."
        )
    )
}
