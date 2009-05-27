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
package br.com.sysmap.crux.advanced.client.decoratedbutton;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.PushButton;

/**
 * PushButton, based on a 3 X 1 table, useful to build rounded corners. 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedButton extends PushButton
{
	public static final String DEFAULT_STYLE_NAME = "crux-DecoratedButton" ;
	private String text = "";
	private String width = "";
	
	public DecoratedButton()
	{
		super();
		super.setStyleName(DEFAULT_STYLE_NAME);
	}

	/**
	 * @param text
	 */
	private void updateFaces()
	{
		getUpFace().setHTML(createFace());
		getUpDisabledFace().setHTML(createFace());
		getUpHoveringFace().setHTML(createFace());

		getDownFace().setHTML(createFace());
		getDownDisabledFace().setHTML(createFace());
		getDownHoveringFace().setHTML(createFace());
	}
	
	/**
	 * @param text 
	 * @param face 
	 * @return
	 */
	private String createFace()
	{
		TableElement table = DOM.createTable().cast();
		table.setCellSpacing(0);
		table.setCellPadding(0);
		table.setWidth(this.width);
		table.setClassName("html-face-table");
	
		Element tableBody = DOM.createTBody();
		table.appendChild(tableBody);
	    	    		
		TableRowElement tr = DOM.createTR().cast();
		tableBody.appendChild(tr);
		
		TableCellElement tdLeft = DOM.createTD().cast();
		tdLeft.setClassName("leftCell");
		tdLeft.setInnerHTML("&nbsp;");
		tr.appendChild(tdLeft);
		
		TableCellElement tdCenter = DOM.createTD().cast();
		tdCenter.setClassName("centerCell");
		tdCenter.setAlign("center");
		tdCenter.setInnerText(this.text);
		tr.appendChild(tdCenter);
		
		TableCellElement tdRight = DOM.createTD().cast();
		tdRight.setClassName("rightCell");
		tdRight.setInnerHTML("&nbsp;");
		tr.appendChild(tdRight);
		
		return table.getString();
	}
	
	@Override
	public void setText(String text)
	{
		this.text = text;
		updateFaces();
	}
	
	@Override
	public void setHeight(String height)
	{
		super.setHeight(height);
		updateFaces();
	}
	
	@Override
	public void setWidth(String width)
	{
		this.width = width;
		updateFaces();
	}
}