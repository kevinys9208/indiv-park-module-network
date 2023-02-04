package indiv.park.network.client.config;

import lombok.Getter;

@Getter
public class ClientConnectionInfo {

	private String id, host;
	private int port, timeout, cycle;

	public ClientConnectionInfo(String id, String host, int port, int timeout, int cycle) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.cycle = cycle;
	}
}
