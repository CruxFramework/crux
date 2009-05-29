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
package br.com.sysmap.crux.core.rebind.screen;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public class WidgetParserImpl implements WidgetParser
{
	private static final Log logger = LogFactory.getLog(WidgetParserImpl.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	public void parse(Widget widget, Object element) 
	{
		Element elem = (Element) element;
		
		Attributes attrs =  elem.getAttributes();
		for (Object object : attrs) 
		{
			Attribute attr = (Attribute)object;
			String attrName = attr.getName();
			
			if (attrName.equals("id") || attrName.equals("_type"))
			{
				continue;
			}
			
			if (attrName.startsWith("_on"))
			{
				setEvent(widget, attrName, attr.getValue());
			}
			else if (attrName.equals("_formatter"))
			{
				setProperty(widget, "formatter", attr.getValue());
			}
			else
			{
				widget.addProperty(attrName, attr.getValue());
			}
		}
	}
	
	protected void setProperty(Widget widget, String propName, String value)
	{
		try 
		{
			BeanUtils.copyProperty(widget, propName, value);
		} 
		catch (Throwable e) 
		{
			if (logger.isDebugEnabled()) logger.debug(messages.widgetParserImplWidgetPropertyError(propName, widget.getId()));
		} 
	}

	protected void setEvent(Widget widget, String evtName, String value)
	{
		Event event = EventFactory.getEvent(evtName, value);
		
		if (event != null)
		{
			widget.addEvent(event);
		}
	}
	
}
