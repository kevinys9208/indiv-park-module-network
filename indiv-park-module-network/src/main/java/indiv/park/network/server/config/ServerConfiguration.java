package indiv.park.network.server.config;

import java.util.Map;

public class ServerConfiguration {

	private String group, type;
	private int port, bossThread, workerThread;

	public ServerConfiguration(Map<String, Object> config) {
		this.group = config.get("group") != null ? (String) config.get("group") : "default";
		this.type = config.get("type") != null ? (String) config.get("type") : "tcp";
		this.port = config.get("port") != null ? (int) config.get("port") : 8080;
		this.bossThread = config.get("bossThread") != null ? (int) config.get("bossThread") : 2;
		this.workerThread = config.get("workerThread") != null ? (int) config.get("workerThread") : 8;
	}

	public String getGroup() {
		return group;
	}

	public String getType() {
		return type;
	}

	public int getPort() {
		return port;
	}

	public int getBossThread() {
		return bossThread;
	}

	public int getWorkerThread() {
		return workerThread;
	}

}
