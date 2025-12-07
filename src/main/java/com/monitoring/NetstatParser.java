package com.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсер вывода утилиты netstat
 * Преобразует сырые строки в объекты ConnectionInfo
 */
public class NetstatParser {

    /**
     * Парсит список строк, полученных от команды netstat
     * @param lines Список строк вывода консоли
     * @return Список объектов ConnectionInfo
     */
    public List<ConnectionInfo> parse(List<String> lines) {
        List<ConnectionInfo> connections = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            // Пропуск заголовков и пустых строк
            // Работа только со строками, начинающимися на TCP или UDP (
            if (trimmed.isEmpty() || (!trimmed.toLowerCase().startsWith("tcp") && !trimmed.toLowerCase().startsWith("udp"))) {
                continue;
            }

            // Разбивка строки по токенам через "один иои более пробелов"
            String[] tokens = trimmed.split("\\s+");

            // Логика определения формата (windows и linux)
            // Windows TCP: Proto,  Local,  Foreign, State,   PID 
            // Windows UDP: Proto,  Local,  Foreign, PID 
            // Linux: (		Proto,  Recv-Q, Send-Q,  Local,   Foreign,   State,   PID/Program)

            if (tokens.length >= 4) {
                String proto = tokens[0];
                String local = "";
                String foreign = "";
                String state = "UNKNOWN";
                String pid = "UNKNOWN";
                String processName = "Unknown"; // На случай если не получилось получить имя процесса, что возможно в windows

                if (tokens.length == 5 && (proto.equalsIgnoreCase("TCP") || proto.equalsIgnoreCase("TCP6"))) {
                    // Windows TCP standard
                    local = tokens[1];
                    foreign = tokens[2];
                    state = tokens[3];
                    pid = tokens[4];
                } else if (tokens.length == 4 && (proto.equalsIgnoreCase("UDP") || proto.equalsIgnoreCase("UDP6"))) {
                    // Windows UDP standard 
                    local = tokens[1];
                    foreign = tokens[2];
                    state = "NONE"; 
                    pid = tokens[3];
                } else if (tokens.length >= 7) {
                    // Linux netstat -anp
                    local = tokens[3];
                    foreign = tokens[4];
                    state = tokens[5];
                    String pidAndProgram = tokens[6]; // "1234/mysqld"
                    
                    if (pidAndProgram.contains("/")) {
                        String[] parts = pidAndProgram.split("/", 2);
                        pid = parts[0];
                        processName = parts[1];
                    } else {
                        pid = pidAndProgram;
                    }
                }

                if (!pid.equals("UNKNOWN")) {
                    connections.add(new ConnectionInfo(proto, local, foreign, state, pid, processName));
                }
            }
        }
        return connections;
    }
}