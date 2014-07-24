/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllKeyHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasValueChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.gwt.client.LoadOracleEvent;

import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * Factory for SuggestBox widget
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="suggestBox", library="gwt", targetWidget=SuggestBox.class)
@TagAttributes({
	@TagAttribute(value="accessKey", type=Character.class),
	@TagAttribute(value="autoSelectEnabled", type=Boolean.class),
	@TagAttribute(value="focus", type=Boolean.class),
	@TagAttribute(value="limit", type=Integer.class),
	@TagAttribute(value="popupStyleName", supportsResources=true),
	@TagAttribute(value="tabIndex", type=Integer.class),
	@TagAttribute("value")
})
@TagEventsDeclaration({
	@TagEventDeclaration("onLoadOracle")
})
public class SuggestBoxFactory extends CompositeFactory<WidgetCreatorContext> 
       implements HasAnimationFactory<WidgetCreatorContext>, HasTextFactory<WidgetCreatorContext>, 
                  HasValueChangeHandlersFactory<WidgetCreatorContext>, 
                  HasSelectionHandlersFactory<WidgetCreatorContext>,
                  HasAllKeyHandlersFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String className = SuggestBox.class.getCanonicalName();

		String oracle = ViewFactoryCreator.createVariableName("oracle");

		String eventLoadOracle = context.readWidgetProperty("onLoadOracle");
		if (!StringUtils.isEmpty(eventLoadOracle))
		{
			out.println(SuggestOracle.class.getCanonicalName()+" "+oracle+" = ("+SuggestOracle.class.getCanonicalName()+")");
			EvtProcessor.printEvtCall(out, eventLoadOracle, "onLoadOracle", LoadOracleEvent.class.getCanonicalName()+"<"+className+">", 
					" new "+LoadOracleEvent.class.getCanonicalName()+"<"+className+">("+EscapeUtils.quote(context.getWidgetId())+")", this);
			out.println(className + " " + context.getWidget()+" = new "+className+"("+oracle+");");
		}
		else
		{
			throw new CruxGeneratorException("The attribute onLoadOracle is required for widget id: ["+context.getWidgetId()+"].");
		}
	}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
