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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasText;

/**
 * PushButton, based on a 3 X 1 table, useful to build rounded corners.
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class DecoratedButton extends FocusWidget implements HasText
{
	public static final String DEFAULT_STYLE_NAME = "crux-DecoratedButton";
	private TableElement faceBox;
	private TableCellElement faceText;
	private boolean allowClick;
	private String baseStyleName = DEFAULT_STYLE_NAME;

	public DecoratedButton()
	{
		super();
		this.faceBox = createElement();
		super.setElement(faceBox);
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.KEYEVENTS);
		Accessibility.setRole(getElement(), Accessibility.ROLE_BUTTON);
	}

	/**
	 * @param text
	 * @param face
	 * @return
	 */
	private TableElement createElement()
	{
		TableElement table = DOM.createTable().cast();
		table.setCellSpacing(0);
		table.setCellPadding(0);
		table.setClassName(DEFAULT_STYLE_NAME);

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

	public void setText(String text)
	{
		this.faceText.setInnerText(text);
	}

	public String getText()
	{
		return this.faceText.getInnerText();
	}

	@Override
	public void onBrowserEvent(Event event)
	{
		if (!isEnabled())
		{
			return;
		}

		int type = DOM.eventGetType(event);
		
		if(type == Event.ONCLICK)
		{
			if(!this.allowClick)
			{
				event.stopPropagation();
				return;
			}
		}
		else if(type == Event.ONMOUSEUP)
		{
			this.faceBox.setClassName(this.baseStyleName);
			onClick();
		}		
		else if (type == Event.ONMOUSEDOWN)
		{
			this.faceBox.setClassName(this.baseStyleName + " " + this.baseStyleName + "-down");
		}
		else if (type == Event.ONMOUSEOUT)
		{
			this.faceBox.setClassName(this.baseStyleName);
		}
		else if (wasClickByKeyEvent(event))
		{
			onClick();
		}

		super.onBrowserEvent(event);
	}

	/**
	 * 
	 */
	protected void onClick()
	{
		this.allowClick = true;

		NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		getElement().dispatchEvent(evt);

		this.allowClick = false;
	}

	/**
	 * @param event
	 * @return
	 */
	private boolean wasClickByKeyEvent(Event event)
	{
		int eventType = DOM.eventGetType(event);

		if ((event.getTypeInt() & Event.KEYEVENTS) != 0)
		{
			char keyCode = (char) DOM.eventGetKeyCode(event);

			return (eventType == Event.ONKEYUP && keyCode == ' ') || (eventType == Event.ONKEYPRESS && (keyCode == '\n' || keyCode == '\r'));
		}

		return false;
	}
	
	@Override
	public void setStyleName(String style)
	{
		if(this.baseStyleName.equals(DEFAULT_STYLE_NAME) || style.indexOf(" ") < 0)
		{
			this.baseStyleName = style;
		}
		
		super.setStyleName(style);
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		this.faceBox.setClassName(this.baseStyleName + " " + this.baseStyleName + (enabled ? "" : "-disabled"));
		super.setEnabled(enabled);
	}
}