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
package org.cruxframework.crux.widgets.rebind.formdisplay;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.AlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.CompositeFactory;
import org.cruxframework.crux.widgets.client.formdisplay.FormDisplay;
import org.cruxframework.crux.widgets.rebind.formdisplay.FormDisplayFactory.FormDisplayContext;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;


/**
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="formDisplay", library="widgets", targetWidget=FormDisplay.class, 
		description="A data form that distribute fields according to the device size.")
@TagChildren({
	@TagChild(FormDisplayFactory.EntryProcessor.class)
})		
public class FormDisplayFactory extends CompositeFactory<FormDisplayContext> 
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="entry")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label"),
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="left")
	})
	@TagChildren({
		@TagChild(EntryWidgetProcessor.class),
	})		
	public static class EntryProcessor extends WidgetChildProcessor<FormDisplayContext>
	{ 
		@Override
		public void processChildren(SourcePrinter out, FormDisplayContext context) throws CruxGeneratorException 
		{
			String label = context.readChildProperty("label");
			label = (label != null && label.length() > 0) ? getWidgetCreator().getDeclaredMessage(label) : null;
			context.setLabel(label);
			
			String align = context.readChildProperty("horizontalAlignment");
			align = AlignmentAttributeParser.getHorizontalAlignment(align, HasHorizontalAlignment.class.getCanonicalName()+".ALIGN_LEFT");
			context.setAlign(align);
		}
	}
	
	@TagConstraints(minOccurs="1", maxOccurs="1", type=AnyWidget.class)
	public static class EntryWidgetProcessor extends WidgetChildProcessor<FormDisplayContext>
	{
		@Override
		public void processChildren(SourcePrinter out, FormDisplayContext context) throws CruxGeneratorException 
		{
			String child = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			out.println(context.getWidget() + ".addEntry(" + context.getLabel() + ", " + child + ", " +  context.getAlign() + ");");
		}
	}

	@Override
	public FormDisplayContext instantiateContext()
	{
		return new FormDisplayContext();
	}
	
	/**
	 * @author gessedafe
	 */
	public class FormDisplayContext extends WidgetCreatorContext
	{
		String label;
		String align;

		public String getLabel()
		{
			return label;
		}

		public void setLabel(String label)
		{
			this.label = label;
		}

		public String getAlign()
		{
			return align;
		}

		public void setAlign(String align)
		{
			this.align = align;
		}
	}
}