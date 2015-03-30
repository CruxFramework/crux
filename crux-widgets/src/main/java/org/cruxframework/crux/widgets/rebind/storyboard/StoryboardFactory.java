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
package org.cruxframework.crux.widgets.rebind.storyboard;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.ComplexPanelFactory;
import org.cruxframework.crux.widgets.client.storyboard.Storyboard;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

class StoryboardContext extends WidgetCreatorContext
{
	public String horizontalAlignment;
	public String verticalAlignment;
	public String largeDeviceItemHeight;
	public String smallDeviceItemHeight;
	public String largeDeviceItemWidth;
}


@DeclarativeFactory(library="widgets", id="storyboard", targetWidget=Storyboard.class, 
	description="An element distribution panel that adapts to the type of device.")
@TagChildren({
	@TagChild(StoryboardFactory.StoryboardContentProcessor.class)
})
@TagAttributes({
	@TagAttribute(value="largeDeviceItemWidth", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="smallDeviceItemHeight", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch}),
	@TagAttribute(value="largeDeviceItemHeight", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="horizontalAlignment", type=HorizontalAlignment.class, processor=HorizontalAlignmentAttributeParser.class, defaultValue="center",  
			supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="verticalAlignment", type=VerticalAlignment.class, processor=VerticalAlignmentAttributeParser.class, defaultValue="middle"), 
	@TagAttribute(value="fixedWidth", type=Boolean.class, defaultValue="true"), 
	@TagAttribute(value="fixedHeight", type=Boolean.class, defaultValue="true") 
})
public class StoryboardFactory  extends ComplexPanelFactory<StoryboardContext> implements HasSelectionHandlersFactory<StoryboardContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(ItemProcessor.class),
		@TagChild(WidgetProcessor.class)
	})		
	public static class StoryboardContentProcessor extends ChoiceChildProcessor<StoryboardContext> 
	{
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	public static class WidgetProcessor extends AnyWidgetChildProcessor<StoryboardContext> 
	{
	}

	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	@TagChildren({
		@TagChild(value=WidgetContentProcessor.class)
	})		
	public static class ItemProcessor extends WidgetChildProcessor<StoryboardContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, StoryboardContext context) throws CruxGeneratorException 
		{
			context.largeDeviceItemHeight = context.readChildProperty("height");
			context.smallDeviceItemHeight = context.readChildProperty("width");
			context.horizontalAlignment = context.readChildProperty("horizontalAlignment");
			context.verticalAlignment = context.readChildProperty("verticalAlignment");
		}
	}
	
	
	@TagConstraints(type=AnyWidget.class)
    public static class WidgetContentProcessor extends WidgetChildProcessor<StoryboardContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, StoryboardContext context) throws CruxGeneratorException
		{
			boolean childPartialSupport = getWidgetCreator().hasChildPartialSupport(context.getChildElement());
			if (childPartialSupport)
			{
				out.println("if ("+getWidgetCreator().getChildWidgetClassName(context.getChildElement())+".isSupported()){");
			}
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			String parent = context.getWidget();
			out.println(parent+".add("+child+");");
			if (!StringUtils.isEmpty(context.largeDeviceItemHeight))
			{
				out.println(parent+".setLargeDeviceItemHeight("+child+", "+EscapeUtils.quote(context.largeDeviceItemHeight)+");");
			}
			if (!StringUtils.isEmpty(context.smallDeviceItemHeight))
			{
				out.println(parent+".setSmallDeviceItemHeight("+child+", "+EscapeUtils.quote(context.smallDeviceItemHeight)+");");
			}
			if (!StringUtils.isEmpty(context.largeDeviceItemWidth))
			{
				out.println(parent+".setLargeDeviceItemWidth("+child+", "+EscapeUtils.quote(context.largeDeviceItemWidth)+");");
			}
			if (!StringUtils.isEmpty(context.horizontalAlignment))
			{
				out.println(parent+".setHorizontalAlignment("+child+", "+
					  AlignmentAttributeParser.getHorizontalAlignment(context.horizontalAlignment, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_DEFAULT")+");");
			}
			if (!StringUtils.isEmpty(context.verticalAlignment))
			{
				out.println(parent+".setVerticalAlignment("+child+", "+AlignmentAttributeParser.getVerticalAlignment(context.verticalAlignment)+");");
			}
			if (childPartialSupport)
			{
				out.println("}");
			}
			context.largeDeviceItemHeight = null;
			context.smallDeviceItemHeight = null;
			context.horizontalAlignment = null;
			context.verticalAlignment = null;
		}
	}
	
    @Override
    public StoryboardContext instantiateContext()
    {
	    return new StoryboardContext();
    }
	
	@Override
	public void instantiateWidget(SourcePrinter out, StoryboardContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
