package indiv.park.network.client;

import indiv.park.network.processor.ProcessDistinguisher;
import io.netty.channel.EventLoopGroup;

public class SharableGroup {

	private final EventLoopGroup eventLoopGroup;
	private final ProcessDistinguisher distinguisher;

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
