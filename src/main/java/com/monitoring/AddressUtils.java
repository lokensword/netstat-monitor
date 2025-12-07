package com.monitoring;

/**
 * Класс для работы с сетевыми адресами
 */
public class AddressUtils {

    /**
     * Извлекает номер порта из строки адреса
     * @param address строка вида "192.168.1.1:8080" или "[::1]:8080"
     * @return номер порта или -1, если не удалось распарсить
     */
    public static int extractPort(String address) {
        if (address == null || address.isEmpty()) return -1;

        int portStart;
        if (address.startsWith("[") && address.contains("]:")) {
            // IPv6 - поиск после ]
            portStart = address.lastIndexOf(']') + 2; // +2: "]:" → начало порта
        } else {
            // IPv4 - после :
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