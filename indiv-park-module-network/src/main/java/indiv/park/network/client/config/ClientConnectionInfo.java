package indiv.park.network.client.config;

public class ClientConnectionInfo {

	private String host;
	private int port, timeout, cycle;

	public ClientConnectionInfo(String host, int port, int timeout, int cycle) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.cycle = cycle;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}
	
	public int getCycle() {
		return cycle;
	}
}
