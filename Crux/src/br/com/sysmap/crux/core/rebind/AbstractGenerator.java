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
package br.com.sysmap.crux.core.rebind;

import br.com.sysmap.crux.core.i18n.MessagesFactory;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractGenerator extends Generator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	@Override
	public String generate(TreeLogger logger, GeneratorContext ctx, String requestedClass) throws UnableToCompleteException
	{
		TypeOracle typeOracle = ctx.getTypeOracle();
		assert (typeOracle != null);

		JClassType baseIntf = typeOracle.findType(requestedClass);
		if (baseIntf == null)
		{
			logger.log(TreeLogger.ERROR, messages.generatorSourceNotFound(requestedClass), null);
			throw new UnableToCompleteException();
		}

		return createProxy(logger, ctx, baseIntf).create();
	}
	
	/**
	 * @param logger
	 * @param ctx
	 * @param baseIntf
	 * @return
	 * @throws UnableToCompleteException 
	 */
	protected abstract AbstractProxyCreator createProxy(TreeLogger logger, GeneratorContext ctx, JClassType baseIntf) throws UnableToCompleteException;
}
