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

import org.cruxframework.crux.core.client.screen.RequiresResizeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.HeaderPanel;


/**
 * Factory for HeaderPanel widget
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="headerPanel", library="gwt", targetWidget=HeaderPanel.class)
@TagChildren({
	@TagChild(HeaderPanelFactory.HeaderProcessor.class),
	@TagChild(HeaderPanelFactory.ContentProcessor.class),
	@TagChild(HeaderPanelFactory.FooterProcessor.class)
})
public class HeaderPanelFactory extends ComplexPanelFactory<WidgetCreatorContext> implements RequiresResizeFactory
{
	@TagConstraints(tagName="header")
	@TagChildren({
		@TagChild(HeaderWidgetProcessor.class)
	})
	public static class HeaderProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="headerWidget")
	public static class HeaderWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="content")
	@TagChildren({
		@TagChild(ContentWidgetProcessor.class)
	})
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="contentWidget")
	public static class ContentWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="footer")
	@TagChildren({
		@TagChild(FooterWidgetProcessor.class)
	})
	public static class FooterProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(widgetProperty="footerWidget")
	public static class FooterWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}