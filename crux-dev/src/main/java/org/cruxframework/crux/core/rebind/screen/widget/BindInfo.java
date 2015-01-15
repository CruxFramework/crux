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

import org.cruxframework.crux.core.client.converter.TypeConverter;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class BindInfo
{
	protected static String DATA_OBJECT_VAR_REF = "{0}";
	protected static String CONVERTER_VARIABLE = "__converter";

	protected String dataObjectClassName;
	protected String dataObject;
	protected String converterClassName;
	protected String converterParams;
	protected String dataObjectReadExpression;
	protected JClassType converterType;
	protected JType bindPathType;
	protected JType bindInfoType;

	public BindInfo(String bindPath, JClassType dataObjectType, 
			JClassType converterType, String dataObject, String converterParams) throws NoSuchFieldException
	{
		this.converterType = converterType;
		this.dataObjectClassName = dataObjectType.getQualifiedSourceName();
		this.dataObject = dataObject;
		this.converterClassName = converterType != null ? converterType.getParameterizedQualifiedSourceName() : null;
		this.converterParams = converterParams == null ? "": converterParams;
		this.dataObjectReadExpression = getDataObjectReadExpression(dataObjectType, bindPath);
	}
	
	public String getDataObjectClassName()
    {
	    return dataObjectClassName;
    }
	
	public String getConverterClassName()
	{
		return converterClassName;
	}
	
	public String getConverterVariable()
	{
		return (converterClassName != null ? CONVERTER_VARIABLE : null); 
	}
	
	public String getDataObject()
	{
		return dataObject;
	}
	
	public String getConverterDeclaration()
    {
		if (getConverterVariable() != null)
		{
			return getConverterClassName() + " " + getConverterVariable() +	" = new " + getConverterClassName() + "("+converterParams+");";
		}
        return null;
    }

	public String getExpression(String expression, String dataObjectVariable)
	{
		return expression.replace(DATA_OBJECT_VAR_REF, dataObjectVariable);
	}
	
	public JType getType()
    {
	    return bindInfoType;
    }
	
	protected String getDataObjectReadExpression() 
	{
		return dataObjectReadExpression;
	}
	
	protected String getDataObjectReadExpression(JClassType dataObjectType, String bindPath) throws NoSuchFieldException
	{
		StringBuilder getExpression = new StringBuilder();

		bindPathType = JClassUtils.buildGetValueExpression(getExpression, dataObjectType, bindPath, DATA_OBJECT_VAR_REF, false);

		String converterVariable = getConverterVariable();
		if (converterVariable != null)
		{
			getExpression.insert(0, converterVariable + ".to(").append(")");
			JClassType typeConverterType = converterType.getOracle().findType(TypeConverter.class.getCanonicalName());
			JClassType[] types = JClassUtils.getActualParameterTypes(converterType, typeConverterType);
			bindInfoType = types[1];
		}
		else
		{
			bindInfoType = bindPathType;
		}

		//TODO validate conveter type and expression type here

		return getExpression.toString();
	}
	
	protected String getDataObjectWriteExpression(JClassType dataObjectType, String bindPath, String value) throws NoSuchFieldException
	{
		StringBuilder writeExpression = new StringBuilder();
		
		String converterVariable = getConverterVariable();
		if (converterVariable != null)
		{
			value = converterVariable + ".from(" + value + ")";
		}

		JClassUtils.buildSetValueExpression(writeExpression, dataObjectType, bindPath, DATA_OBJECT_VAR_REF, value);
		//TODO validate conveter type and expression type here
        return writeExpression.toString();
	}
	
}
