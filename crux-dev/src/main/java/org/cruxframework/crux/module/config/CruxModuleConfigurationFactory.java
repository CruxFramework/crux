/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.module.config;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.config.AbstractPropertiesFactory;
import org.cruxframework.crux.core.config.ConfigurationInvocationHandler;
import org.cruxframework.crux.core.config.ConstantsInvocationHandler;
import org.cruxframework.crux.core.i18n.MessageException;

/**
 * Factory for configuration parameters. Receive an Interface and use it's name to look for 
 * resource bundles in the classpath. Each interface's method is used as key 
 * in that property file.
 * @author Thiago da Rosa de Bustamante
 * @author Gesse S. F. Dafe <code>gessedafe@gmail.com</code>
 *
 */
@Deprecated
@Legacy
public class CruxModuleConfigurationFactory extends AbstractPropertiesFactory
{
	protected static final CruxModuleConfigurationFactory instance = new CruxModuleConfigurationFactory();
	
	private CruxModuleConfigurationFactory() 
	{
	}
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	public static CruxModuleConfig getConfigurations() throws MessageException
	{
		return instance.getConstantsFromProperties(CruxModuleConfig.class);
	}

	@Override
	protected ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface) 
	{
		return new ConfigurationInvocationHandler(targetInterface);
	}

}