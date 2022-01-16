package indiv.park.network.processor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import indiv.park.network.annotation.ByteOperationCode;
import indiv.park.network.annotation.StringOperationCode;
import indiv.park.network.exception.SameOperationCodeException;
import indiv.park.network.processor.exception.NoProcessorFoundException;
import indiv.park.network.processor.inheritance.DataWrapper;
import indiv.park.starter.module.TaskExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class ProcessDistinguisher {

	private final ConcurrentMap<Object, Class<?>> processorMap = new ConcurrentHashMap<>();

	public void addProcessor(Class<?> clazz) {
		Object operationCode = getOperationCode(clazz);
		
		if (processorMap.containsKey(operationCode)) {
			throw new SameOperationCodeException();
		}
		processorMap.put(operationCode, clazz);
	}

	private Object getOperationCode(Class<?> clazz) {
		if (clazz.getAnnotation(ByteOperationCode.class) != null) {
			return clazz.getAnnotation(ByteOperationCode.class).value();
		}
		if (clazz.getAnnotation(StringOperationCode.class) != null) {
			return clazz.getAnnotation(StringOperationCode.class).value();
		}
		return null;
	}

	public void distinguish(ChannelHandlerContext ctx, DataWrapper dataWrapper) throws Exception {
		Class<?> clazz = processorMap.get(dataWrapper.getOperationKey());
		if (clazz == null) {
			throw new NoProcessorFoundException();
		}

		List<Object> parameterList = new ArrayList<Object>();

		Constructor<?>[] constructors = clazz.getConstructors();
		Constructor<?> constructor = constructors[0];

		Class<?>[] parameterTypes = constructor.getParameterTypes();
		for (Class<?> c : parameterTypes) {
			if (c.equals(ChannelHandlerContext.class)) {
				parameterList.add(ctx);
				continue;
			}
			if (c.equals(Channel.class)) {
				parameterList.add(ctx.channel());
				continue;
			}
			if (c.equals(DataWrapper.class)) {
				parameterList.add(dataWrapper);
				continue;
			}
		}
		TaskExecutor.execute((Runnable) constructor.newInstance(parameterList.toArray()));
	}
}