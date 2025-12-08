package com.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
    try {
        // Загрузка конфигурации
        AppConfig config = new AppConfig("app.properties");
        int portToCheck = config.getPortToCheck();

        // Выполнение netstat
        CommandExecutor executor = new CommandExecutor();
        List<String> rawOutput = executor.executeNetstat();

        // Парсинг
        NetstatParser parser = new NetstatParser();
        List<ConnectionInfo> connections = parser.parse(rawOutput);

        // Разрешение имён процессов
        Map<String, String> pidToProcess = ProcessNameResolver.resolveProcessNames(connections);
        for (ConnectionInfo info : connections) {
            if (!"UNKNOWN".equals(info.getPid())) {
                String resolvedName = pidToProcess.get(info.getPid());
                if (resolvedName != null) {
                    info.setProcessName(resolvedName);
                }
            }
        }

        // === Чтение режима вывода ===
		String outputMode = config.getProperty("output.mode", "VERBOSE").trim().toUpperCase();

		if ("VERBOSE".equals(outputMode)) {
			// --- Listening Ports ---
			System.out.println("\n--- Listening Ports ---");
			boolean hasListening = false;
			for (ConnectionInfo info : connections) {
				if ("LISTENING".equalsIgnoreCase(info.getState()) || "LISTEN".equalsIgnoreCase(info.getState())) {
					int port = AddressUtils.extractPort(info.getLocalAddress());
					System.out.printf("PID: %s, Process: %s, Port: %d%n", 
						info.getPid(), info.getProcessName(), port);
					hasListening = true;
				}
			}
			if (!hasListening) {
				System.out.println("No listening ports found.");
			}

			// --- External Connections ---
			System.out.println("\n--- External Connections ---");
			boolean hasExternal = false;
			for (ConnectionInfo info : connections) {
				if ("ESTABLISHED".equalsIgnoreCase(info.getState())) {
					System.out.printf("PID: %s, Process: %s, Foreign: %s%n", 
						info.getPid(), info.getProcessName(), info.getForeignAddress());
					hasExternal = true;
				}
			}
			if (!hasExternal) {
				System.out.println("No external connections found.");
			}
		}

		// --- Port Check (всегда выполняется, но вывод зависит от режима) ---
		System.out.println("\n--- Port " + portToCheck + " Check ---");
		boolean isBusy = false;
		for (ConnectionInfo info : connections) {
			int port = AddressUtils.extractPort(info.getLocalAddress());
			if (port == portToCheck) {
				System.out.printf("Port %d is in use by PID: %s (%s)%n", 
					portToCheck, info.getPid(), info.getProcessName());
				isBusy = true;
				break;
			}
		}

		// В режиме SUMMARY выводим только если порт занят
		if ("SUMMARY".equals(outputMode)) {
			if (!isBusy) {
				// Нет проблем → ничего не выводим (или минимальное сообщение по желанию)
				System.out.println("No issues detected.");
			}
		} else {
			// VERBOSE: всегда показываем статус порта
			if (!isBusy) {
				System.out.println("Port " + portToCheck + " is not in use.");
			}
		}

    } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
}
}