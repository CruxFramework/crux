package br.com.sysmap.crux.core.server.config;

import br.com.sysmap.crux.core.i18n.MessagesFactory;

public class ConfigurationFactory 
{
	public static final Crux getConfiguration()
	{
		return (Crux)MessagesFactory.getMessages(Crux.class);
	}
}
