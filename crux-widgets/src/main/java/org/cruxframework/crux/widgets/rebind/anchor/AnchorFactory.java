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
package org.cruxframework.crux.widgets.rebind.anchor;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.anchor.Anchor;
import org.cruxframework.crux.widgets.rebind.event.SelectEvtBind;

/**
 *Factory for Anchor Widgets
 * @authorThiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="widgets", id="anchor", targetWidget=Anchor.class, description="A simple anchor widget")

@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false", 
			description="If true, the component will call preventDefault on all touch events."),
	@TagAttribute(value="href", supportsResources=true, 
			description="The URL to which it links."), 
	@TagAttribute(value="target", 
			description="The anchor's target frame (the frame in which navigation will occur when the link is selected).") 
})
@TagEvents({
	@TagEvent(SelectEvtBind.class)
})
@TagChildren({
	@TagChild(value=AnchorFactory.ContentProcessor.class, autoProcess=false)
})
public class AnchorFactory extends WidgetCreator<WidgetCreatorContext>
						implements 	HasAllFocusHandlersFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>,
									HasHTMLFactory<WidgetCreatorContext>, FocusableFactory<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
