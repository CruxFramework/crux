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


import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyTag;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
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
			@TagAttributeDeclaration("verticalAlignment")
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
				if (verticalAlignment != null && verticalAlignment.trim().length() > 0)
				{
					if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(verticalAlignment))
					{
						context.getRootWidget().getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_BOTTOM);
					}
					else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(verticalAlignment))
					{
						context.getRootWidget().getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_MIDDLE);
					}
					else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(verticalAlignment))
					{
						context.getRootWidget().getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_TOP);
					}
				}
			}
			finally
			{
				context.setAttribute("rowIndex", (index));
				context.setAttribute("colIndex", 0);
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
			@TagAttributeDeclaration("verticalAlignment"),
			@TagAttributeDeclaration("horizontalAlignment")			
		})
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException
		{
			Element element = context.getChildElement();
			HTMLTable widget = context.getRootWidget();

			Integer indexRow = (Integer) context.getAttribute("rowIndex");
			Integer indexCol = (Integer) context.getAttribute("colIndex");

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
				if (horizontalAlignment != null && horizontalAlignment.trim().length() > 0)
				{
					if (HasHorizontalAlignment.ALIGN_CENTER.getTextAlignString().equals(horizontalAlignment))
					{
						widget.getCellFormatter().setHorizontalAlignment(indexRow, indexCol, HasHorizontalAlignment.ALIGN_CENTER);
					}
					else if (HasHorizontalAlignment.ALIGN_DEFAULT.getTextAlignString().equals(horizontalAlignment))
					{
						widget.getCellFormatter().setHorizontalAlignment(indexRow, indexCol, HasHorizontalAlignment.ALIGN_DEFAULT);
					}
					else if (HasHorizontalAlignment.ALIGN_LEFT.getTextAlignString().equals(horizontalAlignment))
					{
						widget.getCellFormatter().setHorizontalAlignment(indexRow, indexCol, HasHorizontalAlignment.ALIGN_LEFT);
					}
					else if (HasHorizontalAlignment.ALIGN_RIGHT.getTextAlignString().equals(horizontalAlignment))
					{
						widget.getCellFormatter().setHorizontalAlignment(indexRow, indexCol, HasHorizontalAlignment.ALIGN_RIGHT);
					}
				}			

				String verticalAlignment = element.getAttribute("_verticalAlignment");
				if (verticalAlignment != null && verticalAlignment.trim().length() > 0)
				{
					if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(verticalAlignment))
					{
						widget.getCellFormatter().setVerticalAlignment(indexRow, indexCol, HasVerticalAlignment.ALIGN_BOTTOM);
					}
					else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(verticalAlignment))
					{
						widget.getCellFormatter().setVerticalAlignment(indexRow, indexCol, HasVerticalAlignment.ALIGN_MIDDLE);
					}
					else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(verticalAlignment))
					{
						widget.getCellFormatter().setVerticalAlignment(indexRow, indexCol, HasVerticalAlignment.ALIGN_TOP);
					}
				}	
			}
			finally
			{
				context.setAttribute("colIndex", (indexCol+1));
			}
		}
	}
	
	@TagChildAttributes(minOccurs="0")
	public abstract static class CellChildrenProcessor<T extends HTMLTable> extends ChoiceChildProcessor<T> 
	{
		@Override
		public void processChildren(WidgetChildProcessorContext<T> context) throws InterfaceConfigException	
		{
			//prepare cell
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
			context.getRootWidget().setText(indexRow, indexCol, context.getChildElement().getInnerHTML());
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
