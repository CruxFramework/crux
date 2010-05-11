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
package br.com.sysmap.crux.tools.compile;

import br.com.sysmap.crux.core.config.AbstractPropertiesFactory;
import br.com.sysmap.crux.core.config.ConfigurationInvocationHandler;
import br.com.sysmap.crux.core.config.ConstantsInvocationHandler;
import br.com.sysmap.crux.core.i18n.MessageException;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class CompilerConfigurationFactory extends AbstractPropertiesFactory
{
	protected static final CompilerConfigurationFactory instance = new CompilerConfigurationFactory();
	
	private CompilerConfigurationFactory() 
	{
	}
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	public static CruxCompilerConfig getConfigurations() throws MessageException
	{
		return instance.getConstantsFromProperties(CruxCompilerConfig.class);
	}

	@Override
	protected ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface) 
	{
		return new ConfigurationInvocationHandler(targetInterface);
	}
}