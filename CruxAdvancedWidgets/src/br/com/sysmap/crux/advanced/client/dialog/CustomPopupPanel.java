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
package br.com.sysmap.crux.advanced.client.dialog;

import java.util.Iterator;

import br.com.sysmap.crux.advanced.client.decoratedpanel.DecoratedPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO - Gessé - Comment this
 * 
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class CustomPopupPanel extends PopupPanel
{
	private static final String DEFAULT_STYLENAME = ".crux-CustomPopupPanel";

	private DecoratedPanel panel;

	/**
	 * 
	 */
	public CustomPopupPanel()
	{
		this(false);
	}

	/**
	 * @param autoHide
	 */
	public CustomPopupPanel(boolean autoHide)
	{
		this(autoHide, false);
	}

	/**
	 * @param autoHide
	 * @param modal
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
	 * @param w
	 */
	public void setTopRightWidget(Widget w)
	{
		panel.setTopRightWidget(w);
	}
	
	/**
	 * @return the topLine
	 */
	public Element getTopLine()
	{
		return panel.getTopLine();
	}

	/**
	 * @return the middleLine
	 */
	public Element getMiddleLine()
	{
		return panel.getMiddleLine();
	}

	/**
	 * @return the bottomLine
	 */
	public Element getBottomLine()
	{
		return panel.getBottomLine();
	}

	/**
	 * @return the topLeftCell
	 */
	public Element getTopLeftCell()
	{
		return panel.getTopLeftCell();
	}

	/**
	 * @return the topCenterCell
	 */
	public Element getTopCenterCell()
	{
		return panel.getTopCenterCell();
	}

	/**
	 * @return the topRightCell
	 */
	public Element getTopRightCell()
	{
		return panel.getTopRightCell();
	}

	/**
	 * @return the middleLeftCell
	 */
	public Element getMiddleLeftCell()
	{
		return panel.getMiddleLeftCell();
	}

	/**
	 * @return the middleCenterCell
	 */
	public Element getMiddleCenterCell()
	{
		return panel.getMiddleCenterCell();
	}

	/**
	 * @return the middleRightCell
	 */
	public Element getMiddleRightCell()
	{
		return panel.getMiddleRightCell();
	}

	/**
	 * @return the bottomLeftCell
	 */
	public Element getBottomLeftCell()
	{
		return panel.getBottomLeftCell();
	}

	/**
	 * @return the bottomCenterCell
	 */
	public Element getBottomCenterCell()
	{
		return panel.getBottomCenterCell();
	}

	/**
	 * @return the bottomRightCell
	 */
	public Element getBottomRightCell()
	{
		return panel.getBottomRightCell();
	}	
	
	/**
	 * 
	 * @return
	 */
	public Widget getContentWidget()
	{
		return panel.getContentWidget();
	}
}