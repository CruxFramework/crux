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

import com.google.gwt.user.client.ui.DecoratedStackPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="decoratedStackPanel", library="gwt", targetWidget= DecoratedStackPanel.class)
@TagChildren({
	@TagChild(DecoratedStackPanelFactory.StackItemProcessor.class)
})	
public class DecoratedStackPanelFactory extends AbstractStackPanelFactory
{
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="stackItem")
	@TagChildren({
		@TagChild(TitleProcessor.class),
		@TagChild(ContentProcessor.class)
	})	
	public static class StackItemProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext> {}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(TitleTextProcessor.class),
		@TagChild(TitleHTMLProcessor.class)
	})	
	public static class TitleProcessor extends ChoiceChildProcessor<AbstractStackPanelFactoryContext> {}

	public static class TitleTextProcessor extends AbstractTitleTextProcessor {}
	
	public static class TitleHTMLProcessor extends AbstractTitleHTMLProcessor {}
	
	@TagConstraints(minOccurs="0", tagName="widget")
	@TagChildren({
		@TagChild(ContentWidgetProcessor.class)
	})	
	public static class ContentProcessor extends WidgetChildProcessor<AbstractStackPanelFactoryContext> {}
	
	public static class ContentWidgetProcessor extends AbstractContentWidgetProcessor {}	
}
