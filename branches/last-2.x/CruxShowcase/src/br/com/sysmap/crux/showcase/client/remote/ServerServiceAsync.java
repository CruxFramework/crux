package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServerServiceAsync
{
	void sayHello(String name, AsyncCallback<String> callback);
}
