package indiv.park.network.client.config;

import java.util.Map;

public class ClientConfiguration {

	public final String group, type;
	public final int workerThread;

	private ClientConfiguration(Map<String, Object> config) {
		this.group = config.get("group") != null ? (String) config.get("group") : "default";
		this.type = config.get("type") != null ? (String) config.get("type") : "tcp";
		this.workerThread = config.get("workerThread") != null ? (int) config.get("workerThread") : 8;
	}

	public static ClientConfiguration newCongiguration(Map<String, Object> config) {
		return new ClientConfiguration(config);
	}
}
