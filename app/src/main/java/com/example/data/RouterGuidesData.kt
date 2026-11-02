package com.example.data

data class RouterGuide(
    val id: String,
    val brand: String,
    val titleBn: String,
    val titleEn: String,
    val defaultIp: String,
    val defaultCredentials: String,
    val popularModels: String,
    val overviewBn: String,
    val overviewEn: String,
    val stepsBn: List<String>,
    val stepsEn: List<String>,
    val technicianTipBn: String,
    val technicianTipEn: String
) {
    fun title(): String = str(titleBn, titleEn)
    fun overview(): String = str(overviewBn, overviewEn)
    fun steps(): List<String> = if (LanguageManager.currentLanguage == AppLanguage.BN) stepsBn else stepsEn
    fun tip(): String = str(technicianTipBn, technicianTipEn)
}

object RouterGuidesData {
    val guides = listOf(
        RouterGuide(
            id = "guide_tplink_pppoe",
            brand = "TP-Link",
            titleBn = "TP-Link রাউটারে PPPoE ইন্টারনেট কনফিগারেশন",
            titleEn = "TP-Link Router PPPoE Internet Setup",
            defaultIp = "192.168.0.1 / tplinkwifi.net",
            defaultCredentials = "admin / admin (বা নতুন পাসওয়ার্ড)",
            popularModels = "Archer C6, C80, WR840N, WR841N, WR845N",
            overviewBn = "বাংলাদেশের যেকোনো আইএসপি (যেমন Link3, AmberIT, Carnival, ICC) এর জন্য TP-Link রাউটারে ব্রডব্যান্ড PPPoE ডায়াল-আপ সেটআপ পদ্ধতি।",
            overviewEn = "Complete guide to configure PPPoE broadband connection on TP-Link routers for any Bangladesh ISP.",
            stepsBn = listOf(
                "প্রথমে ONU এর LAN পোর্ট থেকে ক্যাট৬ কেবল দিয়ে রাউটারের নীল রঙের WAN পোর্টে সংযোগ দিন।",
                "মোবাইল বা পিসি দিয়ে রাউটারের ডিফল্ট ওয়াইফাই অথবা LAN পোর্টে কানেক্ট করুন।",
                "ব্রাউজার খুলে টাইপ করুন: 192.168.0.1 অথবা tplinkwifi.net এবং অ্যাডমিন পাসওয়ার্ড সেট করুন।",
                "মেনু থেকে Network > WAN অথবা Quick Setup এ ক্লিক করুন।",
                "WAN Connection Type হিসেবে 'PPPoE / Russia PPPoE' নির্বাচন করুন।",
                "আইএসপি থেকে দেওয়া Username এবং Password নির্ভুলভাবে লিখুন। Secondary Connection 'Disabled' রাখুন।",
                "Wireless 2.4GHz / 5GHz অপশনে গিয়ে আপনার পছন্দের ওয়াইফাই নাম (SSID) ও WPA2-PSK পাসওয়ার্ড দিন।",
                "Save এ ক্লিক করুন এবং রাউটারটি রিবুট হলে স্ট্যাটাস পেজে দেখুন WAN IP পেয়েছে কি না।"
            ),
            stepsEn = listOf(
                "Connect the Ethernet cable from the ONU LAN port to the blue WAN port of the router.",
                "Connect your mobile or PC to the default WiFi or LAN port of the router.",
                "Open browser and enter 192.168.0.1 or tplinkwifi.net, then set an admin password.",
                "Go to Network > WAN or click Quick Setup.",
                "Select WAN Connection Type as 'PPPoE / Russia PPPoE'.",
                "Enter the ISP provided Username and Password accurately. Keep Secondary Connection Disabled.",
                "Go to Wireless 2.4GHz/5GHz and set your preferred WiFi SSID and WPA2-PSK password.",
                "Click Save. Once rebooted, verify if the WAN IP address is received on the Status page."
            ),
            technicianTipBn = "টিপ: অনেক সময় আইএসপি ম্যাক বাইন্ডিং করে রাখে। নেট না পেলে Network > MAC Clone এ গিয়ে 'Clone Current Device MAC' দিয়ে Save দিন।",
            technicianTipEn = "Tech Tip: If internet does not connect, check if ISP requires MAC Binding. Go to Network > MAC Clone and click 'Clone Current Device MAC'."
        ),
        RouterGuide(
            id = "guide_tenda_pppoe",
            brand = "Tenda",
            titleBn = "Tenda রাউটারে সম্পূর্ণ PPPoE ও ওয়াইফাই সেটআপ",
            titleEn = "Tenda Router PPPoE & WiFi Configuration",
            defaultIp = "192.168.0.1 / tendawifi.com",
            defaultCredentials = "পাসওয়ার্ড ফাঁকা / admin",
            popularModels = "Tenda F3, F6, AC10, TX2 Pro, TX12, AC23",
            overviewBn = "Tenda রাউটারে দ্রুত ব্রডব্যান্ড সেটআপ, সিগন্যাল বুস্ট এবং রিপিটার মোড সক্রিয় করার নিয়ম।",
            overviewEn = "Fast setup guide for Tenda routers including PPPoE, WiFi boost and repeater mode.",
            stepsBn = listOf(
                "ONU থেকে আসা তারটি Tenda রাউটারের WAN পোর্টে লাগান।",
                "Tenda এর ডিফল্ট ওপেন ওয়াইফাইতে কানেক্ট করুন।",
                "ব্রাউজারে tendawifi.com অথবা 192.168.0.1 ওপেন করলে সেটআপ উইজার্ড আসবে।",
                "Connection Type এ 'PPPoE' সিলেক্ট করে আইএসপির দেওয়া ইউজারনেম ও পাসওয়ার্ড দিন।",
                "WiFi Name (SSID) এবং WiFi Password ফিল্ডে নতুন নাম ও পাসওয়ার্ড লিখুন।",
                "লগইন পাসওয়ার্ড সুরক্ষার জন্য 'Set login password' চেকবক্স টিক দিয়ে অ্যাডমিন পাসওয়ার্ড দিন।",
                "'OK' বাটনে ক্লিক করুন। রাউটার সেভ হয়ে রিবুট হবে এবং নতুন ওয়াইফাইতে কানেক্ট করুন।"
            ),
            stepsEn = listOf(
                "Connect the cable from ONU to the Tenda router WAN port.",
                "Connect to the default unencrypted Tenda WiFi network.",
                "Navigate to tendawifi.com or 192.168.0.1 to launch the setup wizard.",
                "Select 'PPPoE' as connection type and input the ISP credentials.",
                "Configure your WiFi Name (SSID) and WiFi Password.",
                "Optionally enable 'Set login password' to protect the router web admin.",
                "Click 'OK'. The router will apply settings and reboot."
            ),
            technicianTipBn = "টিপ: ওয়াইফাই সিগন্যাল দুর্বল হলে Wireless Parameters এ গিয়ে 'Transmit Power' অপশন 'High' সিলেক্ট করুন।",
            technicianTipEn = "Tech Tip: For maximum coverage, navigate to Wireless Parameters and ensure Transmit Power is set to 'High'."
        ),
        RouterGuide(
            id = "guide_dlink_pppoe",
            brand = "D-Link",
            titleBn = "D-Link রাউটার কনফিগারেশন ও VLAN ট্যাগিং",
            titleEn = "D-Link Router Setup & VLAN Tagging",
            defaultIp = "192.168.0.1 / 192.168.1.1",
            defaultCredentials = "admin / (ফাঁকা অথবা admin)",
            popularModels = "DIR-615, DIR-825, DIR-842, DIR-853",
            overviewBn = "D-Link রাউটার কনফিগারেশন এবং যেসব আইএসপিতে VLAN ট্যাগ প্রয়োজন তাদের জন্য ট্রিপল-প্লে সেটআপ।",
            overviewEn = "D-Link router setup including VLAN tagging for ISPs requiring tagged PPPoE traffic.",
            stepsBn = listOf(
                "রাউটারের WAN পোর্টে কেবল যুক্ত করে 192.168.0.1 এ প্রবেশ করুন।",
                "ডিফল্ট ইউজারনেম admin এবং পাসওয়ার্ড ফাঁকা রেখে লগইন করুন।",
                "Settings > Internet অথবা Setup Wizard এ প্রবেশ করুন।",
                "IPv4 WAN Connection এ 'PPPoE' নির্বাচন করুন।",
                "আইএসপির ইউজার ও পাসওয়ার্ড দিন। কানেকশন মোড 'Always On' রাখুন।",
                "যদি আইএসপি VLAN আইডি চায়, তবে 'Enable VLAN' দিয়ে ইন্টারনেট VLAN ID লিখুন (যেমন: 100 বা 500)।",
                "Wireless Settings এ 2.4GHz ও 5GHz এর আলাদা নাম ও পাসওয়ার্ড দিয়ে Apply করুন।"
            ),
            stepsEn = listOf(
                "Connect WAN cable and access 192.168.0.1 in your browser.",
                "Login with username 'admin' and leave password blank (or use admin).",
                "Go to Settings > Internet or Setup Wizard.",
                "Choose IPv4 WAN connection type as 'PPPoE'.",
                "Enter ISP user credentials and set connection mode to Always On.",
                "If ISP requires VLAN tagging, enable VLAN and enter the designated VLAN ID.",
                "Set wireless names and WPA2 AES passwords, then apply changes."
            ),
            technicianTipBn = "টিপ: D-Link এর MTU ডিফল্ট 1492 থাকে। যদি কোনো ওয়েবসাইট না লোড হয় তবে MTU 1480 সেট করে দেখুন।",
            technicianTipEn = "Tech Tip: Default MTU is 1492. If certain websites stall, try reducing WAN MTU to 1480."
        ),
        RouterGuide(
            id = "guide_mikrotik_basics",
            brand = "MikroTik",
            titleBn = "MikroTik RouterOS বেসিক আইএসপি সেটআপ (Winbox)",
            titleEn = "MikroTik RouterOS Basic ISP Setup (Winbox)",
            defaultIp = "192.168.88.1",
            defaultCredentials = "admin / (পাসওয়ার্ড ফাঁকা)",
            popularModels = "hEX (RB750Gr3), hAP ac2, hAP ax2, RB951Ui, CCR1009",
            overviewBn = "উইনবক্স (Winbox) ব্যবহার করে মাইক্রোটিক রাউটারে WAN PPPoE ক্লায়েন্ট, ব্রিজ, DHCP সার্ভার এবং NAT মাসকারেড তৈরি করার প্রফেশনাল গাইড।",
            overviewEn = "Professional step-by-step guide to configure MikroTik RouterOS with PPPoE Client, LAN Bridge, DHCP Server and NAT Masquerade using Winbox.",
            stepsBn = listOf(
                "উইনবক্স চালু করে Neighbors ট্যাব থেকে MikroTik এর MAC অ্যাড্রেসে ক্লিক করে Connect করুন।",
                "Interfaces এ গিয়ে ether1 এর নাম দিন 'ether1-WAN' এবং বাকি পোর্টগুলোকে 'bridge-LAN' এ যোগ করুন।",
                "PPP মেনু থেকে '+' এ ক্লিক করে 'PPPoE Client' যোগ করুন। Interfaces দিন 'ether1-WAN', Dial Out ট্যাবে গিয়ে ISP User ও Password দিন এবং 'Use Peer DNS' ও 'Add Default Route' টিক দিন।",
                "IP > Addresses এ গিয়ে লোকাল ব্রিজে আইপি দিন: 192.168.88.1/24 (Interface: bridge-LAN)।",
                "IP > Pool এ গিয়ে লোকাল আইপি রেঞ্জ তৈরি করুন: 192.168.88.10-192.168.88.254।",
                "IP > DHCP Server এ গিয়ে 'DHCP Setup' বাটনে ক্লিক করে bridge-LAN নির্বাচন করে নেক্সট নেক্সট দিয়ে সার্ভার চালু করুন।",
                "IP > Firewall > NAT এ গিয়ে '+' দিন। Chain: srcnat, Out. Interface: pppoe-out1, Action: masquerade দিন।"
            ),
            stepsEn = listOf(
                "Launch Winbox, go to Neighbors tab, click on the router's MAC address and connect.",
                "In Interfaces, rename ether1 to 'ether1-WAN' and group ether2-ether5 under 'bridge-LAN'.",
                "Navigate to PPP > click '+' > PPPoE Client. Select ether1-WAN, enter ISP User/Pass, tick 'Add Default Route' and 'Use Peer DNS'.",
                "Go to IP > Addresses, add IP: 192.168.88.1/24 to interface 'bridge-LAN'.",
                "Go to IP > Pool, add IP pool: 192.168.88.10-192.168.88.254.",
                "Go to IP > DHCP Server > click 'DHCP Setup', select bridge-LAN and follow wizard.",
                "Go to IP > Firewall > NAT > click '+'. Set Chain: srcnat, Out. Interface: pppoe-out1, Action: masquerade."
            ),
            technicianTipBn = "টিপ: সেটআপ শেষে IP > DNS এ গিয়ে 'Allow Remote Requests' অপশনটি অবশ্যই আনচেক রাখুন, অন্যথায় বাইরে থেকে DNS অ্যাটাক হতে পারে।",
            technicianTipEn = "Tech Tip: Keep 'Allow Remote Requests' disabled in IP > DNS unless firewall rules specifically drop external port 53 traffic to prevent open DNS resolver DDoS attacks."
        ),
        RouterGuide(
            id = "guide_huawei_onu",
            brand = "Huawei",
            titleBn = "Huawei XPON ONU / রাউটার কনফিগারেশন",
            titleEn = "Huawei XPON ONU / Router Setup",
            defaultIp = "192.168.100.1 / 192.168.1.1",
            defaultCredentials = "telecomadmin / admintelecom (সুপার অ্যাডমিন)",
            popularModels = "EG8141A5, HG8245H, HG8145V5, HG8546M",
            overviewBn = "Huawei অপটিক্যাল নেটওয়ার্ক ইউনিট (ONU) এর সুপার অ্যাডমিন লগইন, PPPoE WAN প্রোফাইল এবং অপটিক্যাল পাওয়ার পরীক্ষা।",
            overviewEn = "Configuration guide for Huawei XPON ONUs including superadmin login, WAN PPPoE with VLAN and optical Rx power inspection.",
            stepsBn = listOf(
                "পিসি বা ফোনের ব্রাউজারে 192.168.100.1 এ প্রবেশ করুন।",
                "সুপার অ্যাডমিন ইউজারনেম 'telecomadmin' এবং পাসওয়ার্ড 'admintelecom' দিয়ে লগইন করুন।",
                "WAN ট্যাবে গিয়ে 'New' বাটনে ক্লিক করুন।",
                "Encapsulation Mode: IPoE/PPPoE থেকে 'PPPoE' সিলেক্ট করুন। Protocol Type: IPv4।",
                "WAN Mode: 'Route WAN', Service Type: 'INTERNET' নির্বাচন করুন।",
                "Enable VLAN টিক দিন এবং আইএসপি দেওয়া VLAN ID ও 802.1p প্রাইওরিটি লিখুন (যেমন: VLAN 100)।",
                "Username ও Password ফিল্ডে আইএসপি ব্রডব্যান্ড ক্রেডেনশিয়াল দিন।",
                "Binding Options এ গিয়ে LAN1 এবং WLAN1 টিক দিয়ে 'Apply' করুন।",
                "Status > Optical Information এ গিয়ে দেখে নিন Rx Optical Power -8 dBm থেকে -25 dBm এর মধ্যে আছে কি না।"
            ),
            stepsEn = listOf(
                "Open browser and enter 192.168.100.1.",
                "Login with superadmin credentials 'telecomadmin' / 'admintelecom'.",
                "Navigate to the WAN tab and click 'New'.",
                "Set Encapsulation to PPPoE, Protocol to IPv4, and WAN Mode to 'Route WAN'.",
                "Set Service Type to 'INTERNET'. Enable VLAN and input the ISP VLAN ID.",
                "Enter PPPoE username and password.",
                "Check LAN1 and SSID1 in the port binding section, then click Apply.",
                "Check Status > Optical Information to confirm Rx Optical Power is between -8 dBm and -25 dBm."
            ),
            technicianTipBn = "টিপ: অপটিক্যাল পাওয়ার যদি -27 dBm এর চেয়ে কম (যেমন -29 বা -32 dBm) দেখায় তবে ফাইবার প্যাচ কর্ড বাঁকানো বা ময়লা আছে কি না চেক করুন।",
            technicianTipEn = "Tech Tip: If Rx power is weaker than -27 dBm, inspect the blue SC-UPC connector and check for bent fiber patch cables."
        ),
        RouterGuide(
            id = "guide_zte_onu",
            brand = "ZTE",
            titleBn = "ZTE XPON / EPON ONU কনফিগারেশন ও পোর্ট বাইন্ডিং",
            titleEn = "ZTE XPON / EPON ONU Configuration",
            defaultIp = "192.168.1.1",
            defaultCredentials = "admin / admin অথবা user / user",
            popularModels = "F660, F670L, F609, ZXHN F623",
            overviewBn = "ZTE রাউটার ও ONU তে ইন্টারনেট WAN প্রোফাইল তৈরি, ব্রিজ মোড এবং পোর্ট ম্যাপিং কনফিগারেশন।",
            overviewEn = "ZTE ONU configuration guide for WAN profiles, bridge mode, and port mapping.",
            stepsBn = listOf(
                "192.168.1.1 এ ব্রাউজ করে অ্যাডমিন ক্রেডেনশিয়াল দিয়ে লগইন করুন।",
                "Network > WAN > WAN Connection এ যান।",
                "Connection Name এ 'Create WAN Connection' নির্বাচন করুন।",
                "Type: 'Route' এবং Link Type: 'PPP' নির্বাচন করুন।",
                "VLAN: যদি আইএসপিতে প্রয়োজন হয় তবে VLAN Tagging এনেবল করে VLAN ID দিন।",
                "Username এবং Password দিয়ে 'Add' বা 'Modify' বাটনে চাপ দিন।",
                "Network > WAN > Port Binding এ গিয়ে নবনির্মিত WAN প্রোফাইলটির সাথে LAN1 এবং SSID1 টিক দিন।",
                "Administration > System Management থেকে রাউটারটি রিবুট দিন।"
            ),
            stepsEn = listOf(
                "Browse to 192.168.1.1 and log in with admin credentials.",
                "Navigate to Network > WAN > WAN Connection.",
                "Choose 'Create WAN Connection'.",
                "Set Mode to Route and Link Type to PPP.",
                "Configure VLAN tagging if demanded by the ISP.",
                "Enter the broadband username and password and click Add/Modify.",
                "Go to Port Binding and map the newly created WAN profile to LAN1 and WiFi.",
                "Reboot the device from Administration > System Management."
            ),
            technicianTipBn = "টিপ: যদি ZTE ONU কে শুধু ব্রিজ করে পেছনে MikroTik বা অন্য রাউটার চালাতে চান, তবে WAN Type 'Bridge' দিয়ে VLAN Transparent অথবা Tagging সিলেক্ট করুন।",
            technicianTipEn = "Tech Tip: To run ZTE in pure Bridge mode for an external MikroTik, change WAN type to Bridge with transparent VLAN."
        ),
        RouterGuide(
            id = "guide_openwrt_setup",
            brand = "OpenWrt",
            titleBn = "OpenWrt ফার্মওয়্যার সেটআপ ও বাফারব্লোট সমাধান",
            titleEn = "OpenWrt Firmware Setup & SQM QoS",
            defaultIp = "192.168.1.1",
            defaultCredentials = "root / (পাসওয়ার্ড ফাঁকা)",
            popularModels = "Xiaomi Mi 4A Gigabit, Archer C6 v2/v3, x86 Mini PC, Linksys",
            overviewBn = "লিনাক্স-ভিত্তিক ওপেন সোর্স ফার্মওয়্যার OpenWrt তে LuCI ওয়েব ইন্টারফেস দিয়ে ব্রডব্যান্ড সেটআপ এবং SQM (Cake) দিয়ে পিং অপ্টিমাইজেশন।",
            overviewEn = "Complete guide to configure open source OpenWrt with LuCI web interface, PPPoE WAN and SQM QoS to fix gaming latency bufferbloat.",
            stepsBn = listOf(
                "ব্রাউজারে 192.168.1.1 এ যান এবং 'Login' এ ক্লিক করুন। কোনো ডিফল্ট পাসওয়ার্ড থাকে না।",
                "System > Administration এ গিয়ে প্রথমেই একটি শক্তিশালী রুট পাসওয়ার্ড সেট করুন।",
                "Network > Interfaces এ গিয়ে 'WAN' ইন্টারফেসের 'Edit' বাটনে ক্লিক করুন।",
                "Protocol ড্রপডাউন থেকে 'PPPoE' নির্বাচন করে 'Switch protocol' এ চাপ দিন।",
                "PAP/CHAP username এবং password ফিল্ডে আপনার আইএসপি তথ্য লিখুন এবং 'Save & Apply' দিন।",
                "Network > Wireless এ গিয়ে রেডিও এনেবল করুন এবং Edit করে WPA2/WPA3 এনক্রিপশন দিন।",
                "বাফারব্লোট ও গেমিং পিং কমানোর জন্য Software > sqm-scripts ইনস্টল করে Network > SQM এ যান। Download/Upload এর 90% স্পিড এবং 'Cake / piece_of_cake' নির্বাচন করুন।"
            ),
            stepsEn = listOf(
                "Browse to 192.168.1.1 and click Login without a password.",
                "Go to System > Administration and set a secure root password immediately.",
                "Go to Network > Interfaces and click 'Edit' on the WAN interface.",
                "Change Protocol to 'PPPoE' and click 'Switch protocol'.",
                "Enter your ISP username and password, then click 'Save & Apply'.",
                "Navigate to Network > Wireless, enable the radios and configure WPA2/WPA3 security.",
                "To fix bufferbloat and gaming lag, install luci-app-sqm, configure upload/download to 90% of your subscribed speed, and use Cake / piece_of_cake."
            ),
            technicianTipBn = "টিপ: OpenWrt দিয়ে যেকোনো সাধারণ রাউটারকে এন্টারপ্রাইজ গ্রেড রাউটার বা ডাম্ব এক্সেস পয়েন্ট (Dumb AP) এ রূপান্তর করা যায়।",
            technicianTipEn = "Tech Tip: OpenWrt excels at turning consumer routers into carrier-grade firewalls with SQM Cake queuing."
        )
    )
}
