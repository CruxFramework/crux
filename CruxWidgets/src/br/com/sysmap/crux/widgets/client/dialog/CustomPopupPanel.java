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
package br.com.sysmap.crux.widgets.client.dialog;

import java.util.Iterator;

import br.com.sysmap.crux.widgets.client.decoratedpanel.DecoratedPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A decorated PopupPanel
 * @author Gesse S. F. Dafe
 */
public class CustomPopupPanel extends PopupPanel
{
	private static final String DEFAULT_STYLENAME = ".crux-CustomPopupPanel";
	private DecoratedPanel panel;

	/**
	 * Default constructor
	 */
	public CustomPopupPanel()
	{
		this(false);
	}

	/**
	 * @param autoHide <code>true</code> if the dialog box should be automatically hidden when the user clicks outside of it
	 */
	public CustomPopupPanel(boolean autoHide)
	{
		this(autoHide, false);
	}

	/**
	 * Constructor
	 * @param autoHide <code>true</code> if the dialog box should be automatically hidden when the user clicks outside of it
	 * @param modal <code>true</code> if keyboard or mouse events that do not target the PopupPanel or its children should be ignored
	 */
	public CustomPopupPanel(boolean autoHide, boolean modal)
	{
		super(autoHide, modal);
		panel = new DecoratedPanel("100%", "100%", null);
		panel.setStyleName("");
		setStylePrimaryName(DEFAULT_STYLENAME);
		super.setWidget(panel);
		getTopCenterCell().setInnerText("");
	}

	@Override
	public void clear()
	{
		panel.clear();
	}

	@Override
	public Widget getWidget()
	{
		return panel;
	}

	@Override
	public Iterator<Widget> iterator()
	{
		return panel.iterator();
	}

	@Override
	public boolean remove(Widget w)
	{
		return panel.remove(w);
	}

	@Override
	public void setWidget(Widget w)
	{
		panel.setContentWidget(w);
	}
	
	/**
	 * Sets the widget to be displayed at in to right cell 
	 * @param w
	 */
	public void setTopRightWidget(Widget w)
	{
		panel.setTopRightWidget(w);
	}
	
	/**
	 * Gets the first row element (TR)
	 * @return a TR element
	 */
	public Element getTopLine()
	{
		return panel.getTopLine();
	}

	/**
	 * Gets the second row element (TR)
	 * @return a TR element
	 */
	public Element getMiddleLine()
	{
		return panel.getMiddleLine();
	}

	/**
	 * Gets the third row element (TR)
	 * @return a TR element
	 */
	public Element getBottomLine()
	{
		return panel.getBottomLine();
	}

	/**
	 * Gets the top left cell (TD)
	 * @return a TD element
	 */
	public Element getTopLeftCell()
	{
		return panel.getTopLeftCell();
	}

	/**
	 * Gets the top center cell (TD)
	 * @return a TD element
	 */
	public Element getTopCenterCell()
	{
		return panel.getTopCenterCell();
	}

	/**
	 * Gets the top right cell (TD)
	 * @return a TD element
	 */
	public Element getTopRightCell()
	{
		return panel.getTopRightCell();
	}

	/**
	 * Gets the middle left cell (TD)
	 * @return a TD element
	 */
	public Element getMiddleLeftCell()
	{
		return panel.getMiddleLeftCell();
	}

	/**
	 * Gets the middle center cell (TD)
	 * @return a TD element
	 */
	public Element getMiddleCenterCell()
	{
		return panel.getMiddleCenterCell();
	}

	/**
	 * Gets the middle right cell (TD)
	 * @return a TD element
	 */
	public Element getMiddleRightCell()
	{
		return panel.getMiddleRightCell();
	}

	/**
	 * Gets the bottom left cell (TD)
	 * @return a TD element
	 */
	public Element getBottomLeftCell()
	{
		return panel.getBottomLeftCell();
	}

	/**
	 * Gets the bottom center cell (TD)
	 * @return a TD element
	 */
	public Element getBottomCenterCell()
	{
		return panel.getBottomCenterCell();
	}

	/**
	 * Gets the bottom right cell (TD)
	 * @return a TD element
	 */
	public Element getBottomRightCell()
	{
		return panel.getBottomRightCell();
	}	
	
	/**
	 * Gets the widget which is being displayed in the body (middle center cell)
	 * @return a TD element  
	 */
	public Widget getContentWidget()
	{
		return panel.getContentWidget();
	}
}