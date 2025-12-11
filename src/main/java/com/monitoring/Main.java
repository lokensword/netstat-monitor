package com.monitoring;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        boolean isCI = "true".equalsIgnoreCase(System.getenv("CI"));

        try {
            AppConfig config = new AppConfig("app.properties");
            int intervalSeconds = config.getMonitoringIntervalSeconds();
            int portToCheck = config.getPortToCheck();
            String outputMode = config.getProperty("output.mode", "VERBOSE").trim().toUpperCase();

            if (isCI) {
                // CI: одна итерация и выход
                performIteration(config, portToCheck, outputMode);
                System.out.println("CI run completed successfully.");
                return; // main завершается → exit code 0
            }

            // Обычный режим: бесконечный цикл
            while (true) {
                performIteration(config, portToCheck, outputMode);
                Thread.sleep(intervalSeconds * 1000L);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Monitoring interrupted: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // явный код ошибки при сбое
        }
    }

    private static void performIteration(AppConfig config, int portToCheck, String outputMode) {
        // Опционально: очистка экрана 
        System.out.print("\033[2J\033[H");

        try {
            // === Выполнение netstat ===
            CommandExecutor executor = new CommandExecutor();
            List<String> rawOutput = executor.executeNetstat();

            // === Парсинг ===
            NetstatParser parser = new NetstatParser();
            List<ConnectionInfo> connections = parser.parse(rawOutput);

            // === Разрешение имён процессов ===
            Map<String, String> pidToProcess = ProcessNameResolver.resolveProcessNames(connections);
            for (ConnectionInfo info : connections) {
                if (!"UNKNOWN".equals(info.getPid())) {
                    String resolvedName = pidToProcess.get(info.getPid());
                    if (resolvedName != null) {
                        info.setProcessName(resolvedName);
                    }
                }
            }

            // === Вывод согласно режиму ===
            boolean hasListening = false;
            boolean hasExternal = false;

            if ("VERBOSE".equals(outputMode)) {
                System.out.println("\n--- Listening Ports ---");
                for (ConnectionInfo info : connections) {
                    if ("LISTENING".equalsIgnoreCase(info.getState()) || "LISTEN".equalsIgnoreCase(info.getState())) {
                        int port = AddressUtils.extractPort(info.getLocalAddress());
                        System.out.printf("PID: %s, Process: %s, Port: %d%n", info.getPid(), info.getProcessName(), port);
                        hasListening = true;
                    }
                }
                if (!hasListening) {
                    System.out.println("No listening ports found.");
                }

                System.out.println("\n--- External Connections ---");
                for (ConnectionInfo info : connections) {
                    if ("ESTABLISHED".equalsIgnoreCase(info.getState())) {
                        System.out.printf("PID: %s, Process: %s, Foreign: %s%n", info.getPid(), info.getProcessName(), info.getForeignAddress());
                        hasExternal = true;
                    }
                }
                if (!hasExternal) {
                    System.out.println("No external connections found.");
                }
            }

            // === Проверка порта ===
            System.out.println("\n--- Port " + portToCheck + " Check ---");
            boolean isBusy = false;
            for (ConnectionInfo info : connections) {
                int port = AddressUtils.extractPort(info.getLocalAddress());
                if (port == portToCheck) {
                    System.out.printf("Port %d is in use by PID: %s (%s)%n", portToCheck, info.getPid(), info.getProcessName());
                    isBusy = true;
                    break;
                }
            }

            if ("SUMMARY".equals(outputMode)) {
                if (!isBusy) {
                    System.out.println("No issues detected.");
                }
            } else {
                if (!isBusy) {
                    System.out.println("Port " + portToCheck + " is not in use.");
                }
            }

        } catch (Exception e) {
            System.err.println("Error during monitoring iteration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
