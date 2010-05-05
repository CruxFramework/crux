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
package br.com.sysmap.crux.widgets.client.decoratedpanel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * Panel based on a 3x3 table, useful to build rounded corners boxes. 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedPanel extends CellPanel
{
	private static final String DEFAULT_STYLE_NAME = "crux-DecoratedPanel" ;
	
	private Element topLine;
	private Element topLeftCell;
	private Element topCenterCell;
	private Element topRightCell;
	
	private Element middleLine;
	
	private Element middleLeftCell;
	private Element middleCenterCell;
	private Element middleRightCell;
	
	private Element bottomLine;
	private Element bottomLeftCell;
	private Element bottomCenterCell;
	private Element bottomRightCell;
	private Widget contentWidget;
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param styleName
	 */
	public DecoratedPanel(String width, String height, String styleName)
	{
		getTable().setClassName(styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		getTable().setPropertyString("width", width);
		getTable().getStyle().setProperty("height", height);
//		getTable().getStyle().setProperty("tableLayout", "fixed");
		
		topLine = DOM.createTR();
		topLeftCell = createTd("topLeftCell", true);
		topCenterCell = createTd("topCenterCell", true);
		topRightCell = createTd("topRightCell", true);
		
		middleLine = DOM.createTR();
		
		Element wrapper = DOM.createTable().cast();
		wrapper.setPropertyInt("cellSpacing", 0);
		wrapper.setPropertyInt("cellPadding", 0);
		wrapper.setPropertyString("width", "100%");
		wrapper.getStyle().setProperty("height", "100%");
	    Element wrapperBody = DOM.createTBody();
	    Element wrapperLine = DOM.createTR();
	    
	    Element middleLineTD = createTd("", false);
	    middleLineTD.setPropertyInt("colSpan", 4);		
	    middleLineTD.getStyle().setProperty("padding", "0px");
	    middleLineTD.appendChild(wrapper);
		
		middleLeftCell = createTd("middleLeftCell", true);
		middleCenterCell = createTd("middleCenterCell", false);
		middleRightCell = createTd("middleRightCell", true);
		
		bottomLine = DOM.createTR();
		bottomLeftCell = createTd("bottomLeftCell", true);
		bottomCenterCell = createTd("bottomCenterCell", true);
		bottomRightCell = createTd("bottomRightCell", true);
		
		DOM.appendChild(topLine, topLeftCell);
		DOM.appendChild(topLine, topCenterCell);
		DOM.appendChild(topLine, topRightCell);
		DOM.appendChild(getBody(), topLine);
	   	    
	    DOM.appendChild(wrapperLine, middleLeftCell);
	    DOM.appendChild(wrapperLine, middleCenterCell);
	    DOM.appendChild(wrapperLine, middleRightCell);
	    DOM.appendChild(wrapperBody, wrapperLine);
	    DOM.appendChild(wrapper, wrapperBody);
	    DOM.appendChild(middleLineTD, wrapper);
	    DOM.appendChild(middleLine, middleLineTD);
	    DOM.appendChild(getBody(), middleLine);
	    	    
	    DOM.appendChild(bottomLine, bottomLeftCell);
	    DOM.appendChild(bottomLine, bottomCenterCell);
	    DOM.appendChild(bottomLine, bottomRightCell);
	    DOM.appendChild(getBody(), bottomLine);
	    
	    setSpacing(0);
	}
	
	/**
	 * Sets the widget which will be displayed at the northeastern cell
	 * @param widget
	 */
	public void setTopRightWidget(Widget widget)
	{
		getTopRightCell().setInnerText("");
		add(widget, getTopRightCell());
		setCellVerticalAlignment(widget, HasVerticalAlignment.ALIGN_MIDDLE);
		setCellHorizontalAlignment(widget, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	/**
	 * Sets the widget which will be displayed in the body of the panel
	 * @param widget
	 */
	public void setContentWidget(Widget widget)
	{
		cleanEmptySpaces(middleCenterCell);
		add(widget, middleCenterCell);
		contentWidget = widget;
	}
	
	/**
	 * @return the widget displayed in the body of the panel
	 */
	public Widget getContentWidget()
	{
		return contentWidget;
	}

	/**
	 * Sets a text to be displayed in the body of the panel
	 * @param text
	 */
	public void setContentText(String text)
	{
		cleanEmptySpaces(middleCenterCell);
		middleCenterCell.setInnerText(text);
	}
	
	/**
	 * Sets an HTML text to be displayed in the body of the panel
	 * @param html
	 */
	public void setContentHtml(String html)
	{
		cleanEmptySpaces(middleCenterCell);
		middleCenterCell.setInnerHTML(html);
	}

	/**
	 * Removes the empty spaces from the element's inner text 
	 * @param cell
	 */
	private void cleanEmptySpaces(Element cell)
	{
		String text = cell.getInnerText();
		
		if(text != null && text.trim().length() == 0)
		{
			cell.setInnerText("");
		}
	}

	/**
	 * Creates a TD with the given style name
	 * @param styleName
	 * @param fillWithBlank if true, inserts a blank space into the TD's inner text
	 * @return a table cell (TD)
	 */
	private Element createTd(String styleName, boolean fillWithBlank)
	{
		Element td = DOM.createTD();
		td.setClassName(styleName);
		if(fillWithBlank)
		{
			td.setInnerHTML("&nbsp;");
		}
		td.setPropertyString("align", "center");
		td.setPropertyString("valign", "middle");		
		return td;
	}

	/**
	 * @return the top TR
	 */
	public Element getTopLine()
	{
		return topLine;
	}

	/**
	 * @return the middle TR
	 */
	public Element getMiddleLine()
	{
		return middleLine;
	}

	/**
	 * @return the bottom TR
	 */
	public Element getBottomLine()
	{
		return bottomLine;
	}

	/**
	 * @return the top left TD
	 */
	public Element getTopLeftCell()
	{
		return topLeftCell;
	}

	/**
	 * @return the top center TD
	 */
	public Element getTopCenterCell()
	{
		return topCenterCell;
	}

	/**
	 * @return the top right TD
	 */
	public Element getTopRightCell()
	{
		return topRightCell;
	}

	/**
	 * @return the middle left TD
	 */
	public Element getMiddleLeftCell()
	{
		return middleLeftCell;
	}

	/**
	 * @return the middle center TD (panel's body)
	 */
	public Element getMiddleCenterCell()
	{
		return middleCenterCell;
	}

	/**
	 * @return the middle right TD
	 */
	public Element getMiddleRightCell()
	{
		return middleRightCell;
	}

	/**
	 * @return the bottom left TD
	 */
	public Element getBottomLeftCell()
	{
		return bottomLeftCell;
	}

	/**
	 * @return the bottom center TD
	 */
	public Element getBottomCenterCell()
	{
		return bottomCenterCell;
	}

	/**
	 * @return the bottom right TD
	 */
	public Element getBottomRightCell()
	{
		return bottomRightCell;
	}
	
	/**
	 * Sets the horizontal alignment of the body
	 * @param align
	 */
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
	{
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getTextAlignString());		
	}
	
	/**
	 * Sets the vertical alignment of the body
	 * @param align
	 */
	public void setVerticalAlignment(VerticalAlignmentConstant align)
	{
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getVerticalAlignString());		
	}
}