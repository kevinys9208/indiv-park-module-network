package indiv.park.network.client;

import indiv.park.starter.module.TaskExecutor;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public class ClientReconnector implements ChannelFutureListener {

	private final Runnable clientConnector;
	private final int cycle;

	public ClientReconnector(Runnable clientConnector, int cycle) {
		this.clientConnector = clientConnector;
		this.cycle = cycle;
	}

	@Override
	public void operationComplete(@NonNull ChannelFuture future) throws Exception {
		TaskExecutor.schedule(clientConnector, cycle, TimeUnit.SECONDS);
	}
}