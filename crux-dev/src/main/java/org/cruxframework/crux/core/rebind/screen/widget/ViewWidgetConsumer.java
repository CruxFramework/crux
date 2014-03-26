/*
 * Copyright 2013 cruxframework.org.
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
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.views.BindableView;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.converter.Converters;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.LazyCompatibleWidgetConsumer;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 *
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewWidgetConsumer implements LazyCompatibleWidgetConsumer
{
	private final ViewFactoryCreator viewFactoryCreator;
	private JClassType hasValueType;
	private JClassType hasFormatterType;
	private JClassType hasTextType;
	private JClassType typeConverterType;

	public ViewWidgetConsumer(ViewFactoryCreator viewFactoryCreator)
	{
		this.viewFactoryCreator = viewFactoryCreator;
	}

	public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem)
	{
		hasValueType = viewFactoryCreator.getContext().getTypeOracle().findType(HasValue.class.getCanonicalName());
		hasFormatterType = viewFactoryCreator.getContext().getTypeOracle().findType(HasFormatter.class.getCanonicalName());
		hasTextType = viewFactoryCreator.getContext().getTypeOracle().findType(HasText.class.getCanonicalName());
		typeConverterType = viewFactoryCreator.getContext().getTypeOracle().findType(TypeConverter.class.getCanonicalName());

		String bindPath = metaElem.optString("bindPath");
		String bindConverter = metaElem.optString("bindConverter");
		if (viewFactoryCreator.isDataBindEnabled() && !StringUtils.isEmpty(bindPath))
		{
			Class<?> widgetClass = viewFactoryCreator.getWidgetCreatorHelper(widgetType).getWidgetType();
			String dataObjectClassName = DataObjects.getDataObject(viewFactoryCreator.view.getDataObject());
			JClassType dataObjectType = viewFactoryCreator.getContext().getTypeOracle().findType(dataObjectClassName);
			JClassType widgetClassType = viewFactoryCreator.getContext().getTypeOracle().findType(widgetClass.getCanonicalName());
			
			try
			{
				out.println(ViewFactoryCreator.getViewVariable()+".addWidget("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +
						", new "+BindableView.class.getCanonicalName()+".PropertyBinder<"+dataObjectClassName+">(){");
				
				JClassType converterType = getConverterType(out, bindPath, bindConverter, dataObjectType, widgetClassType);
		    	String converterVariable = null;
		    	if (converterType != null)
		    	{
		    		converterVariable = "__converter";
		    		out.println(converterType.getParameterizedQualifiedSourceName()+" "+converterVariable+" = new "+converterType.getParameterizedQualifiedSourceName()+"();");
		    	}

				out.println("public void copyTo("+dataObjectClassName+" dataObject, Widget w){");
				generateCopyToCode(out, "dataObject", "w", dataObjectType, widgetClassType, bindPath, converterVariable, converterType);
				out.println("}");
				out.println("public void copyFrom(Widget w, "+dataObjectClassName+" dataObject){");
				generateCopyFromCode(out, "dataObject", "w", dataObjectType, widgetClassType, bindPath, converterVariable, converterType);
				out.println("}");
				out.println("});");
			}
			catch (NoSuchFieldException e) 
			{
				throw new CruxGeneratorException("Invalid binding path ["+bindPath+"] on target dataobject ["+dataObjectClassName+"]. Property not found.");
			}
		}
		else
		{
			out.println(ViewFactoryCreator.getViewVariable()+".addWidget("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +");");
		}

		if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().renderWidgetsWithIDs()))
		{
			out.println("ViewFactoryUtils.updateWidgetElementId("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +", "+ViewFactoryCreator.getViewVariable()+");");
		}
	}

	private JClassType getConverterType(SourcePrinter out, String bindPath, String bindConverter, JClassType dataObjectType, JClassType widgetClassType)
    {
		JClassType converterType = null;
	    if (!StringUtils.isEmpty(bindConverter))
	    {
	    	String converterClassName = Converters.getConverter(bindConverter);
	    	converterType = viewFactoryCreator.getContext().getTypeOracle().findType(converterClassName);
	    	JType propertyType = JClassUtils.getTypeForProperty(bindPath, dataObjectType);
	    	String propertyClassName = getPropertyClassName(propertyType);
	    	validateConverter(converterType, widgetClassType, viewFactoryCreator.getContext().getTypeOracle().findType(propertyClassName));
	    }
	    return converterType;
    }

	protected void generateCopyFromCode(SourcePrinter srcWriter, String dataObjectVariable, 
			String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, JClassType converterType) throws NoSuchFieldException
    {
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
			propertyClassName = getPropertyClassName(propertyType);
		}
		if (dataObjectType != null && widgetClass != null)
		{
			srcWriter.println("if (dataObject != null){");
			String getExpression;
			if (widgetClass.isAssignableTo(hasValueType))
			{
				getExpression = getNullSafeExpression("(("+HasValue.class.getCanonicalName()+"<"+propertyClassName+">)"+widgetVariable+").getValue()", 
												propertyType, bindPath, dataObjectClassName, converterVariable);
			}
			else if (widgetClass.isAssignableTo(hasFormatterType))
			{
				getExpression = getNullSafeExpression("("+propertyClassName+")(("+HasFormatter.class.getCanonicalName()+")w).getUnformattedValue()", 
												propertyType, bindPath, dataObjectClassName, converterVariable);
			} 
			else if (widgetClass.isAssignableTo(hasTextType))
			{
				getExpression = getNullSafeExpression("(("+HasText.class.getCanonicalName()+")w).getText()", propertyType, bindPath, dataObjectClassName, 
												converterVariable);
			}
			else
			{
				getExpression = getEmptyValueExpression(propertyType, bindPath, dataObjectClassName);
			}
			
			JClassUtils.buildSetValueExpression(srcWriter, dataObjectType, bindPath, dataObjectVariable, getExpression);
			srcWriter.println("}");
		}
    }

	protected String getNullSafeExpression(String expression, JType propertyType, String bindPath, String dataObjectClassName, String converterVariable)
	{
		if (converterVariable != null)
		{
			expression = converterVariable+".from(" + expression + ")";
		}
		String getExpression;
		JPrimitiveType primitiveType = propertyType.isPrimitive();
		if (primitiveType == null)
		{
			getExpression = "(w==null?null:"+expression+")";
		}
		else if (primitiveType.equals(JPrimitiveType.BOOLEAN))
		{
			getExpression = "(w==null?false:"+expression+"==null?false:"+expression+")";
		}
		else if (!primitiveType.equals(JPrimitiveType.VOID))
		{
			getExpression = "(w==null?0:"+expression+"==null?0:"+expression+")";
		}
		else
		{
			throw new CruxGeneratorException("Invalid binding path ["+bindPath+"] on target dataObject ["+dataObjectClassName+"]. Property can not be void.");
		}
		return getExpression;
	}
	
	protected String getEmptyValueExpression(JType propertyType, String bindPath, String dataObjectClassName)
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

	protected void generateCopyToCode(SourcePrinter srcWriter, String dataObjectVariable, 
			String widgetVariable, JClassType dataObjectType, JClassType widgetClass, String bindPath, String converterVariable, JClassType converterType) throws NoSuchFieldException
	{
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
				propertyClassName = getPropertyClassName(propertyType);
			}
			srcWriter.println("if (w != null){");
			if (widgetClass.isAssignableTo(hasValueType))
			{
				srcWriter.println("(("+HasValue.class.getCanonicalName()+"<"+propertyClassName+">)"+widgetVariable+").setValue("+propertyGetExpression+");");
			}
			else if (widgetClass.isAssignableTo(hasFormatterType))
			{
				if (propertyType.isPrimitive() != null)
				{
					srcWriter.println("(("+HasFormatter.class.getCanonicalName()+")w).setUnformattedValue(("+propertyClassName+")"+propertyGetExpression+");");
				}
				else
				{
					srcWriter.println("(("+HasFormatter.class.getCanonicalName()+")w).setUnformattedValue("+propertyGetExpression+");");
				}
			} 
			else if (widgetClass.isAssignableTo(hasTextType))
			{
				srcWriter.println("(("+HasText.class.getCanonicalName()+")w).setText("+propertyGetExpression+");");
			} 
			srcWriter.println("}");
		}
	}
	
	private void validateConverter(JClassType converterType, JClassType widgetClass, JClassType propertyType)
	{
		JClassType[] types = JClassUtils.getActualParameterTypes(converterType, typeConverterType);
		JClassType[] widgetValueType = JClassUtils.getActualParameterTypes(widgetClass, hasValueType);
		if (!propertyType.isAssignableTo(types[0]))
		{
			throw new CruxGeneratorException("converter ["+converterType.getQualifiedSourceName()+
					"] can not be used to convert values to widget of type ["+widgetClass.getQualifiedSourceName()+"]. Incompatible types.");
		}
		if (!widgetValueType[0].isAssignableTo(types[1]))
		{
			throw new CruxGeneratorException("converter ["+converterType.getQualifiedSourceName()+
					"] can not be used to convert values to property of type ["+propertyType.getQualifiedSourceName()+"]. Incompatible types.");
		}
	}

	private String getPropertyClassName(JType propertyType)
    {
	    String propertyClassName;
	    if (propertyType.isPrimitive() != null)
	    {
	    	propertyClassName = propertyType.isPrimitive().getQualifiedBoxedSourceName();
	    }
	    else
	    {
	    	propertyClassName = propertyType.getParameterizedQualifiedSourceName();
	    }
	    return propertyClassName;
    }


	@Override
	public void handleLazyWholeWidgetCreation(SourcePrinter out, String widgetId)
	{
		out.println(ViewFactoryCreator.getViewVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+
				EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget)) +");");
	}

	@Override
	public void handleLazyWrapChildrenCreation(SourcePrinter out, String widgetId)
	{
		out.println(ViewFactoryCreator.getViewVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+
				EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapChildren)) +");");
	}
}
