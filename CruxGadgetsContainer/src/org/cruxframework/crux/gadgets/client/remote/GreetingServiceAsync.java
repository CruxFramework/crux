package org.cruxframework.crux.gadgets.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GreetingServiceAsync
{
	public void getHelloMessage(String name, AsyncCallback<String> callback);
}
