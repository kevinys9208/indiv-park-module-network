package indiv.park.network.server.binder;

import indiv.park.network.server.config.ServerConfiguration;
import indiv.park.network.server.inheritance.ServerBinder;

public enum ServerType {

	TCP() {

		@Override
		public ServerBinder loadServerBinder(ServerConfiguration config) {
			return new TcpServerBinder(config);
		}

	},
	
	HTTP() {

		@Override
		public ServerBinder loadServerBinder(ServerConfiguration config) {
			return new HttpServerBinder(config);
		}
	};

	public abstract ServerBinder loadServerBinder(ServerConfiguration config);
}
