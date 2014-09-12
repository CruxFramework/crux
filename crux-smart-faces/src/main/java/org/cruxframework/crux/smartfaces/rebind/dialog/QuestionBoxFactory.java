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
package org.cruxframework.crux.smartfaces.rebind.dialog;

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.dialog.QuestionBox;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * A factory for QuestionBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="questionBox", library=Constants.LIBRARY_NAME, attachToDOM=false, targetWidget= QuestionBox.class,
					description="A question box that can display messages inside a dialog window with custom question buttons.")
@TagAttributes({
	@TagAttribute(value="dialogTitle", supportsI18N=true, description="Sets the Dialog title."),
	@TagAttribute(value="message", supportsI18N=true, description="Message to be presented on this confirm.")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="movable", type=Boolean.class, defaultValue="true", description="If true, the window can be dragged on the screen"),
	@TagAttributeDeclaration(value="resizable", type=Boolean.class, defaultValue="false", description="If true, the window can be resized"),
	@TagAttributeDeclaration(value="closable", type=Boolean.class, defaultValue="false", description="If true, the window can be closed clicking on a close button on topbar."),
})
@TagChildren({
	@TagChild(QuestionBoxFactory.QuestionProcessor.class)
})
public class QuestionBoxFactory extends PanelFactory<WidgetCreatorContext>
       implements DialogFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>
{
	@TagConstraints(tagName="answer", minOccurs="0", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", supportsI18N=true, required=true),
		@TagAttributeDeclaration(value="styleName", supportsResources=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onSelect", description="Event to be called when answer is selected")
	})
	public static class QuestionProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{

			String label = context.readChildProperty("label");
			String styleName = context.readChildProperty("styleName");
			String onSelect = context.readChildProperty("onSelect");

			if(label != null && label.length() > 0)
			{
				label = getWidgetCreator().getDeclaredMessage(label);
			}
			if(styleName != null && styleName.length() > 0)
			{
				styleName = getWidgetCreator().getResourceAccessExpression(styleName);
			}
			else
			{
				styleName = null;
			}

			out.print(context.getWidget()+".addAnswer("+label+", ");
			if (StringUtils.isEmpty(onSelect))
			{
				out.print("null");
			}
			else
			{
				out.println("new "+SelectHandler.class.getCanonicalName()+"(){");
				out.println("public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");
				EvtProcessor.printEvtCall(out, onSelect, "onSelect", SelectEvent.class.getCanonicalName(), "event", getWidgetCreator());
				out.println("}");
				out.println("}");
			}
			out.println(", "+styleName+");");
		}
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		boolean movable = context.readBooleanWidgetProperty("movable", true);
		boolean resizable = context.readBooleanWidgetProperty("resizable", false);
		boolean closable = context.readBooleanWidgetProperty("closable", false);
		String styleName = context.readWidgetProperty("styleName", QuestionBox.DEFAULT_STYLE_NAMES);
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+movable+", "+resizable+", "+closable+", "+EscapeUtils.quote(styleName)+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
}
