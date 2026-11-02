package com.example

import com.example.data.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSubnetCalculator() {
        val result = TechToolsData.calculateSubnet("192.168.1.50", 24)
        assertNotNull(result)
        assertEquals("192.168.1.0", result?.networkIp)
        assertEquals("192.168.1.255", result?.broadcastIp)
        assertEquals("255.255.255.0", result?.netmask)
        assertEquals("0.0.0.255", result?.wildcardMask)
        assertEquals("192.168.1.1", result?.firstUsableIp)
        assertEquals("192.168.1.254", result?.lastUsableIp)
        assertEquals(254L, result?.totalUsableHosts)
    }

    @Test
    fun testSubnetCalculatorPrefix29() {
        val result = TechToolsData.calculateSubnet("10.0.0.10", 29)
        assertNotNull(result)
        assertEquals("10.0.0.8", result?.networkIp)
        assertEquals("10.0.0.15", result?.broadcastIp)
        assertEquals(6L, result?.totalUsableHosts)
    }

    @Test
    fun testMikrotikGenerator() {
        val param = MikrotikParam(
            wanInterface = "ether1",
            lanInterface = "bridge-LAN",
            pppoeUser = "testuser",
            pppoePass = "testpass"
        )
        val script = MikrotikCommandGenerator.generate(MikrotikModule.PPPOE, param)
        assertTrue(script.contains("/interface pppoe-client"))
        assertTrue(script.contains("testuser"))
        assertTrue(script.contains("masquerade"))
    }

    @Test
    fun testRouterGuidesIntegrity() {
        val brands = RouterGuidesData.guides.map { it.brand }.distinct()
        assertTrue(brands.contains("TP-Link"))
        assertTrue(brands.contains("Tenda"))
        assertTrue(brands.contains("D-Link"))
        assertTrue(brands.contains("MikroTik"))
        assertTrue(brands.contains("Huawei"))
        assertTrue(brands.contains("ZTE"))
        assertTrue(brands.contains("OpenWrt"))
    }
}
