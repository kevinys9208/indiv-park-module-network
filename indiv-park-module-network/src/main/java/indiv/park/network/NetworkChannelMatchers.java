package indiv.park.network;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

public class NetworkChannelMatchers<K> {

	private final Map<K, Channel> channelMap = new ConcurrentHashMap<>();
	
	public boolean addChannel(K key, Channel value) {
		return channelMap.putIfAbsent(key, value) == null;
	}
	
	public boolean removeChannel(K key) {
		return !(channelMap.remove(key) == null);
	}
	
	public ChannelMatcher in(@SuppressWarnings("unchecked") K... key) {
		Channel[] channels = Arrays.stream(key).map(channelMap::get).toArray(Channel[]::new);
		return new CompositeChannelMatcher(channels, true);
	}
	
	public ChannelMatcher notIn(@SuppressWarnings("unchecked") K...key) {
		Channel[] channels = Arrays.stream(key).map(channelMap::get).toArray(Channel[]::new);
		return new CompositeChannelMatcher(channels, false);
	}
}
