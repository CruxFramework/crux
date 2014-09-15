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
package org.cruxframework.crux.widgets.client.rollingtabs;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gesse - Comment this
 * @author Gesse S. F. Dafe
 */
public class SimpleDecoratedPanel extends CellPanel
{
	private Element line;
	private Element leftCell;
	private Element centerCell;
	private Element rightCell;
	
	public SimpleDecoratedPanel()
	{
		getTable().setClassName("");
	
		line = DOM.createTR();
		
		Element templateTd = createTemplateTd();
		centerCell = createTd(templateTd, "flapCenter");
		
		rightCell = createTd(templateTd, "flapRight");
		rightCell.setInnerHTML("&nbsp;");

		leftCell = templateTd;
		leftCell.setClassName("flapLeft");
		leftCell.setInnerHTML("&nbsp;");
		centerCell.setPropertyInt("colSpan", 2);
		
	    line.appendChild(leftCell);
	    line.appendChild(centerCell);
	    line.appendChild(rightCell);
	    getBody().appendChild(line);
	    	    
	    setSpacing(0);
	}
	
	/**
	 * Adds a widget to the body of the panel (middle center cell)
	 * @param w
	 */
	public void setContentWidget(Widget w)
	{
		cleanEmptySpaces(centerCell);
		add(w, centerCell);
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
	private Element createTd(Element templateTD, String styleName)
	{
		Element td = templateTD.cloneNode(false).cast();
		td.setClassName(styleName);
		
		td.setPropertyString("align", "center");
		td.setPropertyString("valign", "middle");		
		return td;
	}

	/**
	 * Creates a TD with the given style name 
	 * @param styleName
	 * @return
	 */
	private Element createTemplateTd()
	{
		Element td = DOM.createTD();
		td.setPropertyString("align", "center");
		td.setPropertyString("valign", "middle");		
		return td;
	}
}