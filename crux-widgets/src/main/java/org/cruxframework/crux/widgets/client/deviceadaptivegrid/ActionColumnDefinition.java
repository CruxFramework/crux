package org.cruxframework.crux.widgets.client.deviceadaptivegrid;


import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition;
import org.cruxframework.crux.widgets.client.grid.WidgetColumnDefinition.WidgetColumnCreator;

import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
* ===========================================================================
* Copyright (c) 2013 Sysmap Solutions Software e Consultoria Ltda
* Todos os direitos reservados.
* ===========================================================================
*
* Project:     MCA
* File:        ActionColumnDefinition.java
*
* @author     samuel.cardoso - <code>samuel.cardoso@sysmap.com.br</code>
* @created    08/07/2013
* @version    1.0
*
* ===========================================================================
*/
public class ActionColumnDefinition extends WidgetColumnDefinition 
{

	/**
	 * @param label
	 * @param width
	 * @param creator
	 * @param visible
	 * @param frozen
	 * @param horizontalAlign
	 * @param verticalAlign
	 */
	public ActionColumnDefinition(String label, String width, WidgetColumnCreator creator, boolean visible, 
			boolean frozen, HorizontalAlignmentConstant horizontalAlign, VerticalAlignmentConstant verticalAlign)
	{
		super(label, width, creator, visible, frozen, horizontalAlign, verticalAlign);
	}
}
