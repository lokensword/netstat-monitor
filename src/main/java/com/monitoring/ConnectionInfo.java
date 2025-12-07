package com.monitoring;

/**
 * Класс представляющий информацию о сетевом соединении
 * Хранит данные полученные после парсинга строки netstat
 */
public class ConnectionInfo {
    private String protocol;
    private String localAddress;
    private String foreignAddress;
    private String state;
    private String pid;
    private String processName;

    public ConnectionInfo(String protocol, String localAddress, String foreignAddress, String state, String pid, String processName) {
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.foreignAddress = foreignAddress;
        this.state = state;
        this.pid = pid;
        this.processName = processName;
    }

	public void setProcessName(String processName) {
		this.processName = processName; }
		
    public String getProtocol() { return protocol; }
    public String getLocalAddress() { return localAddress; }
    public String getForeignAddress() { return foreignAddress; }
    public String getState() { return state; }
    public String getPid() { return pid; }
    public String getProcessName() { return processName; }

    @Override
    public String toString() {
        return String.format("PID: %s, Process: %s, Proto: %s, Local: %s, Foreign: %s, State: %s",
                pid, processName, protocol, localAddress, foreignAddress, state);
    }
}