package indiv.park.network.client.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import indiv.park.network.client.ClientChannelGroup;
import indiv.park.network.client.ClientChannelInitializer;
import indiv.park.network.client.ClientReconnector;
import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.client.inheritance.ClientConnector;
import indiv.park.network.exception.NoHandlerFoundException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClientConnector extends ClientConnector {

	private final Logger logger = LoggerFactory.getLogger(TcpClientConnector.class);

	public TcpClientConnector(ClientConnectionInfo info) {
		this.info = info;
	}

	@Override
	public void run() {
		try {
			if (clientHandlerList.size() == 0)
				throw new NoHandlerFoundException();

			if (info.getCycle() != 0 && info.getTimeout() > info.getCycle())
				throw new RuntimeException("재접속 주기가 타임아웃보다 짧습니다.");

			Bootstrap bootstrap = new Bootstrap();
			bootstrap
				.group(workerGroup)
				.channel(NioSocketChannel.class)
				.handler(new ClientChannelInitializer(clientHandlerList, distinguisher, info))
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, info.getTimeout() * 1000);

			ChannelFuture channelFuture = bootstrap.connect(info.getHost(), info.getPort());
			if (info.getCycle() != 0) {
				ClientChannelGroup.INSTANCE.add(channelFuture.channel(), new ClientReconnector(this, info.getCycle()));
				
			} else {
				ClientChannelGroup.INSTANCE.add(channelFuture.channel());
			}
			if (sync.get()) {
				sync.set(false);
				channelFuture.sync();
			}

		} catch (Exception e) {
			logger.error("클라이언트 접속 중 예외가 발생하였습니다. [ {} ]", e.toString());
		}
	}
}