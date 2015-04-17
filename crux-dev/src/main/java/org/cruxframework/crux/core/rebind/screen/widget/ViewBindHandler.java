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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.cruxframework.crux.core.client.screen.binding.DataObjectBinder;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.converter.Converters;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.util.collect.HashSet;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewBindHandler
{
	private View view;
	private GeneratorContext context;
	private Set<String> dataObjects = new HashSet<String>();
	private Map<String, String> dataObjectBinderVariables = new HashMap<String, String>();

	public ViewBindHandler(GeneratorContext context, View view)
    {
		this.context = context;
		this.view = view;
    }
	
	/**
	 * Retrieve the variable name for the dataObjectBinder associated with the given alias.
	 * @param dataObjectAlias
	 * @param out
	 * @return
	 */
	protected String getDataObjectBinderVariable(String dataObjectAlias, SourcePrinter out)
	{
		String dataObjectBinder = dataObjectBinderVariables.get(dataObjectAlias);
		if (dataObjectBinder == null)
		{
			dataObjectBinder = ViewFactoryCreator.createVariableName("dataObjectBinder");
			dataObjectBinderVariables.put(dataObjectAlias, dataObjectBinder);
			String dataObjectClassName = DataObjects.getDataObject(dataObjectAlias);//getObjectDataBinding(dataObjectAlias).getDataObjectClassName();
			
			out.println("final " + DataObjectBinder.class.getCanonicalName() + "<" + dataObjectClassName + "> " + dataObjectBinder + "=" + 
					ViewFactoryCreator.getViewVariable() + ".getDataObjectBinder("+EscapeUtils.quote(dataObjectAlias)+");");
		}
		return dataObjectBinder;
	}

	protected void addDataObject(String dataObject)
	{
		dataObjects.add(dataObject);
	}
	
	protected Iterator<String> iterateDataObjects()
    {
		return dataObjects.iterator();
    }
	
	protected PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath,
													boolean boundToAttribute)
	{
		String trimPropertyValue = propertyValue.trim();
	    if (isObjectDataBinding(trimPropertyValue))
	    {
	    	return getPropertyBindInfo(widgetClassName, boundToAttribute, widgetPropertyPath, trimPropertyValue);
	    }
	    else
	    {
	    	return null;
	    }
	}

	protected ExpressionDataBinding getExpressionDataBinding(String propertyValue, String widgetClassName, 
			String widgetPropertyPath, JType widgetPropertyType)
	{
		if (propertyValue == null)
		{
			return null;
		}
	    JClassType widgetType = context.getTypeOracle().findType(widgetClassName);
		String trimPropertyValue = propertyValue.trim();
		if (RegexpPatterns.REGEXP_CRUX_READ_ONLY_OBJECT_DATA_BINDING.matcher(trimPropertyValue).matches())
		{
			return getReadOnlyObjectBindingExpression(widgetPropertyPath, widgetPropertyType, widgetType, trimPropertyValue);
		}
		else if (widgetPropertyType == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(widgetPropertyType.getQualifiedSourceName()))
		{
			if (RegexpPatterns.REGEXP_CRUX_EXPRESSION_DATA_BINDING.matcher(trimPropertyValue).matches())
			{
				return getLogicalBindingExpression(widgetType, widgetPropertyPath, trimPropertyValue);
			}
		}
		else
		{
			return getMultipleBindingsExpression(widgetType, widgetPropertyPath, trimPropertyValue);
		}
		
		return null;
	}

	private ExpressionDataBinding getMultipleBindingsExpression(JClassType widgetType, String widgetPropertyPath, 
			String trimPropertyValue)
    {
	    ExpressionDataBinding result;
	    Matcher matcher = RegexpPatterns.REGEXP_CRUX_OBJECT_DATA_BINDING.matcher(trimPropertyValue);
	    result = new ExpressionDataBinding(widgetType, widgetPropertyPath);
	    int pos = 0;
	    boolean hasExpression = false;
	    while (matcher.find())
	    {
	    	hasExpression = true;
	    	if (pos != matcher.start())
	    	{
	    		String literal = trimPropertyValue.substring(pos, matcher.start());
	    		result.addStringConstant(literal);
	    	}
	    	pos = matcher.end();
	    	
	    	String group = matcher.group();
		    group = group.substring(2, group.length()-1);
			ExpressionPart expressionPart = getExpressionPart(group);
	    	result.addReadBinding(expressionPart);
	    }
	    if (!hasExpression)
	    {
	    	return null;
	    }
	    if (pos != trimPropertyValue.length() -1)
	    {
	    	result.addStringConstant(trimPropertyValue.substring(pos));
	    }
	    return result;
    }

	private ExpressionDataBinding getLogicalBindingExpression(JClassType widgetType, String widgetPropertyPath, 
			String trimPropertyValue)
    {
	    ExpressionDataBinding result;
	    trimPropertyValue = trimPropertyValue.substring(2, trimPropertyValue.length()-2);
	    int index = trimPropertyValue.indexOf('(');
	    String operator = trimPropertyValue.substring(0, index);
	    trimPropertyValue = trimPropertyValue.substring(index+1);
	    
	    String[] parts = trimPropertyValue.split("\\s");
	    List<ExpressionPart> expressionParts = new ArrayList<ExpressionPart>();
	    for (String part : parts)
        {
	    	ExpressionPart expressionPart = getExpressionPart(part);
	    	expressionParts.add(expressionPart);
        }
	    
	    result = new ExpressionDataBinding(widgetType, widgetPropertyPath);
	    boolean negate = operator.startsWith("NOT ");
	    if (negate)
	    {
	    	operator = operator.substring(4);
	    }
	    ExpressionDataBinding.LogicalOperations logicalOperations = ExpressionDataBinding.LogicalOperations.valueOf(operator.trim());
	    result.addLogicalBinding(expressionParts, logicalOperations, negate);
	    return result;
    }

	private ExpressionDataBinding getReadOnlyObjectBindingExpression(String widgetPropertyPath, JType widgetPropertyType, 
			JClassType widgetType, String trimPropertyValue)
    {
	    ExpressionDataBinding result;
	    trimPropertyValue = trimPropertyValue.substring(3, trimPropertyValue.length()-1);
	    ExpressionPart expressionPart = getExpressionPart(trimPropertyValue);
	    
	    result = new ExpressionDataBinding(widgetType, widgetPropertyPath);
	    result.addReadBinding(expressionPart);
	    if (!JClassUtils.isCompatibleTypes(widgetPropertyType, expressionPart.getType()))
	    {
	    	throw new CruxGeneratorException("Invalid binding declaration. DataObject property [" +
	    			expressionPart.getType() + "] can not be cast to Widget property ["+widgetPropertyPath+"]");
	    }
	    return result;
    }
	
	private PropertyBindInfo getPropertyBindInfo(String widgetClassName, boolean boundToAttribute, String widgetPropertyPath, String propertyValue)
    {
	    String[] bindParts = getBindingParts(propertyValue, true);
	    String dataObject = bindParts[0];
	    String bindPath = bindParts[1];
	    String converter = bindParts.length > 2 ? bindParts[2] : null;
	    String dataObjectClassName = DataObjects.getDataObject(dataObject);
 
	    addDataObject(dataObject);
	    
	    JClassType widgetType = context.getTypeOracle().findType(widgetClassName);
	    JClassType dataObjectType = context.getTypeOracle().findType(dataObjectClassName);
	    JClassType converterType = getConverterType(context, converter);
	    String converterParams = null;
	    if (bindParts.length > 3 && converterType != null && 
	    		converterType.findConstructor(new JType[]{context.getTypeOracle().findType(String.class.getCanonicalName())}) != null)
	    {
	    	converterParams = bindParts[3];
	    }
	    
	    checkDataObjectType(dataObject, dataObjectType);
	    try
	    {   
	    	return new PropertyBindInfo(widgetPropertyPath, boundToAttribute, bindPath, widgetType, dataObjectType, 
	    								converterType, dataObject, converterParams);
	    }
	    catch (NoSuchFieldException e)
	    {
	    	throw new CruxGeneratorException("DataObject ["+dataObject+"], refered on view ["+view.getId()+"], has an invalid bind expression ["+bindPath+"]", e);
	    }
    }

	private ExpressionPart getExpressionPart(String bindingDeclaration)
    {
	    String[] bindParts = getBindingParts(bindingDeclaration, false);
	    String dataObject = bindParts[0];
	    String bindPath = bindParts[1];
	    String converter = bindParts.length > 2 ? bindParts[2] : null;
	    String dataObjectClassName = DataObjects.getDataObject(dataObject);
 
	    addDataObject(dataObject);
	    
	    JClassType dataObjectType = context.getTypeOracle().findType(dataObjectClassName);
	    JClassType converterType = getConverterType(context, converter);
	    
	    String converterParams = null;
	    if (bindParts.length > 3 && converterType != null && 
	    		converterType.findConstructor(new JType[]{context.getTypeOracle().findType(String.class.getCanonicalName())}) != null)
	    {
	    	converterParams = bindParts[3];
	    }
	    
	    checkDataObjectType(dataObject, dataObjectType);
	    try
	    {   
	    	return new ExpressionPart(bindPath, dataObjectType, converterType, dataObject, converterParams);
	    }
	    catch (NoSuchFieldException e)
	    {
	    	throw new CruxGeneratorException("DataObject ["+dataObject+"], refered on view ["+view.getId()+"], has an invalid bind expression ["+bindPath+"]", e);
	    }
    }

	/**
     * Returns <code>true</code> if the given text is a binding declaration to a dataObject property.
	 * @param text
	 * @return <code>true</code> if the given text is a binding declaration.
	 */
	private boolean isObjectDataBinding(String text)
	{
		if (text!= null &&  RegexpPatterns.REGEXP_CRUX_OBJECT_DATA_BINDING.matcher(text).matches())
		{
			String[] parts = getBindingParts(text, true);
			return (DataObjects.getDataObject(parts[0]) != null);
		}
		return false;
	}	
	
	/**
	 * Split the dataBindReference and separate the DataObject Class alias from the requested property and converters
	 *
	 * @param text
	 * @param removeBraces
	 * @return
	 */
	private String[] getBindingParts(String text, boolean removeBraces)
	{
		boolean hasConverter = text.indexOf(":") > 0;
		boolean hasConverterParams = hasConverter && text.indexOf("(", text.indexOf(":")) > 0;
		if (removeBraces)
		{
			text = text.substring(2, text.length()-1);
		}
		String[] result = new String[hasConverterParams?4:hasConverter?3:2];
		int index = text.indexOf('.');
		result[0] = text.substring(0, index);
		String path = text.substring(index+1);
		if (hasConverter)
		{
			int endPathIndex = path.indexOf(':');
			result[1] = path.substring(0, endPathIndex);
			if (hasConverterParams)
			{
				int paramStartPath = path.indexOf('(', endPathIndex);
				result[2] = path.substring(endPathIndex+1, paramStartPath);
				result[3] = EscapeUtils.quote(path.substring(paramStartPath+2, path.length()-2)); 
			}
			else
			{
				result[2] = path.substring(endPathIndex+1);
			}
		}
		else
		{
			result[1] = path;
		}
		
		return result;
	}
	
	private void checkDataObjectType(String dataObject, JClassType dataObjectType)
    {
	    if (dataObjectType == null)
	    {
	    	String message = "DataObject ["+dataObject+"], refered on view ["+view.getId()+"], could not be loaded. "
	    	   + "\n Possible causes:"
	    	   + "\n\t 1. Check if any type or subtype used by resource refers to another module and if this module is inherited in the .gwt.xml file."
	    	   + "\n\t 2. Check if your resource or its members belongs to a client package."
	    	   + "\n\t 3. Check the versions of all your modules."
	    	   ;
	    	throw new CruxGeneratorException(message);
	    }
    }

	public static JClassType getConverterType(GeneratorContext context, String bindConverter)
    {
		JClassType converterType = null;
	    if (!StringUtils.isEmpty(bindConverter))
	    {
	    	String converterClassName = Converters.getConverter(bindConverter);
	    	converterType = context.getTypeOracle().findType(converterClassName);
	    }
	    return converterType;
    }
}
