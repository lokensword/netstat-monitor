package com.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсинг вывода утилиты netstat
 * Преобразует строки вывода в объекты ConnectionInfo
 * Поддерживает форматы Windows (netstat -ano) и Linux (netstat -anp)
 */
public class NetstatParser {

    /**
     * Парсинг списка строк из вывода netstat
     *
     * @param lines строки stdout команды netstat
     * @return список объектов ConnectionInfo
     */
    public List<ConnectionInfo> parse(List<String> lines) {
        List<ConnectionInfo> connections = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || (!trimmed.toLowerCase().startsWith("tcp") && !trimmed.toLowerCase().startsWith("udp"))) {
                continue;
            }

            String[] tokens = trimmed.split("\\s+");

            if (tokens.length >= 4) {
                String proto = tokens[0];
                String local = "";
                String foreign = "";
                String state = "UNKNOWN";
                String pid = "UNKNOWN";
                String processName = "Unknown";

                if (tokens.length == 5 && (proto.equalsIgnoreCase("TCP") || proto.equalsIgnoreCase("TCP6"))) {
                    local = tokens[1];
                    foreign = tokens[2];
                    state = tokens[3];
                    pid = tokens[4];
                } else if (tokens.length == 4 && (proto.equalsIgnoreCase("UDP") || proto.equalsIgnoreCase("UDP6"))) {
                    local = tokens[1];
                    foreign = tokens[2];
                    state = "NONE";
                    pid = tokens[3];
                } else if (tokens.length >= 7) {
                    local = tokens[3];
                    foreign = tokens[4];
                    state = tokens[5];
                    String pidAndProgram = tokens[6];

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