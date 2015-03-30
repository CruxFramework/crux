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
package org.cruxframework.crux.widgets.rebind.styledpanel;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHorizontalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasVerticalAlignmentFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.FlowPanelFactory;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.widgets.client.styledpanel.StyledPanel;

/**
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="styledPanel", library="widgets", targetWidget=StyledPanel.class, 
	description="A simple panel wrapped by a div, to allow advanced styling techniques.")
@TagChildren({
	@TagChild(FlowPanelFactory.WidgetContentProcessor.class)
})
public class StyledPanelFactory extends PanelFactory<WidgetCreatorContext> implements HasHorizontalAlignmentFactory<WidgetCreatorContext>, HasVerticalAlignmentFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}