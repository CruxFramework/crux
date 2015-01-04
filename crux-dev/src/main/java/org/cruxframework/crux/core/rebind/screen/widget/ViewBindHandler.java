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

import java.util.Iterator;
import java.util.Set;

import org.cruxframework.crux.core.client.converter.TypeConverter;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.converter.Converters;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ObjectDataBinding.PropertyBindInfo;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewBindHandler
{
	private View view;
	private GeneratorContext context;
	private Set<String> dataObjects = new HashSet<String>();

	public ViewBindHandler(GeneratorContext context, View view)
    {
		this.context = context;
		this.view = view;
    }
	
	protected void addDataObject(String dataObject)
	{
		dataObjects.add(dataObject);
	}
	
	protected Iterator<String> iterateDataObjects()
    {
		return dataObjects.iterator();
    }
	
	protected PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath)
	{
	    if (isObjectDataBinding(propertyValue))
	    {
	    	String[] bindParts = ViewFactoryCreator.getDataObjectParts(propertyValue);
	    	String dataObject = bindParts[0];
	    	String bindPath = bindParts[1];
	    	String converter = bindParts.length > 2 ? bindParts[2] : null;
	    	String dataObjectClassName = DataObjects.getDataObject(dataObject);
	 
	    	addDataObject(dataObject);
	    	
	    	JClassType widgetType = context.getTypeOracle().findType(widgetClassName);
			JClassType dataObjectType = context.getTypeOracle().findType(dataObjectClassName);
			JClassType converterType = getConverterType(context, bindPath, converter, dataObjectType, widgetType);
			String converterParams = null;
			if (bindParts.length > 3 && converterType != null && 
					converterType.findConstructor(new JType[]{context.getTypeOracle().findType(String.class.getCanonicalName())}) != null)
			{
				converterParams = bindParts[3];
			}
			
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
            try
            {   
            	return new PropertyBindInfo(widgetPropertyPath, bindPath, widgetType, dataObjectType, converterType, dataObject, converterParams);
            }
            catch (NoSuchFieldException e)
            {
				throw new CruxGeneratorException("DataObject ["+dataObject+"], refered on view ["+view.getId()+"], has an invalid bind expression ["+bindPath+"]", e);
            }
	    }
	    else
	    {
	    	return null;
	    }
	}

    /**
     * Returns <code>true</code> if the given text is a binding declaration to a dataObject property.
	 * @param text
	 * @return <code>true</code> if the given text is a binding declaration.
	 */
	protected boolean isObjectDataBinding(String text)
	{
		if (text!= null &&  RegexpPatterns.REGEXP_CRUX_OBJECT_DATA_BINDING.matcher(text).matches())
		{
			String[] parts = ViewFactoryCreator.getDataObjectParts(text);
			return (DataObjects.getDataObject(parts[0]) != null);
		}
		return false;
	}	
	
	public static String getEmptyValueExpression(JType propertyType, String bindPath, String dataObjectClassName)
	{
		String getExpression;
		JPrimitiveType primitiveType = propertyType.isPrimitive();
		if (primitiveType == null)
		{
			getExpression = "null";
		}
		else if (primitiveType.equals(JPrimitiveType.BOOLEAN))
		{
			getExpression = "false";
		}
		else if (!primitiveType.equals(JPrimitiveType.VOID))
		{
			getExpression = "0";
		}
		else
		{
			throw new CruxGeneratorException("Invalid binding path ["+bindPath+"] on target dataObject ["+dataObjectClassName+"]. Property can not be void.");
		}
		return getExpression;
	}
	
	public static JClassType getConverterType(GeneratorContext context, String bindPath, String bindConverter, JClassType dataObjectType, JClassType widgetClassType)
    {
		JClassType converterType = null;
	    if (!StringUtils.isEmpty(bindConverter))
	    {
	    	String converterClassName = Converters.getConverter(bindConverter);
	    	converterType = context.getTypeOracle().findType(converterClassName);
	    	JType propertyType = JClassUtils.getTypeForProperty(bindPath, dataObjectType);
	    	String propertyClassName = JClassUtils.getGenericDeclForType(propertyType);
	    	validateConverter(converterType, context, widgetClassType, context.getTypeOracle().findType(propertyClassName));
	    }
	    return converterType;
    }
	
	public static void validateConverter(JClassType converterType, GeneratorContext context, JClassType widgetClass, JClassType propertyType)
	{
		JClassType hasValueType = context.getTypeOracle().findType(HasValue.class.getCanonicalName());
		JClassType hasTextType = context.getTypeOracle().findType(HasText.class.getCanonicalName());
		JClassType typeConverterType = context.getTypeOracle().findType(TypeConverter.class.getCanonicalName());
		JClassType stringType = context.getTypeOracle().findType(String.class.getCanonicalName());

		JClassType[] types = JClassUtils.getActualParameterTypes(converterType, typeConverterType);
		JClassType widgetType = null;

		if (widgetClass.isAssignableTo(hasValueType))
		{
			JClassType[] widgetValueType = JClassUtils.getActualParameterTypes(widgetClass, hasValueType);
			widgetType = widgetValueType[0];
		}
		else if (widgetClass.isAssignableTo(hasTextType))
		{
			widgetType = stringType;
		}
		else
		{
			throw new CruxGeneratorException("converter ["+converterType.getQualifiedSourceName()+
					"] can not be used to convert values to widget of type ["+widgetClass.getQualifiedSourceName()+"]. Incompatible types.");
		}
		if (!propertyType.isAssignableTo(types[0]))
		{
			throw new CruxGeneratorException("converter ["+converterType.getQualifiedSourceName()+
					"] can not be used to convert values to widget of type ["+widgetClass.getQualifiedSourceName()+"]. Incompatible types.");
		}
		if (!widgetType.isAssignableTo(types[1]))
		{
			throw new CruxGeneratorException("converter ["+converterType.getQualifiedSourceName()+
					"] can not be used to convert values to property of type ["+propertyType.getQualifiedSourceName()+"]. Incompatible types.");
		}
	}
	
	public static String getNullSafeExpression(String widgetExpression, JType propertyType, String bindPath, 
						String dataObjectClassName, String converterVariable, String widgetVariable)
	{
		if (converterVariable != null)
		{
			widgetExpression = converterVariable+".from(" + widgetExpression + ")";
		}
		String getExpression;
		JPrimitiveType primitiveType = propertyType.isPrimitive();
		if (primitiveType == null)
		{
			getExpression = "("+widgetVariable+"==null?null:"+widgetExpression+")";
		}
		else if (primitiveType.equals(JPrimitiveType.BOOLEAN))
		{
			getExpression = "("+widgetVariable+"==null?false:"+widgetExpression+"==null?false:"+widgetExpression+")";
		}
		else if (!primitiveType.equals(JPrimitiveType.VOID))
		{
			getExpression = "("+widgetVariable+"==null?0:"+widgetExpression+"==null?0:"+widgetExpression+")";
		}
		else
		{
			throw new CruxGeneratorException("Invalid binding path ["+bindPath+"] on target dataObject ["+dataObjectClassName+"]. Property can not be void.");
		}
		return getExpression;
	}
}
