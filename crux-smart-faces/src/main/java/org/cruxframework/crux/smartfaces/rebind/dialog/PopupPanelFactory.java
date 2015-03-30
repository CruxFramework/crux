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
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.gwt.rebind.PanelFactory;
import org.cruxframework.crux.smartfaces.client.dialog.PopupPanel;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * A factory for PopupPanel widgets
 * @author Thiago Bustamante
 *
 */
@DeclarativeFactory(id="popupPanel", library=Constants.LIBRARY_NAME, targetWidget=PopupPanel.class, attachToDOM=false,
					description="A Popup panel that can display one widget inside a dialog window.")
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="modal", type=Boolean.class, description="If true, this dialog will not accept interactions with elements above it.")
})
@TagChildren({
	@TagChild(PopupPanelFactory.WidgetContentProcessor.class)
})
public class PopupPanelFactory extends PanelFactory<WidgetCreatorContext>
       implements DialogFactory<WidgetCreatorContext>, HasCloseHandlersFactory<WidgetCreatorContext>
{
    @TagConstraints(minOccurs="0", maxOccurs="1")
    public static class WidgetContentProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}		
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		boolean autoHide = context.readBooleanWidgetProperty("autoHide", false);
		boolean modal = context.readBooleanWidgetProperty("modal", false);
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+autoHide+","+modal+");");
	}

    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
