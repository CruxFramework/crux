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

import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.DecoratedTabPanel;

/**
 * Factory for DecoratedTabPanel widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="decoratedTabPanel", library="gwt", targetWidget=DecoratedTabPanel.class)
@TagChildren({
	@TagChild(DecoratedTabPanelFactory.TabProcessor.class)
})	
public class DecoratedTabPanelFactory extends AbstractTabPanelFactory
{
	@TagChildren({
		@TagChild(TabTitleProcessor.class), 
		@TagChild(TabWidgetProcessor.class)
	})	
	public static class TabProcessor extends AbstractTabProcessor {}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(TextTabProcessor.class),
		@TagChild(HTMLTabProcessor.class),
		@TagChild(WidgetTitleTabProcessor.class)
	})		
	public static class TabTitleProcessor extends ChoiceChildProcessor<TabPanelContext> {}
	
	public static class TextTabProcessor extends AbstractTextTabProcessor {}
	
	public static class HTMLTabProcessor extends AbstractHTMLTabProcessor {}
	
	@TagConstraints(tagName="tabWidget")
	@TagChildren({
		@TagChild(WidgetTitleProcessor.class)
	})	
	public static class WidgetTitleTabProcessor extends WidgetChildProcessor<TabPanelContext> {}

	public static class WidgetTitleProcessor extends AbstractWidgetTitleProcessor {}
	
	@TagConstraints(tagName="panelContent")
	@TagChildren({
		@TagChild(WidgetContentProcessor.class)
	})	
	public static class TabWidgetProcessor extends WidgetChildProcessor<TabPanelContext> {}

	public static class WidgetContentProcessor extends AbstractWidgetContentProcessor {}	
}
