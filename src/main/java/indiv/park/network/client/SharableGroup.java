package indiv.park.network.client;

import indiv.park.network.processor.ProcessDistinguisher;
import io.netty.channel.EventLoopGroup;

public class SharableGroup {

	private EventLoopGroup eventLoopGroup;
	private ProcessDistinguisher distinguisher;

	public SharableGroup(EventLoopGroup eventLoopGroup, ProcessDistinguisher distinguisher) {
		this.eventLoopGroup = eventLoopGroup;
		this.distinguisher = distinguisher;
	}

	public EventLoopGroup getEventLoopGroup() {
		return eventLoopGroup;
	}

	public ProcessDistinguisher getDistinguisher() {
		return distinguisher;
	}
}
