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
package org.cruxframework.crux.core.rebind.screen.widget;

import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * Generates a ViewFactory class.  
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryGenerator extends IncrementalGenerator
{
	@Override
	public long getVersionId() 
	{
		return 1L;
	}
	
	@Override
	public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		ViewFactoriesProxyCreator proxy = new ViewFactoriesProxyCreator(new RebindContext(context, logger));
		String returnType = proxy.create();
		if (returnType == null)
		{
		    return new RebindResult(RebindMode.USE_EXISTING, typeName);
		}
		else
		{
		    return new RebindResult(RebindMode.USE_PARTIAL_CACHED, returnType);
		}
	}
}
