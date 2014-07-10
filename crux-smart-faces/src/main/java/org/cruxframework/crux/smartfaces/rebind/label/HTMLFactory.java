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
package org.cruxframework.crux.smartfaces.rebind.label;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAutoHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasTextFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasWordWrapFactory;
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
import org.cruxframework.crux.smartfaces.client.label.HTML;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.event.SelectEvtBind;



/**
 * A Factory for Label widgets
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="html", library=Constants.LIBRARY_NAME, targetWidget=HTML.class, description="A html class that support google fast buttons to simulate clicks on touch devices.")
@TagAttributes({
	@TagAttribute(value="preventDefaultTouchEvents", type=Boolean.class, defaultValue="false", 
				  description="If true, the html will call preventDefault on all touch events.")
})
@TagEvents({
	@TagEvent(SelectEvtBind.class)
})
@TagChildren({
	@TagChild(value=HTMLFactory.ContentProcessor.class, autoProcess=false)
})
public class HTMLFactory extends WidgetCreator<WidgetCreatorContext> implements HasTextFactory<WidgetCreatorContext>, 
													HasWordWrapFactory<WidgetCreatorContext>, 
													HasAutoHorizontalAlignmentFactory<WidgetCreatorContext>,
													HasHTMLFactory<WidgetCreatorContext>
{	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
