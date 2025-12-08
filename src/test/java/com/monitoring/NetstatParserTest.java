package com.monitoring;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NetstatParserTest {

    @Test
    void testParseLinuxOutput() {
        String[] lines = {
            "Active Internet connections (servers and established)",
            "Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name",
            "tcp        0      0 127.0.0.1:3306          0.0.0.0:*               LISTEN      1234/mysqld",
            "tcp        0      0 192.168.1.10:1234       104.18.3.10:443         ESTABLISHED 5678/java"
        };

        NetstatParser parser = new NetstatParser();
        List<ConnectionInfo> result = parser.parse(List.of(lines));

        assertEquals(2, result.size());

        ConnectionInfo listen = result.get(0);
        assertEquals("tcp", listen.getProtocol());
        assertEquals("127.0.0.1:3306", listen.getLocalAddress());
        assertEquals("LISTEN", listen.getState());
        assertEquals("1234", listen.getPid());
        assertEquals("mysqld", listen.getProcessName());

        ConnectionInfo est = result.get(1);
        assertEquals("ESTABLISHED", est.getState());
        assertEquals("104.18.3.10:443", est.getForeignAddress());
        assertEquals("5678", est.getPid());
        assertEquals("java", est.getProcessName());
    }

    @Test
    void testParseWindowsOutput() {
        String[] lines = {
            "  Proto  Local Address          Foreign Address        State           PID",
            "  TCP    0.0.0.0:135            0.0.0.0:0              LISTENING       1216",
            "  TCP    192.168.0.141:49665    20.189.173.15:443      ESTABLISHED     784"
        };

        NetstatParser parser = new NetstatParser();
        List<ConnectionInfo> result = parser.parse(List.of(lines));

        assertEquals(2, result.size());

        ConnectionInfo listen = result.get(0);
        assertEquals("TCP", listen.getProtocol());
        assertEquals("0.0.0.0:135", listen.getLocalAddress());
        assertEquals("LISTENING", listen.getState());
        assertEquals("1216", listen.getPid());
        assertEquals("Unknown", listen.getProcessName()); // будет заменён позже

        ConnectionInfo est = result.get(1);
        assertEquals("ESTABLISHED", est.getState());
        assertEquals("20.189.173.15:443", est.getForeignAddress());
        assertEquals("784", est.getPid());
    }

    @Test
    void testIgnoresHeaderAndEmptyLines() {
        String[] lines = {
            "",
            "Active Internet connections (servers and established)",
            "Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name",
            "",
            "tcp        0      0 127.0.0.1:8080          0.0.0.0:*               LISTEN      9999/nginx"
        };

        NetstatParser parser = new NetstatParser();
        List<ConnectionInfo> result = parser.parse(List.of(lines));

        assertEquals(1, result.size());
        assertEquals("nginx", result.get(0).getProcessName());
    }
}