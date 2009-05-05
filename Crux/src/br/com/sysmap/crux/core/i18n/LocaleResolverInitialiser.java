/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.i18n;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;

/**
 * Initialises the LocaleResolver class
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gessé S. F. Dafé <code>gessedafe@gmail.com</code>
 */
public class LocaleResolverInitialiser 
{
	protected static LocaleResolver localeResolver;
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(LocaleResolverInitialiser.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	
	public static LocaleResolver getLocaleResolver()
	{
		if (localeResolver != null) return localeResolver;
		
		lock.lock();
		try
		{
			if (localeResolver != null) return localeResolver;
			localeResolver = (LocaleResolver) Class.forName(ConfigurationFactory.getConfiguration().localeResolver()).newInstance(); 
		}
		catch (Exception e) 
		{
			logger.error(messages.localeResolverInitialisationError(e.getMessage()), e);
		}
		finally
		{
			lock.unlock();
		}
		return localeResolver;
		
	}
}
