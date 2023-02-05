package indiv.park.network.server.binder;

import java.util.concurrent.TimeUnit;

import indiv.park.network.exception.NoHandlerFoundException;
import indiv.park.network.server.ServerChannelGroup;
import indiv.park.network.server.ServerChannelInitializer;
import indiv.park.network.server.config.ServerConfiguration;
import indiv.park.network.server.inheritance.ServerBinder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpServerBinder extends ServerBinder {

	public TcpServerBinder(ServerConfiguration config) {
		this.config = config;
	}

	@Override
	public void run() {
		EventLoopGroup bossGroup = null, workerGroup = null;
		try {
			if (serverHandlerList.size() == 0)
				throw new NoHandlerFoundException();

			bossGroup 	= new NioEventLoopGroup(config.bossThread);
			workerGroup = new NioEventLoopGroup(config.workerThread);
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap
					.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ServerChannelInitializer(serverHandlerList, distinguisher))
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = serverBootstrap.bind(config.port).sync();
			
			ServerChannelGroup.INSTANCE.add(f.channel());

			String BIND_LOG = "'{}' 서버가 '{}' 포트에 바인드 되었습니다.";
			logger.info(BIND_LOG, config.group, config.port);
			
			if (future != null)
				future.setResponse(true);

			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("서버를 바인딩 하던 중 예외가 발생하였습니다. [ {} ]", e.toString());
			
		} finally {
			if (workerGroup != null)	workerGroup.shutdownGracefully(1, 1000, TimeUnit.MILLISECONDS).syncUninterruptibly();
			if (bossGroup != null)		bossGroup.shutdownGracefully(1, 1000, TimeUnit.MILLISECONDS).syncUninterruptibly();
		}
	}
}