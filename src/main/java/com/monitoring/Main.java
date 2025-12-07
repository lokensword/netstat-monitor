package com.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Файл не найден - программа упадет (что логично, конфиг обязателен по заданию)
            AppConfig config = new AppConfig("app.properties");
            int portToCheck = config.getPortToCheck();
            System.out.println("Config loaded. Checking port: " + portToCheck);

            // Получение данных
            CommandExecutor executor = new CommandExecutor();
            List<String> rawOutput = executor.executeNetstat();

            // Парсинг
            NetstatParser parser = new NetstatParser();
            List<ConnectionInfo> connections = parser.parse(rawOutput);

            Map<String, String> pidToProcess = ProcessNameResolver.resolveProcessNames(connections);

			// Обновление ConnectionInfo для Windows
			for (ConnectionInfo info : connections) {
				if (!"UNKNOWN".equals(info.getPid())) {
					String resolvedName = pidToProcess.get(info.getPid());
					if (resolvedName != null) {
						info.setProcessName(resolvedName);
					}
				}
			}

			// Задание 1: LISTEN
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

			// Задание 2: ESTABLISHED
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

			// Задание 3: Проверка порта
			System.out.println("\n--- Port " + portToCheck + " Check ---");
			boolean isBusy = false;
			for (ConnectionInfo info : connections) {
				int port = AddressUtils.extractPort(info.getLocalAddress());
				if (port == portToCheck) {
					System.out.printf("Port %d is in use by PID: %s (%s)%n", 
						portToCheck, info.getPid(), info.getProcessName());
					isBusy = true;
					break; // достаточно одного совпадения
				}
			}
			if (!isBusy) {
				System.out.println("Port " + portToCheck + " is not in use.");
			};
			}
		
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}