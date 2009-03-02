package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.component.Screen;

public interface EventClientHandlerInvoker 
{
	void invoke(String metodo, Screen screen, String idSender) throws Exception;
}
