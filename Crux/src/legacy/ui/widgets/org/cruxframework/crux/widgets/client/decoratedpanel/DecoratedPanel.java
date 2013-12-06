/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.decoratedpanel;

import org.cruxframework.crux.core.client.Legacy;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel based on a 3x3 table, useful to build rounded corners boxes. 
 * @author Gesse S. F. Dafe
 */
@Deprecated
@Legacy
public class DecoratedPanel extends CellPanel implements HasHorizontalAlignment, HasVerticalAlignment
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

	private VerticalAlignmentConstant verticalAlign;
	private HorizontalAlignmentConstant horizontalAlign;
	
	/**
	 * Constructor
	 */
	public DecoratedPanel()
	{
		this(null, null, null);
	}
	
	/**
	 * Constructor
	 * @param width
	 * @param height
	 * @param styleName
	 */
	public DecoratedPanel(String width, String height, String styleName)
	{
		Element table = getTable();
		table.setClassName(styleName != null && styleName.length() > 0 ? styleName : DEFAULT_STYLE_NAME);
		table.setPropertyString("width", (width==null?"":width));
		table.getStyle().setProperty("height", (height==null?"":height));
		getTable().getStyle().setTableLayout(com.google.gwt.dom.client.Style.TableLayout.FIXED);
	 	
		Element templateTR = DOM.createTR();
		topLine = templateTR;
		middleLine = templateTR.cloneNode(false).cast();
		Element wrapperLine = templateTR.cloneNode(false).cast();
		bottomLine = templateTR.cloneNode(false).cast();
		
		Element templateTd = createTemplateTd(true);
		Element templateSpaceTd = createTemplateTd(false);

		topLeftCell = templateTd;
		topCenterCell = createTd(templateTd, "topCenterCell");
		topRightCell = createTd(templateTd, "topRightCell");
		
		Element wrapper = DOM.createTable().cast();
		wrapper.setPropertyInt("cellSpacing", 0);
		wrapper.setPropertyInt("cellPadding", 0);
		wrapper.setPropertyString("width", "100%");
		wrapper.getStyle().setProperty("height", "100%");
	    Element wrapperBody = DOM.createTBody();
	    
	    Element middleLineTD = templateSpaceTd;
	    middleLineTD.setPropertyInt("colSpan", 3);	
	    middleLineTD.getStyle().setProperty("padding", "0px");
	    middleLineTD.appendChild(wrapper);
		
		middleLeftCell = createTd(templateTd, "middleLeftCell");
		middleCenterCell = createTd(templateSpaceTd, "middleCenterCell");
		middleRightCell = createTd(templateTd, "middleRightCell");
		
		bottomLeftCell = createTd(templateTd, "bottomLeftCell");
		bottomCenterCell = createTd(templateTd, "bottomCenterCell");
		bottomRightCell = createTd(templateTd, "bottomRightCell");
		
		topLeftCell.setClassName("topLeftCell");

		topLine.appendChild(topLeftCell);
		topLine.appendChild(topCenterCell);
		topLine.appendChild(topRightCell);
		Element body = getBody();
		body.appendChild(topLine);
	   	    
	    wrapperLine.appendChild(middleLeftCell);
	    wrapperLine.appendChild(middleCenterCell);
	    wrapperLine.appendChild(middleRightCell);
	    wrapperBody.appendChild(wrapperLine);
	    wrapper.appendChild(wrapperBody);
	    middleLineTD.appendChild(wrapper);
	    middleLine.appendChild(middleLineTD);
	    body.appendChild(middleLine);
	    	    
	    bottomLine.appendChild(bottomLeftCell);
	    bottomLine.appendChild(bottomCenterCell);
	    bottomLine.appendChild(bottomRightCell);
	    body.appendChild(bottomLine);
	    
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
	private Element createTd(Element template, String styleName)
	{
		Element td = template.cloneNode(true).cast();
		td.setClassName(styleName);
		return td;
	}

	/**
	 * Creates a TD with the given style name
	 * @param styleName
	 * @param fillWithBlank if true, inserts a blank space into the TD's inner text
	 * @return a table cell (TD)
	 */
	private Element createTemplateTd(boolean fillWithBlank)
	{
		Element td = DOM.createTD();
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
		this.horizontalAlign = align;
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getTextAlignString());		
	}
	
	/**
	 * Sets the vertical alignment of the body
	 * @param align
	 */
	public void setVerticalAlignment(VerticalAlignmentConstant align)
	{
		this.verticalAlign = align;
		DOM.setElementProperty(getMiddleCenterCell(), "align", align.getVerticalAlignString());		
	}

	public VerticalAlignmentConstant getVerticalAlignment()
    {
	    return this.verticalAlign;
    }

	public HorizontalAlignmentConstant getHorizontalAlignment()
    {
	    return this.horizontalAlign;
    }
}