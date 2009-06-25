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
	public static final String DEFAULT_STYLE_NAME = "crux-DecoratedPanel" ;
	
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
	
	public DecoratedPanel(String width, String height, String styleName)
	{
		getTable().setClassName(styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		getTable().setPropertyString("width", width);
		getTable().getStyle().setProperty("height", height);
		
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
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		cleanEmptySpaces(middleCenterCell);
		add(w, middleCenterCell);
		contentWidget = w;
	}
	
	/**
	 * 
	 * @return
	 */
	public Widget getContentWidget()
	{
		return contentWidget;
	}

	/**
	 * Adds text to the body of the panel (middle center cell)
	 * @param text
	 */
	public void setContentText(String text)
	{
		cleanEmptySpaces(middleCenterCell);
		middleCenterCell.setInnerText(text);
	}
	
	/**
	 * Adds HTML to the body of the panel (middle center cell)
	 * @param html
	 */
	public void setContentHtml(String html)
	{
		cleanEmptySpaces(middleCenterCell);
		middleCenterCell.setInnerHTML(html);
	}

	/**
	 * @param middleCenterCell2
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
	 * @return
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
	 * @return the topLine
	 */
	public Element getTopLine()
	{
		return topLine;
	}

	/**
	 * @return the middleLine
	 */
	public Element getMiddleLine()
	{
		return middleLine;
	}

	/**
	 * @return the bottomLine
	 */
	public Element getBottomLine()
	{
		return bottomLine;
	}

	/**
	 * @return the topLeftCell
	 */
	public Element getTopLeftCell()
	{
		return topLeftCell;
	}

	/**
	 * @return the topCenterCell
	 */
	public Element getTopCenterCell()
	{
		return topCenterCell;
	}

	/**
	 * @return the topRightCell
	 */
	public Element getTopRightCell()
	{
		return topRightCell;
	}

	/**
	 * @return the middleLeftCell
	 */
	public Element getMiddleLeftCell()
	{
		return middleLeftCell;
	}

	/**
	 * @return the middleCenterCell
	 */
	public Element getMiddleCenterCell()
	{
		return middleCenterCell;
	}

	/**
	 * @return the middleRightCell
	 */
	public Element getMiddleRightCell()
	{
		return middleRightCell;
	}

	/**
	 * @return the bottomLeftCell
	 */
	public Element getBottomLeftCell()
	{
		return bottomLeftCell;
	}

	/**
	 * @return the bottomCenterCell
	 */
	public Element getBottomCenterCell()
	{
		return bottomCenterCell;
	}

	/**
	 * @return the bottomRightCell
	 */
	public Element getBottomRightCell()
	{
		return bottomRightCell;
	}
	
	/**
	 * @param align
	 */
	public void setHorizontalAlignment(HorizontalAlignmentConstant align)
	{
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getTextAlignString());		
	}
	
	/**
	 * @param align
	 */
	public void setVerticalAlignment(VerticalAlignmentConstant align)
	{
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getVerticalAlignString());		
	}
}