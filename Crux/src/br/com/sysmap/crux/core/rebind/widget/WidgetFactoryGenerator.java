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
package br.com.sysmap.crux.core.rebind.widget;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.AbstractGenerator;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetFactoryGenerator extends AbstractGenerator
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	@Override
	public String generate(TreeLogger logger, GeneratorContext ctx, String requestedClass) throws UnableToCompleteException
	{
		TypeOracle typeOracle = ctx.getTypeOracle();
		assert (typeOracle != null);

		JClassType registeredIntf = typeOracle.findType(requestedClass);
		if (registeredIntf == null)
		{
			logger.log(TreeLogger.ERROR, messages.generatorSourceNotFound(requestedClass), null);
			throw new UnableToCompleteException();
		}

		return new WidgetFactoryProxyCreator(logger, ctx, registeredIntf).create();
	}
}
