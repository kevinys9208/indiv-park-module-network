package indiv.park.network;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

public class CompositeChannelMatcher implements ChannelMatcher {

	private final Channel[] channels;
	private final boolean match;
	
	public CompositeChannelMatcher(Channel[] channels, boolean match) {
		this.channels = channels;
		this.match = match;
	}	
	
	@Override
	public boolean matches(Channel channel) {
		for (Channel target : channels) {
			if (channel.equals(target)) {
				return match;
			}
		}
		return !match;
	}
}
