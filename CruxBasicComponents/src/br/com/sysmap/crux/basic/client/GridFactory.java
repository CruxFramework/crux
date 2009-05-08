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
package br.com.sysmap.crux.basic.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * Base factory class for all panels
 * @author Thiago Bustamante
 */
public class GridFactory extends HTMLTableFactory<Grid>
{

	@Override
	protected void addCell(Grid widget, Widget child, int indexRow, int indexCol)
	{
		prepareCell(widget, indexRow, indexCol);
		widget.setWidget(indexRow, indexCol, child);
	}

	@Override
	protected void addCell(Grid widget, String text, int indexRow, int indexCol)
	{
		prepareCell(widget, indexRow, indexCol);
		widget.setText(indexRow, indexCol, text);
	}

	@Override
	protected void addCell(Grid widget, String text, boolean asHTML, int indexRow, int indexCol)
	{
		if (asHTML)
		{
			widget.setHTML(indexRow, indexCol, text);
		}
		else
		{
			widget.setText(indexRow, indexCol, text);
		}
	}

	@Override
	protected Grid instantiateWidget(Element element, String widgetId)
	{
		return new Grid();
	}

	private void prepareCell(Grid widget, int indexRow, int indexCol)
	{
		if (indexRow < 0 || indexCol < 0)
		{
			throw new IndexOutOfBoundsException();
			//TODO: colocar mensagem
		}
		if (widget.getRowCount() < indexRow)
		{
			widget.resizeRows(indexRow+1);
		}
		if (widget.getColumnCount() < indexCol)
		{
			widget.resizeColumns(indexCol + 1);
		}
	}
}
