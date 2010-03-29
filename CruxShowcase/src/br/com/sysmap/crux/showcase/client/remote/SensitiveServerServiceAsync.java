package br.com.sysmap.crux.showcase.client.remote;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SensitiveServerServiceAsync
{
	void sensitiveMethod(AsyncCallback<String> callback);
	void sensitiveMethodNoBlock(AsyncCallback<String> callback);
}
