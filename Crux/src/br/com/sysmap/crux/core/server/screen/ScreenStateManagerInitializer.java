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
package br.com.sysmap.crux.core.server.screen;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.config.Crux;

public class ScreenStateManagerInitializer 
{
	private static final Log logger = LogFactory.getLog(ScreenStateManagerInitializer.class);
	private static ScreenStateManager screenStateManager;
	private static final Lock lock = new ReentrantLock();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public static ScreenStateManager getScreenStateManager() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (screenStateManager != null) return screenStateManager;
		
		try
		{
			lock.lock();
			if (screenStateManager != null) return screenStateManager;
			screenStateManager = (ScreenStateManager) Class.forName(ConfigurationFactory.getConfiguration().screenStateManager()).newInstance(); 
		}
		catch (Throwable e)
		{
			if (logger.isInfoEnabled()) logger.info(messages.screenStateManagerInitializerUsingDefaultFactory());
			screenStateManager = (ScreenStateManager) Class.forName(Crux.DEFAULT_SCREEN_STATE_MANAGER).newInstance(); 
		}
		finally
		{
			lock.unlock();
		}
		return screenStateManager;
	}

}
