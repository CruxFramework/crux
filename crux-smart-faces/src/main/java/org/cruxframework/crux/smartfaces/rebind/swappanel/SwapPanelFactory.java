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
package org.cruxframework.crux.smartfaces.rebind.swappanel;

import org.cruxframework.crux.core.rebind.event.SwapEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapPanel;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * @author bruno.rafael
 *
 */
@DeclarativeFactory(id="swapPanel", library=Constants.LIBRARY_NAME, targetWidget=SwapPanel.class, 
					description="A panel that displays one widget at a time and can be used to handle widgets swapping.")
@TagChildren({
	@TagChild(SwapPanelFactory.WidgetContentProcessor.class)
})
@TagEvents({
	@TagEvent(SwapEvtBind.class)
})
public class SwapPanelFactory extends PanelFactory<WidgetCreatorContext>
		implements HasSwapAnimationFactory<WidgetCreatorContext>, HasAnimationFactory<WidgetCreatorContext>
{
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
	
	@TagConstraints(minOccurs="0", maxOccurs="1", widgetProperty="currentWidget", 
					description="The initial widget that will be presented into this panel.")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
		
}
