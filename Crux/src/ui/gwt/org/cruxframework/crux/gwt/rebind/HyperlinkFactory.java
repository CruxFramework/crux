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

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Represents a HyperlinkFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="hyperlink", library="gwt", targetWidget=Hyperlink.class)
@TagAttributes({
	@TagAttribute("targetHistoryToken")
})
@TagChildren({
	@TagChild(value=HyperlinkFactory.ContentProcessor.class, autoProcess=false)
})
public class HyperlinkFactory extends WidgetCreator<WidgetCreatorContext>
	   implements HasHTMLFactory<WidgetCreatorContext>, HasClickHandlersFactory<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
