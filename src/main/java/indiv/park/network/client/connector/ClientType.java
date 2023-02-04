package indiv.park.network.client.connector;

import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.client.inheritance.ClientConnector;

public enum ClientType {

	TCP() {
		
		@Override
		public ClientConnector loadClientConnectorByType(ClientConnectionInfo info) {
			return new TcpClientConnector(info);
		}
	},
	
	HTTP() {
		
		@Override
		public ClientConnector loadClientConnectorByType(ClientConnectionInfo info) {
			return new HttpClientConnector(info);
		}
	};
	
	public abstract ClientConnector loadClientConnectorByType(ClientConnectionInfo info);
}
