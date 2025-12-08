package com.monitoring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testValidConfig(@TempDir Path tempDir) throws IOException {
        String content = "monitor.port.check=8080\noutput.mode=VERBOSE";
        Path configPath = tempDir.resolve("app.properties");
        Files.writeString(configPath, content);

        AppConfig config = new AppConfig(configPath.toString());

        assertEquals(8080, config.getPortToCheck());
        assertEquals("VERBOSE", config.getProperty("output.mode"));
    }

    @Test
    void testDefaultPortWhenMissing(@TempDir Path tempDir) throws IOException {
        String content = "output.mode=SUMMARY";
        Path configPath = tempDir.resolve("app.properties");
        Files.writeString(configPath, content);

        AppConfig config = new AppConfig(configPath.toString());

        assertEquals(8080, config.getPortToCheck()); 
    }

    @Test
    void testInvalidPortThrowsNumberFormatException(@TempDir Path tempDir) {
        assertThrows(NumberFormatException.class, () -> {
            String content = "monitor.port.check=not_a_number";
            Path configPath = tempDir.resolve("app.properties");
            Files.writeString(configPath, content);
            new AppConfig(configPath.toString()).getPortToCheck();
        });
    }
}