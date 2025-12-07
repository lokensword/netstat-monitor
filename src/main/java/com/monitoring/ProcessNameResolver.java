package com.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Класс для разрешения имени процесса по PID на разных ОС
 * На Linux имена уже доступны из netstat
 * На Windows требуется дополнительный вызов tasklist
 */
public class ProcessNameResolver {

    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS.contains("win");

    /**
     * Возвращает отображение PID - имя процесса
     * На Windows выполняет tasklist для всех уникальных PID
     * На Linux использует уже существующие имена из ConnectionInfo
     *
     * @param connections список соединений после парсинга netstat
     * @return мапа PID (строка) -> имя процесса (без .exe для windows)
     */
    public static Map<String, String> resolveProcessNames(List<ConnectionInfo> connections) {
        if (!IS_WINDOWS) {
            // Linux: имена уже есть в объектах
            Map<String, String> map = new HashMap<>();
            for (ConnectionInfo c : connections) {
                if (!"UNKNOWN".equals(c.getPid()) && !"Unknown".equals(c.getProcessName())) {
                    map.put(c.getPid(), c.getProcessName());
                }
            }
            return map;
        }

        // Windows - собрать уникальные PID
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
				String line = reader.readLine(); // только первая строка - нужный процесс
				if (line != null && line.startsWith("\"")) {
					String[] parts = parseCsvLine(line);
					if (parts.length >= 1) {
						return parts[0].replaceAll("\\.exe$", "");
					}
				}
			}
		} catch (IOException e) {
			// Ошибка - возврат null
		}
		return null;
	}

    // CSV-парсер для кавычек
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