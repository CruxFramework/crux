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
package org.cruxframework.crux.core.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractGenerator extends IncrementalGenerator
{

	@Override
	public long getVersionId() 
	{
		return 1L;
	}
	
	@Override
	public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);
		
		JClassType baseIntf = typeOracle.findType(typeName);
		if (baseIntf == null)
		{
			logger.log(TreeLogger.ERROR, "Unable to find source for type ["+typeName+"]", null);
			throw new UnableToCompleteException();
		}
		
		AbstractProxyCreator proxy = createProxy(logger, context, baseIntf);
		String returnType = proxy.create();
		if (returnType == null)
		{
		    return new RebindResult(RebindMode.USE_EXISTING, typeName);
		}
		else if (proxy.isCacheable())
		{
		    return new RebindResult(RebindMode.USE_PARTIAL_CACHED, returnType);
		}
		else
		{
		    return new RebindResult(RebindMode.USE_ALL_NEW_WITH_NO_CACHING, returnType);
		}
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
