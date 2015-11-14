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
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.DataBindingProcessor;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;

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
	private RebindContext context;
	private Map<String, String> dataObjectBinderVariables = new HashMap<String, String>();
	private Set<String> dataObjects = new HashSet<String>();
	private View view;
	private ViewFactoryCreator viewFactoryCreator;

	public ViewBindHandler(RebindContext context, View view, ViewFactoryCreator viewFactoryCreator)
    {
		this.context = context;
		this.view = view;
		this.viewFactoryCreator = viewFactoryCreator;
    }
	
	protected void addDataObject(String dataObject)
	{
		dataObjects.add(dataObject);
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
			String dataObjectClassName = context.getDataObjects().getDataObject(dataObjectAlias);//getObjectDataBinding(dataObjectAlias).getDataObjectClassName();
			
			out.println("final " + DataObjectBinder.class.getCanonicalName() + "<" + dataObjectClassName + "> " + dataObjectBinder + "=" + 
					ViewFactoryCreator.getViewVariable() + ".getDataObjectBinder("+EscapeUtils.quote(dataObjectAlias)+");");
		}
		return dataObjectBinder;
	}
	
	protected ExpressionDataBinding getExpressionDataBinding(String propertyValue, String widgetClassName, 
			String widgetPropertyPath, String uiObjectClassName, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor,
			String setterMethod)
	{
		if (propertyValue == null)
		{
			return null;
		}
	    JClassType widgetType = context.getGeneratorContext().getTypeOracle().findType(widgetClassName);
	    JClassType uiObjectType = (uiObjectClassName!=null?context.getGeneratorContext().getTypeOracle().findType(uiObjectClassName):widgetType);
	    JType widgetPropertyType = JClassUtils.getPropertyType(uiObjectType, widgetPropertyPath);
	    if (widgetPropertyType == null)
	    {
	    	throw new CruxGeneratorException("Can not find out the widget property type, for property ["+widgetPropertyPath+"], on widget ["+widgetClassName+"]");
	    }
		String trimPropertyValue = propertyValue.trim();
		if (RegexpPatterns.REGEXP_CRUX_READ_ONLY_OBJECT_DATA_BINDING.matcher(trimPropertyValue).matches())
		{
			return getReadOnlyObjectBindingExpression(widgetPropertyPath, widgetPropertyType, widgetType, 
														trimPropertyValue, uiObjectType, getUiObjectExpression, dataBindingProcessor, setterMethod);
		}
		else if (widgetPropertyType == JPrimitiveType.BOOLEAN || Boolean.class.getCanonicalName().equals(widgetPropertyType.getQualifiedSourceName()))
		{
			if (RegexpPatterns.REGEXP_CRUX_EXPRESSION_DATA_BINDING.matcher(trimPropertyValue).matches())
			{
				return getLogicalBindingExpression(widgetType, widgetPropertyPath, trimPropertyValue, uiObjectType, 
					getUiObjectExpression, dataBindingProcessor, setterMethod);
			}
		}
		else
		{
			return getMultipleBindingsExpression(widgetType, widgetPropertyPath, trimPropertyValue, uiObjectType, 
				getUiObjectExpression, dataBindingProcessor, setterMethod);
		}
		
		return null;
	}
	
	protected PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath,
													boolean boundToAttribute, String uiObjectClassName, String getUiObjectExpression, 
													DataBindingProcessor dataBindingProcessor)
	{
		String trimPropertyValue = propertyValue.trim();
	    if (isObjectDataBinding(trimPropertyValue, dataBindingProcessor))
	    {
	    	return getPropertyBindInfo(widgetClassName, boundToAttribute, widgetPropertyPath, trimPropertyValue, uiObjectClassName,
	    							getUiObjectExpression, dataBindingProcessor);
	    }
	    else
	    {
	    	return null;
	    }
	}

	protected Iterator<String> iterateDataObjects()
    {
		return dataObjects.iterator();
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
	
	
	private ExpressionPart getExpressionPart(String bindingDeclaration, DataBindingProcessor dataBindingProcessor)
    {
	    String[] bindParts = getBindingParts(bindingDeclaration, false);
	    String dataObject = bindParts[0];
	    String bindPath = bindParts[1];
	    String converter = bindParts.length > 2 ? bindParts[2] : null;
	    String dataObjectAlias = dataBindingProcessor.getDataObjectAlias(dataObject);
		String dataObjectClassName = context.getDataObjects().getDataObject(dataObjectAlias);
 
		if (dataObjectAlias.equals(dataObject))
		{
			addDataObject(dataObject);
		}
	    
	    JClassType dataObjectType = context.getGeneratorContext().getTypeOracle().findType(dataObjectClassName);
	    JClassType converterType = getConverterType(context, converter);
	    
	    String converterParams = null;
	    if (bindParts.length > 3 && converterType != null && 
	    		converterType.findConstructor(new JType[]{context.getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName())}) != null)
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

	private ExpressionDataBinding getLogicalBindingExpression(JClassType widgetType, String widgetPropertyPath, 
			String trimPropertyValue, JClassType uiObjectType, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor,
			String setterMethod)
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
	    	ExpressionPart expressionPart = getExpressionPart(part, dataBindingProcessor);
	    	expressionParts.add(expressionPart);
        }
	    
	    result = new ExpressionDataBinding(context, widgetType, widgetPropertyPath, uiObjectType, getUiObjectExpression, setterMethod);
	    boolean negate = operator.startsWith("NOT ");
	    if (negate)
	    {
	    	operator = operator.substring(4);
	    }
	    ExpressionDataBinding.LogicalOperation logicalOperations = ExpressionDataBinding.LogicalOperation.valueOf(operator.trim());
	    result.addLogicalBinding(expressionParts, logicalOperations, negate);
	    return result;
    }
	
	private ExpressionDataBinding getMultipleBindingsExpression(JClassType widgetType, String widgetPropertyPath, 
			String trimPropertyValue, JClassType uiObjectType, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor,
			String setterMethod)
    {
	    ExpressionDataBinding result;
	    Matcher matcher = RegexpPatterns.REGEXP_CRUX_OBJECT_DATA_BINDING.matcher(trimPropertyValue);
	    result = new ExpressionDataBinding(context, widgetType, widgetPropertyPath, uiObjectType, getUiObjectExpression, setterMethod);
	    int pos = 0;
	    boolean hasExpression = false;
	    while (matcher.find())
	    {
	    	hasExpression = true;
	    	if (pos != matcher.start())
	    	{
	    		String literal = resolveI18NString(trimPropertyValue.substring(pos, matcher.start()));
	    		result.addStringConstant(literal, false);
	    	}
	    	pos = matcher.end();
	    	
	    	String group = matcher.group();
		    group = group.substring(2, group.length()-1);
			ExpressionPart expressionPart = getExpressionPart(group, dataBindingProcessor);
	    	result.addReadBinding(expressionPart);
	    }
	    if (!hasExpression)
	    {
	    	return null;
	    }
	    if (pos != trimPropertyValue.length())
	    {
	    	String i18nString = resolveI18NString(trimPropertyValue.substring(pos));
			result.addStringConstant(i18nString, false);
	    }
	    return result;
    }

	private PropertyBindInfo getPropertyBindInfo(String widgetClassName, boolean boundToAttribute, String widgetPropertyPath, String propertyValue,
												String uiObjectClassName, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor)
    {
	    String[] bindParts = getBindingParts(propertyValue, true);
	    String dataObject = bindParts[0];
	    String bindPath = bindParts[1];
	    String converter = bindParts.length > 2 ? bindParts[2] : null;
	    String dataObjectAlias = dataBindingProcessor.getDataObjectAlias(dataObject);
	    String dataObjectClassName = context.getDataObjects().getDataObject(dataObjectAlias);
 
		if (dataObjectAlias.equals(dataObject))
		{
			addDataObject(dataObject);
		}
	    
	    JClassType widgetType = context.getGeneratorContext().getTypeOracle().findType(widgetClassName);
	    JClassType uiObjectType = uiObjectClassName!= null?context.getGeneratorContext().getTypeOracle().findType(uiObjectClassName): widgetType;
	    JClassType dataObjectType = context.getGeneratorContext().getTypeOracle().findType(dataObjectClassName);
	    JClassType converterType = getConverterType(context, converter);
	    String converterParams = null;
	    if (bindParts.length > 3 && converterType != null && 
	    		converterType.findConstructor(new JType[]{context.getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName())}) != null)
	    {
	    	converterParams = bindParts[3];
	    }
	    
	    checkDataObjectType(dataObject, dataObjectType);
	    try
	    {   
	    	return new PropertyBindInfo(widgetPropertyPath, boundToAttribute, bindPath, widgetType, dataObjectType, 
	    								converterType, dataObject, converterParams, uiObjectType, getUiObjectExpression);
	    }
	    catch (NoSuchFieldException e)
	    {
	    	throw new CruxGeneratorException("DataObject ["+dataObject+"], refered on view ["+view.getId()+"], has an invalid bind expression ["+bindPath+"]", e);
	    }
    }

	private ExpressionDataBinding getReadOnlyObjectBindingExpression(String widgetPropertyPath, JType widgetPropertyType, 
			JClassType widgetType, String trimPropertyValue, JClassType uiObjectType, String getUiObjectExpression, 
			DataBindingProcessor dataBindingProcessor, String setterMethod)
    {
	    ExpressionDataBinding result;
	    trimPropertyValue = trimPropertyValue.substring(3, trimPropertyValue.length()-1);
	    ExpressionPart expressionPart = getExpressionPart(trimPropertyValue, dataBindingProcessor);
	    
	    result = new ExpressionDataBinding(context, widgetType, widgetPropertyPath, uiObjectType, getUiObjectExpression, setterMethod);
	    result.addReadBinding(expressionPart);
	    if (!JClassUtils.isCompatibleTypes(widgetPropertyType, expressionPart.getType()))
	    {
	    	throw new CruxGeneratorException("Invalid binding declaration. DataObject property [" +
	    			trimPropertyValue + "] can not be cast to type ["+widgetPropertyType.getSimpleSourceName()+"]");
	    }
	    return result;
    }	
	
	/**
     * Returns <code>true</code> if the given text is a binding declaration to a dataObject property.
	 * @param text
	 * @param dataBindingProcessor 
	 * @return <code>true</code> if the given text is a binding declaration.
	 */
	private boolean isObjectDataBinding(String text, DataBindingProcessor dataBindingProcessor)
	{
		if (text!= null &&  RegexpPatterns.REGEXP_CRUX_OBJECT_DATA_BINDING.matcher(text).matches())
		{
			String[] parts = getBindingParts(text, true);
			
			String dataObjectAlias = dataBindingProcessor.getDataObjectAlias(parts[0]);
			return (context.getDataObjects().getDataObject(dataObjectAlias) != null);
		}
		return false;
	}
	
	private String resolveI18NString(String text)
	{
		Matcher matcher = RegexpPatterns.REGEXP_CRUX_MESSAGE.matcher(text);
	    int pos = 0;
	    StringBuilder result = new StringBuilder();
	    while (matcher.find())
	    {
	    	if (pos != matcher.start())
	    	{
	    		String literal = text.substring(pos, matcher.start());
	    		result.append(EscapeUtils.quote(literal));
	    	}
	    	pos = matcher.end();
	    	String group = matcher.group();
		    if (result.length() > 0)
		    {
		    	result.append("+");
		    }
		    
		    result.append(viewFactoryCreator.getDeclaredMessage(group));
	    }	    	
	    
	    if (pos != text.length())
	    {
		    if (result.length() > 0)
		    {
		    	result.append("+");
		    }
			result.append(EscapeUtils.quote(text.substring(pos)));
	    }
	    
	    return result.toString();
	}

	public static JClassType getConverterType(RebindContext context, String bindConverter)
    {
		JClassType converterType = null;
	    if (!StringUtils.isEmpty(bindConverter))
	    {
	    	String converterClassName = context.getConverters().getConverter(bindConverter);
	    	converterType = context.getGeneratorContext().getTypeOracle().findType(converterClassName);
	    }
	    return converterType;
    }
}
