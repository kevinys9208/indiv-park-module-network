package indiv.park.network.client.config;

import lombok.Getter;

@Getter
public class ClientConnectionInfo {

	private final String id, host;
	private final int port, timeout, cycle;

	public ClientConnectionInfo(String id, String host, int port, int timeout, int cycle) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.cycle = cycle;
	}
}
