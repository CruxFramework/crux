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

import br.com.sysmap.crux.advanced.client.util.TextSelectionUtils;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * HTML face DecoratedButton
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedButtonFace extends Widget
{
	private TableElement face;
	private TableCellElement faceText;

	/**
	 * Default constructor
	 */
	public DecoratedButtonFace()
	{
		this.face = createElement();
		super.setElement(face);
	}

	/**
	 * Creates a 3 X 1 table, which will be the element of the DecoratedButtion widget
	 * @return
	 */
	private TableElement createElement()
	{
		TableElement table = DOM.createTable().cast();
		table.setCellSpacing(0);
		table.setCellPadding(0);
		table.setClassName(DecoratedButton.DEFAULT_STYLE_NAME);

		Element tableBody = DOM.createTBody();
		table.appendChild(tableBody);

		TableRowElement tr = DOM.createTR().cast();
		tableBody.appendChild(tr);

		TableCellElement tdLeft = DOM.createTD().cast();
		tdLeft.setClassName("leftCell");
		tdLeft.setInnerHTML("&nbsp;");
		TextSelectionUtils.makeUnselectable(tdLeft);
		tr.appendChild(tdLeft);

		TableCellElement tdCenter = DOM.createTD().cast();
		tdCenter.setClassName("centerCell");
		tdCenter.setPropertyBoolean("noWrap", true);
		tdCenter.setAlign("center");
		TextSelectionUtils.makeUnselectable(tdCenter);
		tr.appendChild(tdCenter);

		this.faceText = tdCenter;

		TableCellElement tdRight = DOM.createTD().cast();
		tdRight.setClassName("rightCell");
		tdRight.setInnerHTML("&nbsp;");
		TextSelectionUtils.makeUnselectable(tdRight);
		tr.appendChild(tdRight);

		return table;
	}

	/**
	 * Sets the face's text
	 * @param text
	 */
	public void setText(String text)
	{
		this.faceText.setInnerText(text);
	}

	/**
	 * Gets the face's text 
	 * @return
	 */
	public String getText()
	{
		return this.faceText.getInnerText();
	}
}