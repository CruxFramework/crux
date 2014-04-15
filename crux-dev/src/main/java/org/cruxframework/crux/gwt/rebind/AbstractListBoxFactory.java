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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasChangeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasNameFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

/**
 * Base class for implementing factories for many kinds of list boxes.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
 */
@TagAttributes({
	@TagAttribute(value="visibleItemCount", type=Integer.class)
})
public abstract class AbstractListBoxFactory extends FocusWidgetFactory<ListBoxContext>
				implements HasChangeHandlersFactory<ListBoxContext>, HasNameFactory<ListBoxContext>
{
	@TagConstraints(tagName="item", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("value"),
		@TagAttributeDeclaration(value="label", supportsI18N=true),
		@TagAttributeDeclaration(value="selected", type=Boolean.class)
	})
	public abstract static class ItemsProcessor extends WidgetChildProcessor<ListBoxContext>
	{
		@Override
		public void processChildren(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException
		{

			String label = context.readChildProperty("label");
			String value = context.readChildProperty("value");

			if(label != null && label.length() > 0)
			{
				label = getWidgetCreator().getDeclaredMessage(label);
			}

			value = EscapeUtils.quote(value);

			out.println(context.getWidget()+".insertItem("+label+", "+value+", "+context.index+");");

			String selected = context.readChildProperty("selected");
			if (selected != null && selected.length() > 0)
			{
				out.println(context.getWidget()+".setItemSelected("+context.index+", "+Boolean.parseBoolean(selected)+");");
			}
			context.index += 1;
		}
	}

	@Override
	public ListBoxContext instantiateContext()
	{
	    return new ListBoxContext();
	}
}