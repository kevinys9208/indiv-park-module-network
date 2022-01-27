package indiv.park.network;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

public class NetworkChannelMatchers<T> {

	private final Map<T, Channel> channelMap = new ConcurrentHashMap<>();

	public boolean addChannel(T key, Channel value) {
		return channelMap.putIfAbsent(key, value) == null;
	}

	public boolean removeChannel(T key) {
		return !(channelMap.remove(key) == null);
	}

	public ChannelMatcher in(@SuppressWarnings("unchecked") T... key) {
		Channel[] channels = Arrays.stream(key).map(channelMap::get).toArray(Channel[]::new);
		return new CompositeChannelMatcher(channels, true);
	}

	public ChannelMatcher notIn(@SuppressWarnings("unchecked") T... key) {
		Channel[] channels = Arrays.stream(key).map(channelMap::get).toArray(Channel[]::new);
		return new CompositeChannelMatcher(channels, false);
	}

	private class CompositeChannelMatcher implements ChannelMatcher {

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
}
