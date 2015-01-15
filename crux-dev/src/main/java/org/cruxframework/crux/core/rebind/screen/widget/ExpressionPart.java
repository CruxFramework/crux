/*
 * Copyright 2014 cruxframework.org.
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

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ExpressionPart extends BindInfo
{
	private String converterVariableName = null;

	public ExpressionPart(String bindPath, JClassType dataObjectType, JClassType converterType, 
			String dataObject, String converterParams) throws NoSuchFieldException
    {
	    super(bindPath, dataObjectType, converterType, dataObject, converterParams);
    }
	
	@Override
	public String getConverterVariable()
	{
		if (converterClassName == null)
		{
			return null;
		}
		if (converterVariableName == null)
		{
			converterVariableName = ViewFactoryCreator.createVariableName(super.getConverterVariable());
		}
			
		return converterVariableName;
	}
}
