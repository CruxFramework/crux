package br.com.sysmap.crux.core.client.event;

import com.google.gwt.json.client.JSONValue;

import br.com.sysmap.crux.core.client.component.Screen;

public interface EventClientCallbackInvoker 
{
	void invoke(String metodo, Screen screen, String idSender, JSONValue result) throws Exception;
}
