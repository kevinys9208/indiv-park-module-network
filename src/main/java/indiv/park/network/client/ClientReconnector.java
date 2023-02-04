package indiv.park.network.client;

import java.util.concurrent.TimeUnit;

import indiv.park.starter.module.TaskExecutor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ClientReconnector implements ChannelFutureListener {

	private final Runnable clientConnector;
	private final int cycle;

	public ClientReconnector(Runnable clientConnector, int cycle) {
		this.clientConnector = clientConnector;
		this.cycle = cycle;
	}

	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		TaskExecutor.schedule(clientConnector, cycle, TimeUnit.SECONDS);
	}
}