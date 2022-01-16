package indiv.park.network.client.connector;

import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.client.inheritance.ClientConnector;

public enum ClientType {

	TCP() {
		
		@Override
		public ClientConnector loadClientConnectorByType(ClientConnectionInfo info) {
			return new TcpClientConnector(info);
		}
	}
	
	,WS() {
		
		@Override
		public ClientConnector loadClientConnectorByType(ClientConnectionInfo info) {
			return new WsClientConnector(info);
		}
	};
	
	public abstract ClientConnector loadClientConnectorByType(ClientConnectionInfo info);
}
