package com.monitoring;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CommandExecutor executor = new CommandExecutor();
        try {
            System.out.println("Запуск netstat...");
            for (String line : executor.executeNetstat()) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}