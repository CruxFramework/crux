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
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.dialog.DialogBox;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * A factory for DialogBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="dialogBox", library=Constants.LIBRARY_NAME, attachToDOM=false, targetWidget= DialogBox.class,
					description="A Dialog box that can display one widget inside a dialog window.")
@TagChildren({
	@TagChild(DialogBoxFactory.WidgetContentProcessor.class)
})
@TagAttributes({
	@TagAttribute(value="dialogTitle", supportsI18N=true, description="Sets the Dialog title.")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="movable", type=Boolean.class, defaultValue="true", description="If true, the window can be dragged on the screen"),
	@TagAttributeDeclaration(value="resizable", type=Boolean.class, defaultValue="false", description="If true, the window can be resized"),
	@TagAttributeDeclaration(value="closable", type=Boolean.class, defaultValue="true", description="If true, a close button will be available at the dialog's top bar to close the window"),
	@TagAttributeDeclaration(value="modal", type=Boolean.class, defaultValue="true", description="If true, the content behind the dialog can not be changed when dialog is showing")
})
public class DialogBoxFactory extends PanelFactory<WidgetCreatorContext>
       implements HasAnimationFactory<WidgetCreatorContext>, 
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
    
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
}
