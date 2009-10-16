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
package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasDirectionFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasNameFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;
import br.com.sysmap.crux.core.client.screen.factory.HasWordWrapFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * Represents an AnchorFactory component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="anchor", library="bas")
public class AnchorFactory extends FocusWidgetFactory<Anchor> 
	   implements HasTextFactory<Anchor>, HasNameFactory<Anchor>, 
	              HasWordWrapFactory<Anchor>, HasDirectionFactory<Anchor>
{
	@Override
	@TagAttributes({
		@TagAttribute("href"),
		@TagAttribute("target")
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("horizontalAlignment"),
		@TagAttributeDeclaration("verticalAlignment")
	})
	public void processAttributes(WidgetFactoryContext<Anchor> context) throws InterfaceConfigException
	{
		super.processAttributes(context);

		Element element = context.getElement();
		Anchor widget = context.getWidget();
		
		String horizontalAlignment = element.getAttribute("_horizontalAlignment");
		if (horizontalAlignment != null && horizontalAlignment.trim().length() > 0)
		{
			if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			}
			else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_DEFAULT);
			}
			else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
			}
			else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(horizontalAlignment))
			{
				widget.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			}
		}
		
		String innerHtml = element.getInnerHTML();
		String text = element.getAttribute("_text");
		if ((text == null || text.length() ==0) && innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
		}		
	}

	@Override
	public Anchor instantiateWidget(Element element, String widgetId) 
	{
		return new Anchor();
	}	
}
