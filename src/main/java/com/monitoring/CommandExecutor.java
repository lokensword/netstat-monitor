package com.monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения системных команд
 * Определяет ос и запускает соответствующую версию netstat
 */
public class CommandExecutor {

    /**
     * Выполняет команду netstat в зависимости от ос и возвращает список строк вывода
     * @return Список строк (stdout) команды
     * @throws IOException В случае ошибки ввода вывода
     */
    public List<String> executeNetstat() throws IOException {
        List<String> output = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();
        
        ProcessBuilder processBuilder;

        if (os.contains("win")) {
            // Windows: netstat -ano
            // -a: все подключения и порты
            // -n: адреса и порты в числовом формате
            // -o: включить ID процесса
            processBuilder = new ProcessBuilder("netstat", "-ano");
        } else {
            // Linux/Mac: netstat -anp
            // -p: показать PID и имя программы
            processBuilder = new ProcessBuilder("netstat", "-anp");
        }

        // Перенаправление потока ошибок для отладки (чтобы было видно в stdout команды)
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