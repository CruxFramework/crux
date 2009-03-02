package br.com.sysmap.crux.core.server.screen.formatter;

import br.com.sysmap.crux.core.client.formatter.ClientFormatter;

public interface Formatter 
{
	Class<? extends ClientFormatter> getClientFormatter();
	Class<? extends ServerFormatter> getServerFormatter();
}
