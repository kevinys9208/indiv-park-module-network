package indiv.park.network.processor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import indiv.park.network.exception.SameOperationCodeException;
import indiv.park.network.processor.annotation.Process;
import indiv.park.network.processor.annotation.opcode.ByteOpCode;
import indiv.park.network.processor.annotation.opcode.StringOpCode;
import indiv.park.network.processor.exception.NoProcessFoundException;
import indiv.park.network.processor.inheritance.DataWrapper;
import indiv.park.starter.module.TaskExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class ProcessDistinguisher {

	private final ConcurrentMap<Object, Method> processMap = new ConcurrentHashMap<>();

	public void addProcessor(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		
		for (Method method : methods) {
			if (method.getAnnotation(Process.class) != null) {
				Object opCode = getOperationCode(method);
				
				if (processMap.containsKey(opCode)) {
					throw new SameOperationCodeException();
				}
				processMap.put(opCode, method);
			}
		}
	}

	private Object getOperationCode(Method method) {
		if (method.getAnnotation(ByteOpCode.class) != null) {
			return method.getAnnotation(ByteOpCode.class).value();
		}
		if (method.getAnnotation(StringOpCode.class) != null) {
			return method.getAnnotation(StringOpCode.class).value();
		}
		return null;
	}

	public void distinguish(ChannelHandlerContext ctx, DataWrapper dataWrapper) throws Exception {
		Method method = processMap.get(dataWrapper.getOperationKey());
		if (method == null) {
			throw new NoProcessFoundException();
		}

		List<Object> parameterList = new ArrayList<Object>();

		Class<?>[] parameterTypes = method.getParameterTypes();
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
		
		TaskExecutor.execute(() -> this.executeMethod(method, parameterList));
	}
	
	private void executeMethod(Method method, List<Object> parameterList) {
		try {
			Class<?> clazz = method.getDeclaringClass();
			Object processor = clazz.getConstructor().newInstance();
			
			method.invoke(processor, parameterList.toArray());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}