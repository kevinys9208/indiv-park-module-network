package indiv.park.network.client.config;

import java.util.Map;

public class ClientConfiguration {

	private String group, type;
	private int workerThread;

	public ClientConfiguration(Map<String, Object> config) {
		this.group = config.get("group") != null ? (String) config.get("group") : "default";
		this.type = config.get("type") != null ? (String) config.get("type") : "tcp";
		this.workerThread = config.get("workerThread") != null ? (int) config.get("workerThread") : 8;
	}

	public String getGroup() {
		return group;
	}

	public String getType() {
		return type;
	}

	public int getWorkerThread() {
		return workerThread;
	}
}
