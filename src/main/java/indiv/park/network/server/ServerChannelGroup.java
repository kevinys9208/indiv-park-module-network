package indiv.park.network.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.ChannelMatchers;
import io.netty.util.internal.ObjectUtil;

public class ServerChannelGroup {

	public static final ServerChannelGroup INSTANCE = new ServerChannelGroup();
	
	private final Map<ChannelId, Channel> channelMap = new ConcurrentHashMap<>();
	private final ChannelFutureListener remover = future -> remove(future.channel());
	
	private ServerChannelGroup() {}

	public boolean add(Channel channel) {
		boolean added = channelMap.putIfAbsent(channel.id(), channel) == null;
		if (added)
			channel.closeFuture().addListener(remover);
		return added;
	}

	public boolean remove(Object o) {
		Channel c = null;
		if (o instanceof ChannelId) {
			c = channelMap.remove(o);

		} else if (o instanceof Channel) {
			c = (Channel) o;
			c = channelMap.remove(c.id());
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
				c.close();
			}
		}
	}
	
	public int size() {
		return channelMap.size();
	}
}
