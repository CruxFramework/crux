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
import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.views.BindableView;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.LazyCompatibleWidgetConsumer;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 *
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewWidgetConsumer extends DataWidgetConsumer implements LazyCompatibleWidgetConsumer
{
	private final ViewFactoryCreator viewFactoryCreator;

	public ViewWidgetConsumer(ViewFactoryCreator viewFactoryCreator)
	{
		this.viewFactoryCreator = viewFactoryCreator;
	}

	public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem)
	{
		String bindPath = metaElem.optString("bindPath");
		String bindConverter = metaElem.optString("bindConverter");
		if (viewFactoryCreator.isBindableView() && !StringUtils.isEmpty(bindPath))
		{
			Class<?> widgetClass = viewFactoryCreator.getWidgetCreatorHelper(widgetType).getWidgetType();
			String dataObjectClassName = DataObjects.getDataObject(viewFactoryCreator.view.getDataObject());
			JClassType dataObjectType = viewFactoryCreator.getContext().getGeneratorContext().getTypeOracle().findType(dataObjectClassName);
			JClassType widgetClassType = viewFactoryCreator.getContext().getGeneratorContext().getTypeOracle().findType(widgetClass.getCanonicalName());
			
			try
			{
				out.println(ViewFactoryCreator.getViewVariable()+".addWidget("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +
						", new "+BindableView.class.getCanonicalName()+".PropertyBinder<"+dataObjectClassName+">(){");
				
				JClassType converterType = ViewBindHandler.getConverterType(viewFactoryCreator.getContext(), bindConverter);
		    	String converterVariable = null;
		    	if (converterType != null)
		    	{
			    	JType propertyType = JClassUtils.getTypeForProperty(bindPath, dataObjectType);
			    	if (propertyType == null)
			    	{
			    		throw new CruxGeneratorException("No Data Object type declared for binding path [" + bindPath + "] "
			    				+ "for type " + widgetClassType.getName()
			    				+ ". Have you used the annotation " + DataObject.class.getCanonicalName() + "?");
			    	}
			    	String propertyClassName = JClassUtils.getGenericDeclForType(propertyType);
			    	validateConverter(converterType, viewFactoryCreator.getContext(), widgetClassType, 
			    			viewFactoryCreator.getContext().getGeneratorContext().getTypeOracle().findType(propertyClassName));
		    		
		    		converterVariable = "__converter";
		    		out.println(converterType.getParameterizedQualifiedSourceName()+" "+converterVariable+" = new "+converterType.getParameterizedQualifiedSourceName()+"();");
		    	}

				out.println("public void copyTo("+dataObjectClassName+" dataObject, Widget w){");
				generateCopyToCode(out, viewFactoryCreator.getContext(), "dataObject", "w", dataObjectType, widgetClassType, bindPath, converterVariable, converterType, false);
				out.println("}");
				out.println("public void copyFrom(Widget w, "+dataObjectClassName+" dataObject){");
				generateCopyFromCode(out, viewFactoryCreator.getContext(), "dataObject", "w", dataObjectType, widgetClassType, bindPath, converterVariable, converterType, false);
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
	
	public static void validateConverter(JClassType converterType, RebindContext context, JClassType widgetClass, JClassType propertyType)
	{
		JClassType hasValueType = context.getGeneratorContext().getTypeOracle().findType(HasValue.class.getCanonicalName());
		JClassType hasTextType = context.getGeneratorContext().getTypeOracle().findType(HasText.class.getCanonicalName());
		JClassType typeConverterType = context.getGeneratorContext().getTypeOracle().findType(TypeConverter.class.getCanonicalName());
		JClassType stringType = context.getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName());

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
}
