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
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.user.client.ui.SimpleRadioButton;

/**
 * Represents a SimpleRadioButtonFactory component.
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="simpleRadioButton", library="gwt", targetWidget=SimpleRadioButton.class)
@TagAttributes({
	@TagAttribute(value="checked", type=Boolean.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="name", required=true)
})
public class SimpleRadioButtonFactory extends FocusWidgetFactory<WidgetCreatorContext> 
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String className = SimpleRadioButton.class.getCanonicalName();
		out.println(className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(context.readWidgetProperty("name"))+");");
	}	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
