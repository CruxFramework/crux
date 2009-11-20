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
package br.com.sysmap.crux.widgets.client.event.row;

import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.widgets.client.event.Events;

import com.google.gwt.dom.client.Element;

/**
 * TODO - Gessé - Comment this
 * @author Gessé S. F. Dafé - <code>gessedafe@gmail.com</code>
 */
public class RowEventsBind extends EvtBind
{
	/**
	 * @param widget
	 */
	public static void bindClickRowEvent(Element element, HasRowClickHandlers widget)
	{
		final Event clickLineEvent = getWidgetEvent(element, Events.ROW_CLICK);
		if (clickLineEvent != null)
		{
			widget.addRowClickHandler(new RowClickHandler()
			{
				public void onRowClick(RowClickEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(clickLineEvent, event);					
				}
			});
		}
	}
	
	public static void bindDoubleClickRowEvent(Element element, HasRowDoubleClickHandlers widget)
	{
		final Event doubleClickLineEvent = getWidgetEvent(element, Events.ROW_DOUBLE_CLICK);
		if (doubleClickLineEvent != null)
		{
			widget.addRowDoubleClickHandler(new RowDoubleClickHandler()
			{
				public void onRowDoubleClick(RowDoubleClickEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(doubleClickLineEvent, event);					
				}
			});
		}
	}
	
	public static void bindRenderRowEvent(Element element, HasRowRenderHandlers widget)
	{
		final Event renderEvent = getWidgetEvent(element, Events.ROW_RENDER);
		if (renderEvent != null)
		{
			widget.addRowRenderHandler(new RowRenderHandler()
			{
				public void onRowRender(RowRenderEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(renderEvent, event);					
				}				
			});
		}
	}
	
	public static void bindBeforeSelectRowEvent(Element element, HasBeforeRowSelectHandlers widget)
	{
		final Event evt = getWidgetEvent(element, Events.BEFORE_ROW_SELECT);
		if (evt != null)
		{
			widget.addBeforeRowSelectHandler(new BeforeRowSelectHandler()
			{
				public void onBeforeRowSelect(BeforeRowSelectEvent event)
				{
					br.com.sysmap.crux.core.client.event.Events.callEvent(evt, event);					
				}				
			});
		}
	}
}