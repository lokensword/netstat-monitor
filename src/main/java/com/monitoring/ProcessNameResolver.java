package com.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Разрешение имени процесса по PID
 * На Linux использует данные из netstat
 * На Windows вызывает tasklist для каждого PID
 */
public class ProcessNameResolver {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS.contains("win");

    /**
     * Построение карты PID → имя процесса
     *
     * @param connections список соединений после парсинга
     * @return карта соответствий PID и имён процессов
     */
    public static Map<String, String> resolveProcessNames(List<ConnectionInfo> connections) {
        if (!IS_WINDOWS) {
            Map<String, String> map = new HashMap<>();
            for (ConnectionInfo c : connections) {
                if (!"UNKNOWN".equals(c.getPid()) && !"Unknown".equals(c.getProcessName())) {
                    map.put(c.getPid(), c.getProcessName());
                }
            }
            return map;
        }

        Set<String> pids = new HashSet<>();
        for (ConnectionInfo c : connections) {
            if (!"UNKNOWN".equals(c.getPid())) {
                pids.add(c.getPid());
            }
        }

        return getProcessNamesForPidsWindows(pids);
    }

    private static Map<String, String> getProcessNamesForPidsWindows(Set<String> pids) {
        Map<String, String> result = new HashMap<>();
        if (pids.isEmpty()) return result;

        for (String pid : pids) {
            String name = getProcessNameForPid(pid);
            result.put(pid, name != null ? name : "N/A");
        }

        return result;
    }

    private static String getProcessNameForPid(String pid) {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist", "/NH", "/FO", "CSV", "/FI", "PID eq " + pid);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.startsWith("\"")) {
                    String[] parts = parseCsvLine(line);
                    if (parts.length >= 1) {
                        return parts[0].replaceAll("\\.exe$", "");
                    }
                }
            }
        } catch (IOException e) {
            // игнор ошибки вызова tasklist
        }
        return null;
    }

    // Парсинг CSV без экранирования
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}