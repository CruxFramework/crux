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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;

import com.google.gwt.user.client.ui.ListBox;

/**
 * Represents a List Box component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="listBox", library="gwt", targetWidget=ListBox.class)
@TagChildren({
	@TagChild(ListBoxFactory.ListBoxItemsProcessor.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="multiple", type=Boolean.class)
})
public class ListBoxFactory extends AbstractListBoxFactory
{
	@Override
	public void processChildren(SourcePrinter out, ListBoxContext context) throws CruxGeneratorException {}	
	
	public static class ListBoxItemsProcessor extends ItemsProcessor {}
	
	@Override
	public void instantiateWidget(SourcePrinter out, ListBoxContext context)
	{
		String className = getWidgetClassName();
		String multiple = context.readWidgetProperty("multiple");
		if (multiple != null && multiple.trim().length() > 0)
		{
			out.println("final " + className + " " + context.getWidget()+" = new "+className+"("+Boolean.parseBoolean(multiple)+");");

		}
		else
		{
			out.println("final " + className + " " + context.getWidget()+" = new "+className+"();");
		}
	}
}