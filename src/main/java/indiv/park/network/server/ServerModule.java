package indiv.park.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;

import indiv.park.network.processor.ProcessDistinguisher;
import indiv.park.network.server.annotation.ServerHandler;
import indiv.park.network.server.annotation.ServerProcessor;
import indiv.park.network.server.binder.ServerType;
import indiv.park.network.server.config.ServerConfiguration;
import indiv.park.network.server.inheritance.ServerBinder;
import indiv.park.starter.annotation.Module;
import indiv.park.starter.inheritance.ModuleBase;
import indiv.park.starter.module.future.ResponseFuture;
import lombok.extern.slf4j.Slf4j;

@Module(name = "server")
@Slf4j
public final class ServerModule implements ModuleBase {

	public static final ServerModule INSTANCE = new ServerModule();
	
	private final List<ServerConfiguration> serverList = new ArrayList<>();

	private Set<Class<?>> handlerSet;
	private Set<Class<?>> processorSet;
	private Object configuration;

	private ServerModule() {}

	@Override
	public void initialize(Class<?> mainClass) {
		if (configuration != null)
			addUserServerConfiguration();

		Reflections reflections = new Reflections(mainClass.getPackage().getName());

		loadHandlerList(reflections);
		loadProcessorList(reflections);
	}

	@Override
	public void setConfiguration(Object configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings("unchecked")
	private void addUserServerConfiguration() {
		List<Map<String, Object>> serverConfigList = (List<Map<String, Object>>) configuration;
		serverConfigList.forEach(config -> serverList.add(ServerConfiguration.newConfiguration(config)));
	}

	private void loadHandlerList(Reflections reflections) {
		handlerSet = reflections.getTypesAnnotatedWith(ServerHandler.class);
		final String found = "확인된 핸들러 : {}";
		
		for (Class<?> clazz : handlerSet)
			logger.info(found, clazz.getSimpleName());
	}

	private void loadProcessorList(Reflections reflections) {
		processorSet = reflections.getTypesAnnotatedWith(ServerProcessor.class);
		final String found = "확인된 프로세서 : {}";
		
		for (Class<?> clazz : processorSet)
			logger.info(found, clazz.getSimpleName());
	}
	
	public void bind(String group) {
		bind(group, false);
	}

	public void bind(String group, boolean sync) {
		ServerConfiguration configuration = serverList
												.stream()
												.filter(config -> config.group.equals(group))
												.findFirst()
												.orElse(null);
		if (configuration == null) {
			logger.error("{} 그룹에 대한 설정 정보가 존재하지 않습니다.", group);
			return;
		}
		
		ServerBinder serverBinder = createServerBinder(configuration);

		bindServerOnNewThread(serverBinder, group, sync);
	}

	private ServerBinder createServerBinder(ServerConfiguration config) {
		ServerBinder serverBinder = ServerType.valueOf(config.type.toUpperCase()).loadServerBinder(config);
		ProcessDistinguisher distinguisher = new ProcessDistinguisher();
		handlerSet
				.stream()
				.filter(clazz -> findGroupHandler(clazz, config.group))
				.sorted(this::compareHandlerOrder)
				.forEach(serverBinder::addServerHandler);

		processorSet
				.stream()
				.filter(clazz -> findGroupProcessor(clazz, config.group))
				.forEach(distinguisher::addProcessor);

		serverBinder.addProcessDistinguisher(distinguisher);
		
		return serverBinder;
	}

	private boolean findGroupHandler(Class<?> clazz, String group) {
		return clazz.getAnnotation(ServerHandler.class).group().equals(group);
	}

	private int compareHandlerOrder(Class<?> firstHandler, Class<?> secondHandler) {
		int firstHandlerOrder = firstHandler.getAnnotation(ServerHandler.class).order();
		int secondHandlerOrder = secondHandler.getAnnotation(ServerHandler.class).order();
		return firstHandlerOrder - secondHandlerOrder;
	}

	private boolean findGroupProcessor(Class<?> clazz, String group) {
		return clazz.getAnnotation(ServerProcessor.class).group().equals(group);
	}
	
	private void bindServerOnNewThread(ServerBinder binder, String group, boolean sync) {
		Thread newThread = new Thread(binder, "server-" + group);
		
		if (!sync) {
			newThread.start();
			return;
		}
		
		ResponseFuture<String, Boolean> future = binder.setResponseFuture(group);
		
		newThread.start();
		
		try {
			future.get();
			
		} catch (Exception e) {
			//
		}
	}
	
	public void closeGracefully() {
		ServerChannelGroup.INSTANCE.close();
		
		logger.info("서버를 안전하게 종료하였습니다.");
	}
}
