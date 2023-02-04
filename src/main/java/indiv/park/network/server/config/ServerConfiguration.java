package indiv.park.network.server.config;

import java.util.Map;

public class ServerConfiguration {

	public final String group, type;
	public final int port, bossThread, workerThread;

	private ServerConfiguration(Map<String, Object> config) {
		this.group = config.get("group") != null ? (String) config.get("group") : "default";
		this.type = config.get("type") != null ? (String) config.get("type") : "tcp";
		this.port = config.get("port") != null ? (int) config.get("port") : 8080;
		this.bossThread = config.get("bossThread") != null ? (int) config.get("bossThread") : 2;
		this.workerThread = config.get("workerThread") != null ? (int) config.get("workerThread") : 8;
	}
	
	public static ServerConfiguration newConfiguration(Map<String, Object> config) {
		return new ServerConfiguration(config);
	}
}