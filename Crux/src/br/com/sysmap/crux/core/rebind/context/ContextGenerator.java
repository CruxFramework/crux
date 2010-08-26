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
package br.com.sysmap.crux.core.rebind.context;

import br.com.sysmap.crux.core.rebind.AbstractGenerator;
import br.com.sysmap.crux.core.rebind.AbstractProxyCreator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Generator for context access objects.
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
public class ContextGenerator extends AbstractGenerator
{
	@Override
    protected AbstractProxyCreator createProxy(TreeLogger logger, GeneratorContext ctx, JClassType baseIntf) throws UnableToCompleteException
    {
		if (baseIntf.isInterface() == null)
		{
			logger.log(TreeLogger.ERROR, messages.crossDocumentGeneratorTypeIsNotInterface(baseIntf.getQualifiedSourceName()), null); 
			throw new UnableToCompleteException();
		}
	    return new ContextProxyCreator(logger, ctx, baseIntf);
    }
}
