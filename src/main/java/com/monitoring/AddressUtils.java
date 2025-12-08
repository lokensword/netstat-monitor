package com.monitoring;

/**
 * Утилиты для работы с сетевыми адресами
 * Поддерживает извлечение порта из IPv4 и IPv6 адресов
 */
public class AddressUtils {

    /**
     * Извлечение номера порта из строки адреса
     * Обрабатывает форматы вида "192.168.1.1:8080" и "[::1]:8080"
     *
     * @param address строка с адресом и портом
     * @return номер порта или -1 при ошибке парсинга
     */
    public static int extractPort(String address) {
        if (address == null || address.isEmpty()) return -1;

        int portStart;
        if (address.startsWith("[") && address.contains("]:")) {
            portStart = address.lastIndexOf(']') + 2;
        } else {
            portStart = address.lastIndexOf(':') + 1;
        }

        if (portStart <= 0 || portStart >= address.length()) {
            return -1;
        }

        try {
            return Integer.parseInt(address.substring(portStart));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}