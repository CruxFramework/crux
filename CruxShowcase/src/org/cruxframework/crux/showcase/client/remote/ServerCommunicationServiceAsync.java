package org.cruxframework.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerCommunicationServiceAsync
{
	void sayHello(String name, AsyncCallback<String> callback);
}
