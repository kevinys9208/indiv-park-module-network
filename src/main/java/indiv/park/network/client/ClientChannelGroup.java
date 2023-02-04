package indiv.park.network.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.util.internal.ObjectUtil;

public class ClientChannelGroup {

	public static final ClientChannelGroup INSTANCE = new ClientChannelGroup();
	
	private final Map<ChannelId, Channel> channelMap = new ConcurrentHashMap<>();
	private final Map<ChannelId, ChannelFutureListener> reconnectorMap = new ConcurrentHashMap<>();
	private final ChannelFutureListener remover = new ChannelFutureListener() {
		
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			remove(future.channel());
		}
	};
	
	private ClientChannelGroup() {}
	
	public boolean add(Channel channel) {
		boolean added = channelMap.putIfAbsent(channel.id(), channel) == null;
		if (added) {
			channel.closeFuture().addListener(remover);
		}
		return added;
	}

	public boolean add(Channel channel, ChannelFutureListener listener) {
		boolean added = channelMap.putIfAbsent(channel.id(), channel) == null;
		if (added) {
			reconnectorMap.put(channel.id(), listener);
			channel.closeFuture().addListener(listener);
			channel.closeFuture().addListener(remover);
		}

		return added;
	}

	public boolean remove(Object o) {
		Channel c = null;
		if (o instanceof ChannelId) {
			channelMap.remove(o);
			reconnectorMap.remove(o);

		} else if (o instanceof Channel) {
			c = (Channel) o;
			channelMap.remove(c.id());
			reconnectorMap.remove(o);
		}

		if (c == null) {
			return false;
		}

		c.closeFuture().removeListener(remover);
		return true;
	}

	public void close() {
		close(ChannelMatchers.all());
	}

	public void close(ChannelMatcher matcher) {
		ObjectUtil.checkNotNull(matcher, "matcher");

		for (Channel c : channelMap.values()) {
			if (matcher.matches(c)) {
				if (reconnectorMap.containsKey(c.id())) {
					c.closeFuture().removeListener(reconnectorMap.get(c.id()));
				}
				c.close();
			}
		}
	}
	
	public int size() {
		return channelMap.size();
	}
}
