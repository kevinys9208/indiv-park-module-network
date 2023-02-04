package indiv.park.network.client;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.processor.ProcessDistinguisher;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final List<Class<?>> clientHandlerList;
	private final ProcessDistinguisher distinguisher;
	private final ClientConnectionInfo info;

	public ClientChannelInitializer(List<Class<?>> clientHandlerList, ProcessDistinguisher distinguisher, ClientConnectionInfo info) {
		this.clientHandlerList = clientHandlerList;
		this.distinguisher = distinguisher;
		this.info = info;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		clientHandlerList.forEach(clazz -> addPipeLine(ch, clazz));
	}

	private void addPipeLine(SocketChannel ch, Class<?> clazz) {
		try {
			List<Object> parameterList = new ArrayList<Object>();
			
			Constructor<?>[] constructors = clazz.getConstructors();
			Constructor<?> constructor = Arrays
											.stream(constructors)
											.filter(v -> v.getParameterTypes().length == 0)
											.findFirst()
											.orElse(constructors[0]);
			
			if (constructor.getParameterCount() == 0) {
				ch.pipeline().addLast((ChannelHandler) constructor.newInstance());
				
			} else {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				for (Class<?> c : parameterTypes) {
					if (c.equals(ProcessDistinguisher.class)) {
						parameterList.add(distinguisher);
						continue;
					}
					if (c.equals(ClientConnectionInfo.class)) {
						parameterList.add(info);
						continue;
					}
				}
				ch.pipeline().addLast((ChannelHandler) constructor.newInstance(parameterList.toArray()));
			}

		} catch (Throwable e) {
			throw new RuntimeException("파이프라인에 핸들러를 등록하지 못했습니다.");
		}
	}
}
