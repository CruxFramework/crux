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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.dialog.Confirm;
import org.cruxframework.crux.smartfaces.client.dialog.DialogBox;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox.MessageType;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.event.CancelEvtBind;
import org.cruxframework.crux.smartfaces.rebind.event.OkEvtBind;

/**
 * A factory for Confirm widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="confirm", library=Constants.LIBRARY_NAME, attachToDOM=false, targetWidget= Confirm.class,
					description="A confirm box that can display messages inside a dialog window with Ok and CANCEL buttons.")
@TagAttributes({
	@TagAttribute(value="dialogTitle", supportsI18N=true, description="Sets the Dialog title."),
	@TagAttribute(value="okLabel", supportsI18N=true, description="Sets the Dialog ok button text."),
	@TagAttribute(value="cancelLabel", supportsI18N=true, description="Sets the Dialog cancel button text."),
	@TagAttribute(value="message", supportsI18N=true, description="Message to be presented on this confirm.")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="movable", type=Boolean.class, defaultValue="true", description="If true, the window can be dragged on the screen"),
	@TagAttributeDeclaration(value="resizable", type=Boolean.class, defaultValue="false", description="If true, the window can be resized"),
	@TagAttributeDeclaration(value="messageType", type=MessageType.class, defaultValue="INFO", description="The type of the message presented by this box. It changes the message box style, to customize the dialog when errors or warnings is presented.")
})
@TagEvents({
	@TagEvent(value=OkEvtBind.class, description="Event triggered when the confirm ok button is selected."),
	@TagEvent(value=CancelEvtBind.class, description="Event triggered when the confirm cancel button is selected.")
})
public class ConfirmFactory extends PanelFactory<WidgetCreatorContext>
       implements DialogFactory<WidgetCreatorContext>, 
                  HasCloseHandlersFactory<WidgetCreatorContext>, 
                  HasHTMLFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		boolean movable = context.readBooleanWidgetProperty("movable", true);
		boolean resizable = context.readBooleanWidgetProperty("resizable", false);
		String styleName = context.readWidgetProperty("styleName", DialogBox.DEFAULT_STYLE_NAME);
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+movable+", "+resizable+", "+EscapeUtils.quote(styleName)+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
        return new WidgetCreatorContext();
    }
}
