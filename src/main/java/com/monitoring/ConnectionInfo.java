package com.monitoring;

/**
 * Представляет информацию о сетевом соединении, полученную из вывода утилиты {@code netstat}.
 * Содержит протокол, локальный и удалённый адреса, состояние, PID и имя процесса.
 */
public class ConnectionInfo {
    private String protocol;
    private String localAddress;
    private String foreignAddress;
    private String state;
    private String pid;
    private String processName;

    /**
     * Создаёт объект с информацией о соединении
     *
     * @param protocol протокол соединения (например, "TCP", "UDP")
     * @param localAddress локальный адрес и порт (например, "192.168.1.10:8080" или "[::1]:8080")
     * @param foreignAddress удалённый адрес и порт (например, "104.18.3.10:443")
     * @param state состояние соединения (например, "LISTEN", "ESTABLISHED", "LISTENING")
     * @param pid идентификатор процесса (числовой, строка)
     * @param processName имя исполняемого файла процесса (без расширения .exe на Windows)
     */
    public ConnectionInfo(String protocol, String localAddress, String foreignAddress, String state, String pid, String processName) {
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.foreignAddress = foreignAddress;
        this.state = state;
        this.pid = pid;
        this.processName = processName;
    }

    /**
     * Возвращает протокол соединения
     *
     * @return строка протокола (например, "TCP")
     */
    public String getProtocol() { return protocol; }

    /**
     * Возвращает локальный адрес и порт в формате "адрес:порт"
     *
     * @return строка локального адреса
     */
    public String getLocalAddress() { return localAddress; }

    /**
     * Возвращает удалённый адрес и порт в формате "адрес:порт"
     *
     * @return строка удалённого адреса
     */
    public String getForeignAddress() { return foreignAddress; }

    /**
     * Возвращает состояние соединения
     *
     * @return состояние (например, "ESTABLISHED")
     */
    public String getState() { return state; }

    /**
     * Возвращает идентификатор процесса (PID)
     *
     * @return строка с PID
     */
    public String getPid() { return pid; }

    /**
     * Возвращает имя процесса (без .exe на Windows)
     *
     * @return имя исполняемого файла или "N/A", если неизвестно
     */
    public String getProcessName() { return processName; }

    /**
     * Устанавливает имя процесса (используется после дополнительного разрешения имён на Windows)
     *
     * @param processName новое имя процесса
     */
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @Override
    public String toString() {
        return String.format("PID: %s, Process: %s, Proto: %s, Local: %s, Foreign: %s, State: %s",
                pid, processName, protocol, localAddress, foreignAddress, state);
    }
}