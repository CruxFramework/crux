package org.cruxframework.crux.showcase.client.remote;


import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Business Interface
 */
public interface ServerCommunicationService extends RemoteService
{
	String sayHello(String name) throws PleaseTypeYourNameException;
}
