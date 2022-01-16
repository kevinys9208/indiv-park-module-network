package indiv.park.network.server.inheritance;

import java.util.ArrayList;
import java.util.List;

import indiv.park.network.processor.ProcessDistinguisher;
import indiv.park.network.server.config.ServerConfiguration;
import indiv.park.starter.module.future.ResponseFuture;
import indiv.park.starter.module.future.ResponseFutureListener;

public abstract class ServerBinder implements Runnable {

	protected ServerConfiguration config;
	protected ProcessDistinguisher distinguisher;
	protected ResponseFuture<String, Boolean> future;
	
	private final ResponseFutureListener<String, Boolean> remover = new ResponseFutureListener<String, Boolean>() {
		
		@Override
		public void operationComplete(ResponseFuture<String, Boolean> future) {
			future = null;
		}
	};
	
	protected List<Class<?>> serverHandlerList = new ArrayList<>();
	
	public void addServerHandler(Class<?> serverHandler) {
		this.serverHandlerList.add(serverHandler);
	}

	public void addProcessDistinguisher(ProcessDistinguisher distinguisher) {
		this.distinguisher = distinguisher;
	}
	
	public void setResponseFuture(ResponseFuture<String, Boolean> future) {
		this.future = future;
		this.future.addRemover(remover);
	}
}