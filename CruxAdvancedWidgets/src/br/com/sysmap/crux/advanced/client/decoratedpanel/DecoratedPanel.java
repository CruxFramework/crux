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
	private Element topCenterRightCell;
	private Element topRightCell;
	
	private Element middleLine;
	private Element middleLeftCell;
	private Element middleCenterCell;
	private Element middleRightCell;
	
	private Element bottomLine;
	private Element bottomLeftCell;
	private Element bottomCenterCell;
	private Element bottomRightCell;
	
	public DecoratedPanel(String width, String height, String styleName)
	{
		getTable().setClassName(styleName != null && styleName.trim().length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		getTable().setPropertyString("width", width);
		getTable().setPropertyString("height", height);
		
		topLine = DOM.createTR();
		topLeftCell = createTd("topLeftCell");
		topCenterCell = createTd("topCenterCell");
		topCenterRightCell = createTd("topCenterRightCell");
		topRightCell = createTd("topRightCell");
		
		middleLine = DOM.createTR();
		middleLeftCell = createTd("middleLeftCell");
		middleCenterCell = createTd("middleCenterCell");
		middleCenterCell.setPropertyInt("colSpan", 2);
		middleRightCell = createTd("middleRightCell");
		
		bottomLine = DOM.createTR();
		bottomLeftCell = createTd("bottomLeftCell");
		bottomCenterCell = createTd("bottomCenterCell");
		bottomCenterCell.setPropertyInt("colSpan", 2);
		bottomRightCell = createTd("bottomRightCell");
		
		DOM.appendChild(topLine, topLeftCell);
		DOM.appendChild(topLine, topCenterCell);
		DOM.appendChild(topLine, topCenterRightCell);
		DOM.appendChild(topLine, topRightCell);
		DOM.appendChild(getBody(), topLine);
	   	    
	    DOM.appendChild(middleLine, middleLeftCell);
	    DOM.appendChild(middleLine, middleCenterCell);
	    DOM.appendChild(middleLine, middleRightCell);
	    DOM.appendChild(getBody(), middleLine);
	    	    
	    DOM.appendChild(bottomLine, bottomLeftCell);
	    DOM.appendChild(bottomLine, bottomCenterCell);
	    DOM.appendChild(bottomLine, bottomRightCell);
	    DOM.appendChild(getBody(), bottomLine);
	    
	    setSpacing(0);
	}
	
	/**
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		cleanEmptySpaces(middleCenterCell);
		add(w, middleCenterCell);
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
	private Element createTd(String styleName)
	{
		Element td = DOM.createTD();
		td.setClassName(styleName);
		td.setInnerHTML("&nbsp;");
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
	 * @return the topCenterRightCell
	 */
	public Element getTopCenterRightCell()
	{
		return topCenterRightCell;
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