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
package org.cruxframework.crux.widgets.rebind.datebox;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.gwt.client.DateFormatUtil;
import org.cruxframework.crux.gwt.client.LoadFormatEvent;
import org.cruxframework.crux.gwt.rebind.CompositeFactory;
import org.cruxframework.crux.gwt.rebind.DatePickerFactory;
import org.cruxframework.crux.widgets.client.datebox.DateBox;
import org.cruxframework.crux.widgets.client.datebox.gwtoverride.DateBox.DefaultFormat;
import org.cruxframework.crux.widgets.client.datebox.gwtoverride.DateBox.Format;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author samuel.cardoso
 *
 */
@DeclarativeFactory(id="dateBox", library="widgets", targetWidget=DateBox.class)
@TagAttributes({
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute(value="enabled", type=Boolean.class),
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="focus", type=Boolean.class),
	@TagAttribute(value="readOnly", type=Boolean.class, defaultValue="false")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration("value"),
	@TagAttributeDeclaration("pattern"),
	@TagAttributeDeclaration("mask"),
	@TagAttributeDeclaration(value="reportFormatError", type=Boolean.class)
})
@TagEventsDeclaration({
	@TagEventDeclaration("onLoadFormat")
})
@TagChildren({
	@TagChild(value=DateBoxFactory.DateSelectorProcessor.class,autoProcess=false)
})
public class DateBoxFactory extends CompositeFactory<WidgetCreatorContext>
       implements HasValueChangeHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void processAttributes(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);

		String widget = context.getWidget();

		String value = context.readWidgetProperty("value");
		if (value != null && value.length() > 0)
		{
			boolean reportError = true;
			String reportFormatError = context.readWidgetProperty("reportFormatError");
			if (reportFormatError != null && reportFormatError.length() > 0)
			{
				reportError = Boolean.parseBoolean(reportFormatError);
			}

			out.println("try{");
			String date = ViewFactoryCreator.createVariableName("date");
			out.println(Date.class.getCanonicalName()+" "+date+" = "+widget+".getFormat().parse("+widget+", "+EscapeUtils.quote(value)+", "+reportError+");");
			out.println(widget+".setValue("+date+");");
			out.println("}catch(ValidateException e){/* nada a fazer*/}");
		}
	}

	@Override
	public void processEvents(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		super.processEvents(out, context);
	}

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		JSONArray children = ensureChildren(context.getChildElement(), true, context.getWidgetId());
		int size = (children==null?0:children.length());
		String className = getWidgetClassName();

		String format = getFormat(out, context);
		if (size > 0)
		{
			String picker = null;

			for (int i = 0; i < size; i++)
			{
				JSONObject childElement = children.optJSONObject(i);

				if (isWidget(childElement))
				{
					picker = createChildWidget(out, childElement, context);
				}
			}
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+picker+", null, "+format+");");
		}
		else
		{
			out.println("final "+className + " " + context.getWidget()+" = new "+className+"();");
			out.println(context.getWidget()+".setFormat("+format+");");
		}
	}

	/**
	 * @author breno.lages
	 *
	 */
	@TagConstraints(tagName="datePicker", minOccurs="0", type=DatePickerFactory.class)
	public static class DateSelectorProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}

	/**
	 * @param out
	 * @param context
	 * @return
	 */
	public String getFormat(SourcePrinter out, WidgetCreatorContext context)
	{
		String format = ViewFactoryCreator.createVariableName("date");
		String pattern = context.readWidgetProperty("pattern");

		if (pattern != null && pattern.trim().length() > 0)
		{
			out.println(Format.class.getCanonicalName()+" "+format+"= ("+Format.class.getCanonicalName()+") new "+org.cruxframework.crux.widgets.client.datebox.gwtoverride.DateBox.DefaultFormat.class.getCanonicalName()+
					"("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+EscapeUtils.quote(pattern)+"));");
		}
		else
		{

			String eventLoadFormat = context.readWidgetProperty("onLoadFormat");

			if (!StringUtils.isEmpty(eventLoadFormat))
			{
				String loadEvent = createVariableName("evt");
				String event = createVariableName("evt");

				out.println("final Event "+event+" = Events.getEvent("+EscapeUtils.quote("onLoadImage")+", "+ EscapeUtils.quote(eventLoadFormat)+");");
				out.println(LoadFormatEvent.class.getCanonicalName()+"<"+getWidgetClassName()+"> "+loadEvent+
						" = new "+LoadFormatEvent.class.getCanonicalName()+"<"+getWidgetClassName()+">("+EscapeUtils.quote(context.getWidgetId())+");");
				out.println(Format.class.getCanonicalName()+" "+format+
						" = ("+Format.class.getCanonicalName()+") Events.callEvent("+event+", "+loadEvent+");");
			}
			else
			{
				out.println(Format.class.getCanonicalName()+" "+format+"=GWT.create("+DefaultFormat.class.getCanonicalName()+".class);");
			}
		}

		return format;
	}

	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
}
