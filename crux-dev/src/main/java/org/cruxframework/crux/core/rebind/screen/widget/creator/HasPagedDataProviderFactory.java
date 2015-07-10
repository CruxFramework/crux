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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.client.dataprovider.HasPagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.PageEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * A helper class to help on {@link HasPagedDataProvider} widgets creation, based on crux pages metadata.
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="dataProvider", processor=HasPagedDataProviderFactory.DataProviderProcessor.class, required=true)
})
@TagEvents({
	@TagEvent(PageEvtBind.class)
})
public abstract class HasPagedDataProviderFactory<C extends WidgetCreatorContext> extends WidgetCreator<C>
{
	public static class DataProviderProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public DataProviderProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			JClassType dataObject = getWidgetCreator().getDataObjectFromProvider(attributeValue);
			String dataProviderClassName = PagedDataProvider.class.getCanonicalName()+"<"+dataObject.getParameterizedQualifiedSourceName()+">";
			
			out.println(context.getWidget()+".setDataProvider(("+dataProviderClassName + ")"
						+ getViewVariable()+".getDataProvider("+EscapeUtils.quote(attributeValue)+"), false);");
        }
	}
	
	protected JClassType getDataObject(WidgetCreatorContext context)
	{
		String dataProviderId = context.getWidgetElement().optString("dataProvider");
		if (dataProviderId == null)
		{
			return null;
		}
		JClassType dataObject = getDataObjectFromProvider(dataProviderId);
		return dataObject;
	}
	
}

