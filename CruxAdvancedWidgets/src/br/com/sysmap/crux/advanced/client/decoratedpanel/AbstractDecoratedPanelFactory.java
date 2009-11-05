/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.advanced.client.decoratedpanel;

import br.com.sysmap.crux.basic.client.CellPanelFactory;
import br.com.sysmap.crux.basic.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.basic.client.align.HorizontalAlignment;
import br.com.sysmap.crux.basic.client.align.VerticalAlignment;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Base factory for Decorated Panels
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public abstract class AbstractDecoratedPanelFactory<T extends DecoratedPanel> extends CellPanelFactory<T>
{
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
		@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
		processAlignment(context);
	}

	/**
	 * @param widget
	 * @param element
	 */
	private void processAlignment(WidgetFactoryContext<T> context)
	{
		Element element = context.getElement();
		DecoratedPanel widget = context.getWidget();

		String cellHorizontalAlignment = element.getAttribute("_horizontalAlignment");
		if (cellHorizontalAlignment != null && cellHorizontalAlignment.length() > 0)
		{
			widget.setHorizontalAlignment(AlignmentAttributeParser.getHorizontalAlignment(cellHorizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
		}		
		String cellVerticalAlignment = element.getAttribute("_verticalAlignment");
		if (cellVerticalAlignment != null && cellVerticalAlignment.length() > 0)
		{
			widget.setVerticalAlignment(AlignmentAttributeParser.getVerticalAlignment(cellVerticalAlignment));
		}
	}	
}