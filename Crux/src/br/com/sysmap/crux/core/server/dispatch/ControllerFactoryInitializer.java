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
package br.com.sysmap.crux.core.server.dispatch;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.config.ConfigurationFactory;
import br.com.sysmap.crux.core.server.config.Crux;

public class ControllerFactoryInitializer 
{
	private static final Log logger = LogFactory.getLog(ControllerFactoryInitializer.class);
	private static ControllerFactory controllerFactory;
	private static final Lock lock = new ReentrantLock();
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public static ControllerFactory getControllerFactory() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (controllerFactory != null) return controllerFactory;
		
		try
		{
			lock.lock();
			if (controllerFactory != null) return controllerFactory;
			controllerFactory = (ControllerFactory) Class.forName(ConfigurationFactory.getConfiguration().controllerFactory()).newInstance(); 
		}
		catch (Throwable e)
		{
			if (logger.isInfoEnabled()) logger.info(messages.controllerFactoryInitializerUsingDefaultFactory());
			controllerFactory = (ControllerFactory) Class.forName(Crux.DEFAULT_CONTROLLER_FACTORY).newInstance(); 
		}
		finally
		{
			lock.unlock();
		}
		return controllerFactory;
	}

}
