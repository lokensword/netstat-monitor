package com.monitoring;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс для управления конфигурацией приложения
 * Загружает настройки из app.properties
 */
public class AppConfig {
    private Properties properties = new Properties();

    public AppConfig(String configPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getPortToCheck() {
        return Integer.parseInt(getProperty("monitor.port.check", "8080"));
    }
}