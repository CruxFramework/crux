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

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * Default ServiceFactory implementation. It will use the first implementation found 
 * to the given interface passed.
 *  
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ServiceFactoryImpl implements ServiceFactory 
{
	private static ServerMessages messages = MessagesFactory.getMessages(ServerMessages.class);
	private static final Log logger = LogFactory.getLog(ServiceFactoryImpl.class);
	
	/**
	 * This class uses a file generated during application compilation to find out which
	 * class it must instantiate to each interface service.
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class CompileTimeStrategy implements FactoryStrategy
	{

		public Object getService(String serviceName)
		{
			try 
			{
				return ServicesCompileMap.getService(serviceName).newInstance();
			} 
			catch (Exception e) 
			{
				throw new RuntimeException(messages.servicesErrorCreatingService(serviceName, e.getMessage()), e);
			} 
		}

		public boolean initialize(ServletContext context)
		{
			return ServicesCompileMap.initialize(context);
		}
	}

	/**
	 * Describes a strategy for service instantiation.
	 * @author Thiago da Rosa de Bustamante
	 */
	private static interface FactoryStrategy
	{
		/**
		 * @see br.com.sysmap.crux.core.server.dispatch.ServiceFactory#getService(java.lang.String)
		 */
		Object getService(String serviceName);
		
		/**
		 * @see br.com.sysmap.crux.core.server.dispatch.ServiceFactory#initialize(javax.servlet.ServletContext)
		 */
		boolean initialize(ServletContext context);
	}
	
	/**
	 * This class scan the application classpath to the first class to build a map of 
	 * interfaces implementations and uses it to find out which
	 * class it must instantiate to each interface service. For debug purposes, it is 
	 * better, once it supports hot deployment of resources, but it waste memory in 
	 * production.
	 *
	 * @author Thiago da Rosa de Bustamante
	 */
	private static class RuntimeStrategy implements FactoryStrategy
	{

		public Object getService(String serviceName)
		{
			try 
			{
				return Services.getService(serviceName).newInstance();
			} 
			catch (Exception e) 
			{
				throw new RuntimeException(messages.servicesErrorCreatingService(serviceName, e.getMessage()), e);
			} 
		}

		public boolean initialize(ServletContext context)
		{
			ClassScanner.initialize();
			return true;
		}
	}

	private FactoryStrategy strategy;
	
	
	/**
	 * This Constructor select the best strategy to use. 
	 */
	public ServiceFactoryImpl()
	{
		if (Environment.isProduction() ||  Boolean.parseBoolean(ConfigurationFactory.getConfigurations().useCompileTimeClassScanning()))
		{
			strategy = new CompileTimeStrategy();
		}
		else
		{
			strategy = new RuntimeStrategy();
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.ServiceFactory#getService(java.lang.String)
	 */
	public Object getService(String serviceName) 
	{
		return strategy.getService(serviceName);
	}

	/**
	 * @see br.com.sysmap.crux.core.server.dispatch.ServiceFactory#initialize(javax.servlet.ServletContext)
	 */
	public void initialize(ServletContext context) 
	{
		if (!strategy.initialize(context))
		{
			if (strategy instanceof CompileTimeStrategy)
			{
				logger.info(messages.servicesCompileStrategyInitializeError());
				strategy = new RuntimeStrategy();
				strategy.initialize(context);
			}
		}
	}
}
