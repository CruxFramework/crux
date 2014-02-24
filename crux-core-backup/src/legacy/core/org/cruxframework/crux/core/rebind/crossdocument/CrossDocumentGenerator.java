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
package org.cruxframework.crux.core.rebind.crossdocument;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.RebindMode;

/**
 * Generator for cross document objects.
 * 
 * @author Thiago da Rosa de Bustamante
 * 
 */
@Legacy
@Deprecated
public class CrossDocumentGenerator extends Generator
{
	
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);
		
		JClassType crossDocument = typeOracle.findType(typeName);
		if (crossDocument == null)
		{
			logger.log(TreeLogger.ERROR, "Unable to find source for type ["+typeName+"]", null); 
			throw new UnableToCompleteException();
		}
		
		if (crossDocument.isInterface() == null)
		{
			logger.log(TreeLogger.ERROR, "["+crossDocument.getQualifiedSourceName()+"] is not an interface.", null); 
			throw new UnableToCompleteException();
		}
		
		String returnType = new CrossDocumentProxyCreator(logger, context, crossDocument).create();
	    return new RebindResult(RebindMode.USE_PARTIAL_CACHED, returnType).getResultTypeName();
	}
	
}
