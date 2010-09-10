package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.RemoteService;

public interface ServerService extends RemoteService
{
	String sayHello(String name);
}
