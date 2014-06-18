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

import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.views.BindableView;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.LazyCompatibleWidgetConsumer;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 *
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewWidgetConsumer extends AbstractDataWidgetConsumer implements LazyCompatibleWidgetConsumer
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
				
				JClassType converterType = getConverterType(out, viewFactoryCreator.getContext(), bindPath, bindConverter, dataObjectType, widgetClassType);
		    	String converterVariable = null;
		    	if (converterType != null)
		    	{
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
}
