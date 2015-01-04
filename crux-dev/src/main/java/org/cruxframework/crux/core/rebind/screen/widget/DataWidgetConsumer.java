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
import org.cruxframework.crux.core.client.formatter.HasFormatter;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataWidgetConsumer
{
	public static void generateCopyFromCode(SourcePrinter srcWriter, GeneratorContext context, String dataObjectVariable, 
			String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, 
			JClassType converterType, boolean skipCheckings) throws NoSuchFieldException
    {
		JClassType hasValueType = context.getTypeOracle().findType(HasValue.class.getCanonicalName());
		JClassType hasFormatterType = context.getTypeOracle().findType(HasFormatter.class.getCanonicalName());
		JClassType hasTextType = context.getTypeOracle().findType(HasText.class.getCanonicalName());
		JClassType typeConverterType = context.getTypeOracle().findType(TypeConverter.class.getCanonicalName());

		JType propertyType = JClassUtils.getTypeForProperty(bindPath, dataObjectType);
		String dataObjectClassName = dataObjectType.getParameterizedQualifiedSourceName();
		String propertyClassName;
		if (converterVariable != null)
		{
			JClassType[] types = JClassUtils.getActualParameterTypes(converterType, typeConverterType);
			propertyClassName = types[1].getParameterizedQualifiedSourceName();
		}
		else
		{
			propertyClassName = JClassUtils.getGenericDeclForType(propertyType);
		}
		if (dataObjectType != null && widgetClass != null)
		{
			if (!skipCheckings)
			{
				srcWriter.println("if ("+dataObjectVariable+" != null){");
				generateDataobjectValueSetWithCheckings(srcWriter, dataObjectVariable, widgetVariable, dataObjectType, widgetClass, bindPath, converterVariable, hasValueType, hasFormatterType, hasTextType, propertyType, dataObjectClassName, propertyClassName);
				srcWriter.println("}");
			}
			else
			{
				generateDataobjectValueSetWithNoCheckings(srcWriter, dataObjectVariable, widgetVariable, dataObjectType, widgetClass, bindPath, converterVariable, hasValueType, hasFormatterType, hasTextType, propertyType, dataObjectClassName, propertyClassName);
			}
		}
    }
	
	public static String getPropertyReadExpression(JClassType dataObjectType, String dataObjectVariable, 
												   String converterVariable,  String bindPath) throws NoSuchFieldException
	{
		if (dataObjectType != null)
		{
			StringBuilder propertyGetExpression = new StringBuilder();
			JClassUtils.buildGetValueExpression(propertyGetExpression, dataObjectType, bindPath, dataObjectVariable, false);
			
			if (converterVariable != null)
			{
				propertyGetExpression.insert(0, converterVariable+".to(").append(")");
			}
			
			return propertyGetExpression.toString();
		}
		
		return null;
	}
	
	
	public static void generateCopyToCode(SourcePrinter srcWriter, GeneratorContext context, String dataObjectVariable, 
			String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, 
			JClassType converterType, boolean skipCheckings) throws NoSuchFieldException
	{
		JClassType hasValueType = context.getTypeOracle().findType(HasValue.class.getCanonicalName());
		JClassType hasFormatterType = context.getTypeOracle().findType(HasFormatter.class.getCanonicalName());
		JClassType hasTextType = context.getTypeOracle().findType(HasText.class.getCanonicalName());
		JClassType typeConverterType = context.getTypeOracle().findType(TypeConverter.class.getCanonicalName());

		if (dataObjectType != null && widgetClass != null)
		{
			StringBuilder propertyGetExpression = new StringBuilder();
			JType propertyType = JClassUtils.buildGetValueExpression(propertyGetExpression, dataObjectType, bindPath, dataObjectVariable, false);
			String propertyClassName;
			
			if (converterVariable != null)
			{
				propertyGetExpression.insert(0, converterVariable+".to(").append(")");
				JClassType[] types = JClassUtils.getActualParameterTypes(converterType, typeConverterType);
				propertyClassName = types[1].getParameterizedQualifiedSourceName();
			}
			else
			{
				propertyClassName = JClassUtils.getGenericDeclForType(propertyType);
			}
			if (!skipCheckings)
			{
				srcWriter.println("if ("+widgetVariable+" != null){");
				generateWidgetValueSetWithCheckings(srcWriter, widgetVariable, widgetClass, hasValueType, hasFormatterType, hasTextType, propertyGetExpression.toString(), propertyType, propertyClassName); 
				srcWriter.println("}");
			}
			else
			{
				generateWidgetValueSetWithNoCheckings(srcWriter, widgetVariable, widgetClass, hasValueType, hasFormatterType, hasTextType, propertyGetExpression.toString(), propertyType, propertyClassName); 
			}
		}
	}

	private static void generateWidgetValueSetWithNoCheckings(SourcePrinter srcWriter, String widgetVariable, JClassType widgetClass, JClassType hasValueType, JClassType hasFormatterType, JClassType hasTextType, String propertyGetExpression, JType propertyType,
            String propertyClassName)
    {
	    if (widgetClass.isAssignableTo(hasValueType))
	    {
	    	srcWriter.println(widgetVariable+".setValue("+propertyGetExpression+");");
	    }
	    else if (widgetClass.isAssignableTo(hasFormatterType))
	    {
	    	if (propertyType.isPrimitive() != null)
	    	{
	    		srcWriter.println(widgetVariable+".setUnformattedValue(("+propertyClassName+")"+propertyGetExpression+");");
	    	}
	    	else
	    	{
	    		srcWriter.println(widgetVariable+".setUnformattedValue("+propertyGetExpression+");");
	    	}
	    } 
	    else if (widgetClass.isAssignableTo(hasTextType))
	    {
	    	srcWriter.println(widgetVariable+".setText("+propertyGetExpression+");");
	    }
    }

	private static void generateWidgetValueSetWithCheckings(SourcePrinter srcWriter, String widgetVariable, JClassType widgetClass, JClassType hasValueType, JClassType hasFormatterType, JClassType hasTextType, String propertyGetExpression, JType propertyType,
            String propertyClassName)
    {
	    if (widgetClass.isAssignableTo(hasValueType))
	    {
	    	srcWriter.println("(("+HasValue.class.getCanonicalName()+"<"+propertyClassName+">)"+widgetVariable+").setValue("+propertyGetExpression+");");
	    }
	    else if (widgetClass.isAssignableTo(hasFormatterType))
	    {
	    	if (propertyType.isPrimitive() != null)
	    	{
	    		srcWriter.println("(("+HasFormatter.class.getCanonicalName()+")"+widgetVariable+").setUnformattedValue(("+propertyClassName+")"+propertyGetExpression+");");
	    	}
	    	else
	    	{
	    		srcWriter.println("(("+HasFormatter.class.getCanonicalName()+")"+widgetVariable+").setUnformattedValue("+propertyGetExpression+");");
	    	}
	    } 
	    else if (widgetClass.isAssignableTo(hasTextType))
	    {
	    	srcWriter.println("(("+HasText.class.getCanonicalName()+")"+widgetVariable+").setText("+propertyGetExpression+");");
	    }
    }
	
	private static void generateDataobjectValueSetWithNoCheckings(SourcePrinter srcWriter, String dataObjectVariable, String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, JClassType hasValueType, JClassType hasFormatterType,
            JClassType hasTextType, JType propertyType, String dataObjectClassName, String propertyClassName) throws NoSuchFieldException
    {
	    String getExpression;
	    if (widgetClass.isAssignableTo(hasValueType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression(widgetVariable+".getValue()", 
	    									propertyType, bindPath, dataObjectClassName, converterVariable, widgetVariable);
	    }
	    else if (widgetClass.isAssignableTo(hasFormatterType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression("("+propertyClassName+")"+widgetVariable+".getUnformattedValue()", 
	    									propertyType, bindPath, dataObjectClassName, converterVariable, widgetVariable);
	    } 
	    else if (widgetClass.isAssignableTo(hasTextType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression(widgetVariable+".getText()", propertyType, bindPath, dataObjectClassName, 
	    									converterVariable, widgetVariable);
	    }
	    else
	    {
	    	getExpression = ViewBindHandler.getEmptyValueExpression(propertyType, bindPath, dataObjectClassName);
	    }
	    
	    JClassUtils.buildSetValueExpression(srcWriter, dataObjectType, bindPath, dataObjectVariable, getExpression);
    }

	private static void generateDataobjectValueSetWithCheckings(SourcePrinter srcWriter, String dataObjectVariable, String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, JClassType hasValueType, JClassType hasFormatterType,
            JClassType hasTextType, JType propertyType, String dataObjectClassName, String propertyClassName) throws NoSuchFieldException
    {
	    String getExpression;
	    if (widgetClass.isAssignableTo(hasValueType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression("(("+HasValue.class.getCanonicalName()+"<"+propertyClassName+">)"+widgetVariable+").getValue()", 
	    									propertyType, bindPath, dataObjectClassName, converterVariable, widgetVariable);
	    }
	    else if (widgetClass.isAssignableTo(hasFormatterType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression("("+propertyClassName+")(("+HasFormatter.class.getCanonicalName()+")"+widgetVariable+").getUnformattedValue()", 
	    									propertyType, bindPath, dataObjectClassName, converterVariable, widgetVariable);
	    } 
	    else if (widgetClass.isAssignableTo(hasTextType))
	    {
	    	getExpression = ViewBindHandler.getNullSafeExpression("(("+HasText.class.getCanonicalName()+")"+widgetVariable+").getText()", propertyType, bindPath, dataObjectClassName, 
	    									converterVariable, widgetVariable);
	    }
	    else
	    {
	    	getExpression = ViewBindHandler.getEmptyValueExpression(propertyType, bindPath, dataObjectClassName);
	    }
	    
	    JClassUtils.buildSetValueExpression(srcWriter, dataObjectType, bindPath, dataObjectVariable, getExpression);
    }
}
