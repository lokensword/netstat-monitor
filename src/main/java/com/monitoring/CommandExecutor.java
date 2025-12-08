package com.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Выполнение системных команд
 * Автоматически определяет ОС и запускает соответствующую версию netstat
 */
public class CommandExecutor {

    /**
     * Выполнение команды netstat в зависимости от ОС
     *
     * @return список строк вывода команды
     * @throws IOException при ошибке запуска процесса
     */
    public List<String> executeNetstat() throws IOException {
        List<String> output = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();

        ProcessBuilder processBuilder;

        if (os.contains("win")) {
            processBuilder = new ProcessBuilder("netstat", "-ano");
        } else {
            processBuilder = new ProcessBuilder("netstat", "-anp");
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        }

        return output;
    }
}