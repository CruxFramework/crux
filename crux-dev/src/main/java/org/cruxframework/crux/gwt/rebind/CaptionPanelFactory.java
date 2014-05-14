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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.CaptionPanel;


/**
 * Factory for CaptionPanel widgets
 * @author Gesse S. F. Dafe
 */
@DeclarativeFactory(id="captionPanel", library="gwt", targetWidget=CaptionPanel.class)
@TagAttributes({
	@TagAttribute("captionText")
})
@TagChildren({
	@TagChild(CaptionPanelFactory.CaptionProcessor.class),
	@TagChild(CaptionPanelFactory.ContentProcessor.class)
})	
public class CaptionPanelFactory extends CompositeFactory<WidgetCreatorContext>
{
	@Override
	public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException {}
	
	@TagConstraints(minOccurs="0")
	@TagChildren({
		@TagChild(CaptionTextProcessor.class),
		@TagChild(CaptionHTMLProcessor.class)
	})	
	public static class CaptionProcessor extends ChoiceChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(minOccurs="0", tagName="widget")
	@TagChildren({
		@TagChild(WidgetProcessor.class)
	})	
	public static class ContentProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(minOccurs="0", widgetProperty="contentWidget")
	public static class WidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="captionText", type=String.class)
	public static class CaptionTextProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().getDeclaredMessage(getWidgetCreator().
					ensureTextChild(context.getChildElement(), false, context.getWidgetId(), false));
			out.println(context.getWidget()+".setCaptionText("+title+");");
		}
	}
	
	@TagConstraints(tagName="captionHTML", type=HTMLTag.class)
	public static class CaptionHTMLProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException 
		{
			String title = getWidgetCreator().ensureHtmlChild(context.getChildElement(), false, context.getWidgetId());
			out.println(context.getWidget()+".setCaptionHTML("+title+");");
		}
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
