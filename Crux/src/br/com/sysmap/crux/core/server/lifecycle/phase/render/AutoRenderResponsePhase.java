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
package br.com.sysmap.crux.core.server.lifecycle.phase.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.screen.Component;
import br.com.sysmap.crux.core.server.screen.ComponentRenderer;
import br.com.sysmap.crux.core.server.screen.Container;
import br.com.sysmap.crux.core.server.screen.Screen;
import br.com.sysmap.crux.core.server.screen.config.ComponentConfig;

/**
 * Render the response for an AUTO event
 * @author Thiago
 *
 */
public class AutoRenderResponsePhase extends AbstractRenderResponsePhase implements Phase 
{
	private static final Log logger = LogFactory.getLog(AutoRenderResponsePhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		if (logger.isDebugEnabled()) logger.debug("AutoResponseRenderPhase => rendering response");
		try 
		{
			context.getResponse().setContentType("text/plain;charset=iso-8859-1");
			PrintWriter writer = new PrintWriter(context.getResponse().getOutputStream());
			writer.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><span>");
			if (handleErrors(writer, context))
			{
				writer.println("<span id=\"_dtos_\">");
				renderDtos(context.getDto(), context.getDispatchData(),writer);
				writer.println("</span>");
				writer.println("<span id=\"_components_\">");
				renderComponents(context.getScreen(), writer);
				writer.println("</span>");
				renderScreen(context.getScreen(),writer);
			}
			writer.println("</span>");
			writer.close();
		} 
		catch (IOException e) 
		{
			throw new PhaseException(messages.autoResponseRenderPhaseError(e.getLocalizedMessage()), e);
		}
	}
	
	protected void renderScreen(Screen screen, PrintWriter writer) 
	{
		if (screen.isDirty())
		{
			writer.print("<span id=\"_screen_\" ");
			writer.print("_manageHistory=\"" + screen.isManageHistory() + "\" ");
			writer.println("></span>");
		}
	}

	/**
	 * Handle server exceptions
	 * @param writer
	 * @return if phaseRenderer must continue
	 */
	protected boolean handleErrors(PrintWriter writer, PhaseContext context)
	{
		if (context.isInterruptCycle())
		{
			writer.println("<span id=\"_interrupt_error_\"  _value=\""+context.getInterruptMessage()+"\" ></span>");
			return false;
		}
		if (context.getPhaseException() != null)
		{
			writer.println("<span id=\"_server_error_\" _value=\""+context.getPhaseException().getLocalizedMessage()+"\" ></span>");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Render all dirty components.
	 * @param screen
	 * @param writer
	 */
	protected void renderComponents(Screen screen, PrintWriter writer)
	{
		Iterator<Component> components = screen.iterateComponents();
		while (components.hasNext())
		{
			try
			{
				renderComponent(components.next(), writer);
			}
			catch (Throwable e) 
			{
				logger.error(messages.autoResponseRenderComponentError(e.getLocalizedMessage()), e);
			}
		}
	}
	
	protected void renderComponent(Component component, PrintWriter writer)
	{
		ComponentRenderer renderer = null;
		if (component.isDirty())
		{
			renderer = ComponentConfig.getComponentRenderer(component.getType());
			renderer.renderStart(component, writer);
		}
		
		if (component instanceof Container)
		{
			Container parent = (Container)component;
			Iterator<Component> components = parent.iterateComponents();
			while (components.hasNext())
			{
				renderComponent(components.next(), writer);
			}
		}
		if (component.isDirty())
		{
			renderer.renderEnd(component, writer);
		}
	}
}
