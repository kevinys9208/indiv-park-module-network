package indiv.park.network.client;

import indiv.park.network.client.annotation.ClientHandler;
import indiv.park.network.client.annotation.ClientProcessor;
import indiv.park.network.client.config.ClientConfiguration;
import indiv.park.network.client.config.ClientConnectionInfo;
import indiv.park.network.client.connector.ClientType;
import indiv.park.network.client.inheritance.ClientConnector;
import indiv.park.network.processor.ProcessDistinguisher;
import indiv.park.starter.annotation.Module;
import indiv.park.starter.inheritance.ModuleBase;
import indiv.park.starter.module.TaskExecutor;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Module(name = "client")
@Slf4j
public final class ClientModule implements ModuleBase {

	public static final ClientModule INSTANCE = new ClientModule();
	
	private final List<ClientConfiguration> clientList = new ArrayList<>();
	private final Map<String, SharableGroup> sharableGroupMap = new ConcurrentHashMap<>();

	private Set<Class<?>> handlerSet;
	private Set<Class<?>> processorSet;
	private Object configuration;
	
	private ClientModule() {}

	@Override
	public void initialize(Class<?> mainClass) {
		if (configuration != null)
			addUserClientConfiguration();

		Reflections reflections = new Reflections(mainClass.getPackage().getName());

		loadHandlerList(reflections);
		loadProcessorList(reflections);
	}

	@Override
	public void setConfiguration(Object configuration) {
		this.configuration = configuration;
	}

	@SuppressWarnings("unchecked")
	private void addUserClientConfiguration() {
		List<Map<String, Object>> clientConfigList = (List<Map<String, Object>>) configuration;
		clientConfigList.forEach(config -> clientList.add(ClientConfiguration.newCongiguration(config)));
	}

	private void loadHandlerList(Reflections reflections) {
		handlerSet = reflections.getTypesAnnotatedWith(ClientHandler.class);
		final String found = "확인된 핸들러 : {}";
		
		for (Class<?> clazz : handlerSet) {
			logger.info(found, clazz.getSimpleName());
		}
	}

	private void loadProcessorList(Reflections reflections) {
		processorSet = reflections.getTypesAnnotatedWith(ClientProcessor.class);
		final String found = "확인된 프로세서 : {}";
		
		for (Class<?> clazz : processorSet) {
			logger.info(found, clazz.getSimpleName());
		}
	}
	
	public void connect(String group, ClientConnectionInfo... connectionInfos) {
		connect(group, false, connectionInfos);
	}

	public void connect(String group, boolean sync, ClientConnectionInfo... connectionInfos) {
		if (connectionInfos == null || connectionInfos.length == 0) {
			logger.error("클라이언트 접속 정보가 존재하지 않습니다.");
			return;
		}

		ClientConfiguration configuration;
		configuration = clientList
								.stream()
								.filter(config -> config.group.equals(group))
								.findFirst()
								.orElse(null);
		
		if (configuration == null) {
			logger.error("{} 그룹에 해당하는 설정 정보가 존재하지 않습니다.", group);
			return;
		}

		for (ClientConnectionInfo info : connectionInfos) {
			ClientConnector client = createClientConnector(info, configuration);

			setSharableGroup(client, configuration);
			connectClient(client, sync);
		}
	}

	private void connectClient(ClientConnector client, boolean sync) {
		if (sync) {
			client.sync();
			
		} else {
			TaskExecutor.execute(client);
		}
	}

	private ClientConnector createClientConnector(ClientConnectionInfo info, ClientConfiguration config) {
		ClientConnector connector = ClientType.valueOf(config.type.toUpperCase()).loadClientConnectorByType(info);

		handlerSet
				.stream()
				.filter(clazz -> findGroupHandler(clazz, config.group))
				.sorted(this::compareHandlerOrder)
				.forEach(clazz -> addClientHandler(connector, clazz));
		
		return connector;
	}

	private boolean findGroupHandler(Class<?> clazz, String group) {
		return clazz.getAnnotation(ClientHandler.class).group().equals(group);
	}

	private int compareHandlerOrder(Class<?> firstHandler, Class<?> secondHandler) {
		int firstHandlerOrder = firstHandler.getAnnotation(ClientHandler.class).order();
		int secondHandlerOrder = secondHandler.getAnnotation(ClientHandler.class).order();
		return firstHandlerOrder - secondHandlerOrder;
	}

	private void addClientHandler(ClientConnector clientConnector, Class<?> clazz) {
		clientConnector.addClientHandler(clazz);
	}

	private void setSharableGroup(ClientConnector connector, ClientConfiguration config) {
		EventLoopGroup workerGroup;
		ProcessDistinguisher distinguisher;

		SharableGroup sharableGroup = sharableGroupMap.get(config.group);
		if (sharableGroup == null) {
			workerGroup = new NioEventLoopGroup(config.workerThread);
			distinguisher = createProcessDistinguisher(config.group);

			SharableGroup group = new SharableGroup(workerGroup, distinguisher);
			sharableGroupMap.put(config.group, group);

		} else {
			workerGroup = sharableGroup.getEventLoopGroup();
			distinguisher = sharableGroup.getDistinguisher();
		}

		connector.addWorkerGroup(workerGroup);
		connector.addProcessDistinguisher(distinguisher);
	}

	private ProcessDistinguisher createProcessDistinguisher(String group) {
		ProcessDistinguisher distinguisher = new ProcessDistinguisher();
		processorSet
				.stream()
				.filter(clazz -> findGroupProcessor(clazz, group))
				.forEach(distinguisher::addProcessor);
		
		return distinguisher;
	}

	private boolean findGroupProcessor(Class<?> clazz, String group) {
		return clazz.getAnnotation(ClientProcessor.class).group().equals(group);
	}

	public void closeGracefully() {
		ClientChannelGroup.INSTANCE.close();
		sharableGroupMap.forEach((group, sharableGroup) -> {
			sharableGroup
					.getEventLoopGroup()
					.shutdownGracefully(1, 1000, TimeUnit.MILLISECONDS)
					.syncUninterruptibly();
		});
		
		logger.info("클라이언트를 안전하게 종료하였습니다.");
	}
}