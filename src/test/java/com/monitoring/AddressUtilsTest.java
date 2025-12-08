package com.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressUtilsTest {

    @Test
    void testExtractPortIpv4() {
        assertEquals(8080, AddressUtils.extractPort("192.168.1.10:8080"));
        assertEquals(135, AddressUtils.extractPort("0.0.0.0:135"));
    }

    @Test
    void testExtractPortIpv6() {
        assertEquals(8080, AddressUtils.extractPort("[::1]:8080"));
        assertEquals(443, AddressUtils.extractPort("[2001:db8::1]:443"));
    }

    @Test
    void testExtractPortInvalid() {
        assertEquals(-1, AddressUtils.extractPort("invalid"));
        assertEquals(-1, AddressUtils.extractPort("192.168.1.1")); 
        assertEquals(-1, AddressUtils.extractPort("[::1]")); 
    }
}