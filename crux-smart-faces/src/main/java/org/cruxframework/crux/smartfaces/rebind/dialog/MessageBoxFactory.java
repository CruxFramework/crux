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

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox.MessageType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.event.OkEvtBind;

/**
 * A factory for DialogBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="messageBox", library=Constants.LIBRARY_NAME, attachToDOM=false, targetWidget= MessageBox.class,
					description="A message box that can display messages inside a dialog window.")
@TagAttributes({
	@TagAttribute(value="dialogTitle", supportsI18N=true, description="Sets the Dialog title."),
	@TagAttribute(value="buttonText", supportsI18N=true, description="Sets the Dialog button text."),
	@TagAttribute(value="message", supportsI18N=true, description="Message to be presented on this message box.", processor=MessageBoxFactory.MessageProcessor.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="movable", type=Boolean.class, defaultValue="true", description="If true, the window can be dragged on the screen"),
	@TagAttributeDeclaration(value="resizable", type=Boolean.class, defaultValue="false", description="If true, the window can be resized"),
	@TagAttributeDeclaration(value="closable", type=Boolean.class, defaultValue="true", description="If true, a close button will be available at the dialog's top bar to close the window"),
	@TagAttributeDeclaration(value="modal", type=Boolean.class, defaultValue="true", description="If true, the content behind the dialog can not be changed when dialog is showing"),
	@TagAttributeDeclaration(value="messageType", type=MessageType.class, defaultValue="INFO", description="The type of the message presented by this box. It changes the message box style, to customize the dialog when errors or warnings is presented.")
})
@TagEvents({
	@TagEvent(value=OkEvtBind.class, description="Event triggered when the message box button is selected.")
})
public class MessageBoxFactory extends PanelFactory<WidgetCreatorContext>
       implements DialogFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext> 
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		boolean movable = context.readBooleanWidgetProperty("movable", true);
		boolean closable = context.readBooleanWidgetProperty("closable", true);
		boolean resizable = context.readBooleanWidgetProperty("resizable", false);
		boolean modal = context.readBooleanWidgetProperty("modal", true);
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+movable+", "+resizable+", "+closable+", "+modal+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
        
	/**
	 * Process message attribute
	 * @author Thiago da Rosa de Bustamante
	 *
	 */	
	public static class MessageProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public MessageProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			String messageType = context.readWidgetProperty("messageType", MessageType.INFO.name());
			String expression = getWidgetCreator().getResourceAccessExpression(attributeValue);
			out.println(context.getWidget()+".setMessage("+expression+", "+MessageType.class.getCanonicalName()+"."+messageType+");");
        }
	}
    
}
