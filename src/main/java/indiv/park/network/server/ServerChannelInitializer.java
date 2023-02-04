package indiv.park.network.server;

import java.lang.reflect.Constructor;
import java.util.List;

import indiv.park.network.processor.ProcessDistinguisher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final List<Class<?>> serverHandlerList;
	private final ProcessDistinguisher distinguisher;

	public ServerChannelInitializer(List<Class<?>> serverHandlerList, ProcessDistinguisher distinguisher) {
		this.serverHandlerList = serverHandlerList;
		this.distinguisher = distinguisher;
	}

	@Override
	public void initChannel(SocketChannel channel) throws Exception {
		serverHandlerList.forEach(clazz -> addPipeLine(channel, clazz));
	}

	private void addPipeLine(SocketChannel channel, Class<?> clazz) {
		try {
			Constructor<?>[] constructors = clazz.getConstructors();

			ChannelHandler channelHandler = null;
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterCount() == 0) {
					channelHandler = (ChannelHandler) constructor.newInstance();
					break;
				}
				if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].equals(ProcessDistinguisher.class)) {
					channelHandler = (ChannelHandler) constructor.newInstance(distinguisher);
					break;
				}
			}
			channel.pipeline().addLast(channelHandler);
			
		} catch (Exception e) {
			throw new RuntimeException("파이프라인에 핸들러를 등록하지 못했습니다.");
		}
	}
}
