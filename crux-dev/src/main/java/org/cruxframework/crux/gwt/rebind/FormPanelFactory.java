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
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * Represents a FormPanelFactory.
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="formPanel", library="gwt", targetWidget=FormPanel.class)
@TagAttributes({
	@TagAttribute("method"),
	@TagAttribute("encoding"),
	@TagAttribute("action")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration("target")
})
@TagEvents({
	@TagEvent(SubmitCompleteEvtBind.class),
	@TagEvent(SubmitEvtBind.class)
})
@TagChildren({
	@TagChild(FormPanelFactory.WidgetContentProcessor.class)
})
public class FormPanelFactory extends PanelFactory<WidgetCreatorContext>
{
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context)
	{
		String className = FormPanel.class.getCanonicalName();
		String target = context.readWidgetProperty("target");
		if (target != null && target.length() >0)
		{
			out.println(className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(target)+");");
		}
		else
		{
			out.println(className + " " + context.getWidget()+" = new "+className+"();");
		}
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="1")
	public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
