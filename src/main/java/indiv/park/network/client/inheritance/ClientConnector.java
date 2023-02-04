package indiv.park.network.client.inheritance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.processor.ProcessDistinguisher;
import io.netty.channel.EventLoopGroup;

public abstract class ClientConnector implements Runnable {

	protected ClientConnectionInfo info;
	protected EventLoopGroup workerGroup;
	protected ProcessDistinguisher distinguisher;

	protected List<Class<?>> clientHandlerList = new ArrayList<>();
	protected AtomicBoolean sync = new AtomicBoolean(false);

	public void sync() {
		sync.set(true);
		run();
	};

	public void addWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	};

	public void addProcessDistinguisher(ProcessDistinguisher distinguisher) {
		this.distinguisher = distinguisher;
	};

	public void addClientHandler(Class<?> clientHandler) {
		this.clientHandlerList.add(clientHandler);
	};
}
