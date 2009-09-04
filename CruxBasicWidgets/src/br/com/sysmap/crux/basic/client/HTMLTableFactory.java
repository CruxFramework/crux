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


import java.util.List;

import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.factory.HasClickHandlersFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public abstract class HTMLTableFactory <T extends HTMLTable> extends PanelFactory<T>
       implements HasClickHandlersFactory<T>
{
	protected BasicMessages messages = GWT.create(BasicMessages.class);
	
	/**
	 * @see br.com.sysmap.crux.core.client.screen.HasWidgetsFactory#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget, com.google.gwt.dom.client.Element, com.google.gwt.dom.client.Element)
	 */
	public void add(T parent, Widget child, Element parentElement, Element childElement) throws InterfaceConfigException
	{
		// Does not need to add the child because it was already attached in processAttributes method
	}
	
	
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
	
	/**
	 * Populate the panel with declared items
	 * @param element
	 * @throws InterfaceConfigException 
	 */
	@Override
	public void processChildren(WidgetFactoryContext<T> context) throws InterfaceConfigException
	{
		Element element = context.getElement();
		T widget = context.getWidget();
		
		List<Element> itensCandidates = ensureChildrenSpans(element, true);
		int validRows = 0;
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			renderRow(widget, e, validRows, context.getWidgetId());
			processAttributesForRow(widget, e, validRows);
			validRows++;
		}
	}
	
	protected void renderRow(T widget, Element element, int index, String widgetId) throws InterfaceConfigException
	{
		List<Element> itensCandidates = ensureChildrenSpans(element, false);
		int validCells = 0;
		for (int i=0; i<itensCandidates.size(); i++)
		{
			Element e = (Element)itensCandidates.get(i);
			renderCell(widget, e, index, validCells, widgetId);
			processAttributesForCell(widget, e, index, validCells);
			validCells++;
		}
	}

	protected void renderCell(T widget, Element e, int indexRow, int indexCol, String widgetId) throws InterfaceConfigException
	{
		Element elementChild = ensureFirstChildSpan(e, false);
		if (elementChild != null)
		{
			String text = elementChild.getAttribute("_text");
			if (isWidget(elementChild))
			{
				addCell(widget, createChildWidget(elementChild, elementChild.getId()),indexRow, indexCol, widgetId);
			}
			else if (text != null && text.length() >0)
			{
				addCell(widget, text, indexRow, indexCol, widgetId);
			}
			else
			{
				addCell(widget, elementChild.getInnerHTML(), true, indexRow, indexCol, widgetId);
			}
		}
	}
	
	protected void processAttributesForRow(T widget, Element element, int index)
	{
		String styleName = element.getAttribute("_styleName");
		if (styleName != null && styleName.length() > 0)
		{
			widget.getRowFormatter().setStyleName(index, styleName);
		}
		String visible = element.getAttribute("_visible");
		if (visible != null && visible.length() > 0)
		{
			widget.getRowFormatter().setVisible(index, Boolean.parseBoolean(visible));
		}
		
		String verticalAlignment = element.getAttribute("_verticalAlignment");
		if (verticalAlignment != null && verticalAlignment.trim().length() > 0)
		{
			if (HasVerticalAlignment.ALIGN_BOTTOM.getVerticalAlignString().equals(verticalAlignment))
			{
				widget.getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_BOTTOM);
			}
			else if (HasVerticalAlignment.ALIGN_MIDDLE.getVerticalAlignString().equals(verticalAlignment))
			{
				widget.getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_MIDDLE);
			}
			else if (HasVerticalAlignment.ALIGN_TOP.getVerticalAlignString().equals(verticalAlignment))
			{
				widget.getRowFormatter().setVerticalAlign(index, HasVerticalAlignment.ALIGN_TOP);
			}
		}	
	}

	protected void processAttributesForCell(T widget, Element element, int indexRow, int indexCol)
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
	
	protected void addCell(T widget, Widget child, int indexRow, int indexCol, String widgetId)
	{
		prepareCell(widget, indexRow, indexCol, widgetId);
		widget.setWidget(indexRow, indexCol, child);
	}

	protected void addCell(T widget, String text, int indexRow, int indexCol, String widgetId)
	{
		addCell(widget, text, false, indexRow, indexCol, widgetId);
	}

	protected void addCell(T widget, String text, boolean asHTML, int indexRow, int indexCol, String widgetId)
	{
		prepareCell(widget, indexRow, indexCol, widgetId);
		if (asHTML)
		{
			widget.setHTML(indexRow, indexCol, text);
		}
		else
		{
			widget.setText(indexRow, indexCol, text);
		}
	}	
	
	protected abstract void prepareCell(T widget, int indexRow, int indexCol, String widgetId);
}
