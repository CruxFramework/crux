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

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.VerticalSplitPanel;

/**
 * A Factory for VerticalSplitPanel widgets
 * @author Thiago Bustamante
 */
@SuppressWarnings("deprecation")
@DeclarativeFactory(id="verticalSplitPanel", library="gwt", targetWidget=VerticalSplitPanel.class)
@TagChildren({
	@TagChild(VerticalSplitPanelFactory.TopProcessor.class),
	@TagChild(VerticalSplitPanelFactory.BottomProcessor.class)
})
public class VerticalSplitPanelFactory extends PanelFactory<WidgetCreatorContext>
{
	@TagConstraints(tagName="top", minOccurs="0")
	@TagChildren({
		@TagChild(TopWidgeProcessor.class)
	})
	public static class TopProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="bottom", minOccurs="0")
	@TagChildren({
		@TagChild(BottomWidgeProcessor.class)
	})
	public static class BottomProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="topWidget")
	public static class TopWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(widgetProperty="bottomWidget")
	public static class BottomWidgeProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
