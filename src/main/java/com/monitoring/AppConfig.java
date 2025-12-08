package com.monitoring;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Загружает и предоставляет доступ к настройкам приложения из файла конфигурации
 * Обязательно требует наличие файла {@code app.properties} с ключом {@code monitor.port.check}
 */
public class AppConfig {
    private final Properties properties = new Properties();

    /**
     * Создаёт объект конфигурации и загружает настройки из указанного файла
     *
     * @param configPath путь к файлу конфигурации (например, "app.properties")
     * @throws IOException если файл не найден или не может быть прочитан
     */
    public AppConfig(String configPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(configPath)) {
            properties.load(fis);
        }
    }

    /**
     * Возвращает значение свойства по ключу
     *
     * @param key ключ свойства
     * @return значение свойства или {@code null}, если ключ не найден
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Возвращает значение свойства по ключу или значение по умолчанию, если ключ отсутствует
     *
     * @param key ключ свойства
     * @param defaultValue значение по умолчанию
     * @return значение свойства или {@code defaultValue}, если ключ не найден
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Возвращает номер порта, указанный в свойстве {@code monitor.port.check}
     * Если свойство отсутствует, используется значение по умолчанию — 8080
     *
     * @return номер порта для проверки
     * @throws NumberFormatException если значение свойства не является целым числом
     */
    public int getPortToCheck() {
        return Integer.parseInt(getProperty("monitor.port.check", "8080"));
    }
}