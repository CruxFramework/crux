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
package br.com.sysmap.crux.gwt.client;


import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;
import br.com.sysmap.crux.gwt.client.align.AlignmentAttributeParser;
import br.com.sysmap.crux.gwt.client.align.HorizontalAlignment;
import br.com.sysmap.crux.gwt.client.align.VerticalAlignment;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class HTMLTableFactory <T extends HTMLTable> extends PanelFactory<T>
       implements HasClickHandlersFactory<T>
{	
	@Override
	@TagAttributes({
		@TagAttribute(value="borderWidth",type=Integer.class),
		@TagAttribute(value="cellPadding",type=Integer.class),
		@TagAttribute(value="cellSpacing",type=Integer.class)
	})
	public void processAttributes(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@TagChildAttributes(tagName="row", minOccurs="0", maxOccurs="unbounded")
	public static class TableRowProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Element element = context.getChildElement();
			Integer index = (Integer) context.getAttribute("rowIndex");
			if (index == null)
			{
				index = 0;
			}
			else
			{
				index++;
			}
			try
			{
				String styleName = element.getAttribute("_styleName");
				if (styleName != null && styleName.length() > 0)
				{
					context.getRootWidget().getRowFormatter().setStyleName(index, styleName);
				}
				String visible = element.getAttribute("_visible");
				if (visible != null && visible.length() > 0)
				{
					context.getRootWidget().getRowFormatter().setVisible(index, Boolean.parseBoolean(visible));
				}

				String verticalAlignment = element.getAttribute("_verticalAlignment");
				context.getRootWidget().getRowFormatter().setVerticalAlign(index, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));
			}
			finally
			{
				context.setAttribute("rowIndex", (index));
				context.setAttribute("colIndex", null);
			}
		}
	}

	@TagChildAttributes(minOccurs="0", maxOccurs="unbounded")
	public static class TableCellProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="visible", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="wordWrap", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=HorizontalAlignment.class, defaultValue="defaultAlign"),
			@TagAttributeDeclaration(value="verticalAlignment", type=VerticalAlignment.class)
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Element element = context.getChildElement();
			HTMLTable widget = context.getRootWidget();

			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			if (indexCol == null)
			{
				indexCol = 0;
			}
			else
			{
				indexCol++;
			}
			try
			{
				String styleName = element.getAttribute("_styleName");
				if (styleName != null && styleName.length() > 0)
				{
					widget.getCellFormatter().setStyleName(indexRow, indexCol, styleName);
				}
				String visible = element.getAttribute("_visible");
				if (visible != null && visible.length() > 0)
				{
					widget.getCellFormatter().setVisible(indexRow, indexCol, Boolean.parseBoolean(visible));
				}
				String height = element.getAttribute("_height");
				if (height != null && height.length() > 0)
				{
					widget.getCellFormatter().setHeight(indexRow, indexCol, height);
				}
				String width = element.getAttribute("_width");
				if (width != null && width.length() > 0)
				{
					widget.getCellFormatter().setWidth(indexRow, indexCol, width);
				}
				String wordWrap = element.getAttribute("_wordWrap");
				if (wordWrap != null && wordWrap.length() > 0)
				{
					widget.getCellFormatter().setWordWrap(indexRow, indexCol, Boolean.parseBoolean(wordWrap));
				}

				String horizontalAlignment = element.getAttribute("_horizontalAlignment");
				if (horizontalAlignment != null && horizontalAlignment.length() > 0)
				{
					widget.getCellFormatter().setHorizontalAlignment(indexRow, indexCol, 
						AlignmentAttributeParser.getHorizontalAlignment(horizontalAlignment, HasHorizontalAlignment.ALIGN_DEFAULT));
				}
				String verticalAlignment = element.getAttribute("_verticalAlignment");
				if (verticalAlignment != null && verticalAlignment.length() > 0)
				{
					widget.getCellFormatter().setVerticalAlignment(indexRow, indexCol, 
						AlignmentAttributeParser.getVerticalAlignment(verticalAlignment));

				}
			}
			finally
			{
				context.setAttribute("colIndex", indexCol);
			}
		}
	}
	
	@TagChildAttributes(tagName="text", type=String.class)
	public static abstract class CellTextProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			context.getRootWidget().setText(indexRow, indexCol, ScreenFactory.getInstance().getDeclaredMessage(context.getChildElement().getInnerHTML()));
		}
	}
	
	@TagChildAttributes(tagName="html", type=AnyTag.class)
	public static abstract class CellHTMLProcessor<T extends HTMLTable> extends WidgetChildProcessor<T>
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException 
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			context.getRootWidget().setHTML(indexRow, indexCol, context.getChildElement().getInnerHTML());
		}
	}
	
	@TagChildAttributes(tagName="widget")
	public static abstract class CellWidgetProcessor<T extends HTMLTable> extends WidgetChildProcessor<T> {}

	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor<T extends HTMLTable> extends WidgetChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");
			Element childElement = context.getChildElement();
			context.getRootWidget().setWidget(indexRow, indexCol, createChildWidget(childElement, childElement.getId()));
		}
	}
}
