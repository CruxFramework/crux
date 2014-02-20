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
package org.cruxframework.crux.core.server.rest.state;

import org.cruxframework.crux.core.config.AbstractPropertiesFactory;
import org.cruxframework.crux.core.config.ConfigurationInvocationHandler;
import org.cruxframework.crux.core.config.ConstantsInvocationHandler;
import org.cruxframework.crux.core.i18n.MessageException;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class ClusteredCacheConfigurationFactory extends AbstractPropertiesFactory
{
	protected static final ClusteredCacheConfigurationFactory instance = new ClusteredCacheConfigurationFactory();
	
	private ClusteredCacheConfigurationFactory() 
	{
	}
	
	public static ClusteredCacheConfig getConfigurations() throws MessageException
	{
		return instance.getConstantsFromProperties(ClusteredCacheConfig.class);
	}

	@Override
	protected ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface) 
	{
		return new ConfigurationInvocationHandler(targetInterface);
	}
}