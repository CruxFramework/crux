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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.json.JsonResult;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;
import br.com.sysmap.crux.core.utils.HtmlUtils;

/**
 * Render the response for a RPC event
 * @author Thiago
 *
 */
public class RPCRenderResponsePhase implements Phase 
{
	private static final Log logger = LogFactory.getLog(RPCRenderResponsePhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		if (logger.isDebugEnabled()) logger.debug("RPCResponseRenderPhase => rendering response");
		try 
		{
			JsonResult result = handleErrors(context);
			if (result == null)
			{
				result = (JsonResult)context.getCycleResult();
			}
			if (result != null)
			{
				result.setDtoChanges(getDtoChanges(context.getDto(), context.getDispatchData()));
				context.getResponse().setContentType("text/plain;charset=utf-8");
				OutputStream out = context.getResponse().getOutputStream();
				byte[] bout = result.toString().getBytes("UTF-8");            
				context.getResponse().setIntHeader("Content-Length", bout.length);
				context.getResponse().setHeader("Connection", "keep-alive");
				context.getResponse().setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
				context.getResponse().addHeader("Cache-Control", "post-check=0, pre-check=0");
				context.getResponse().setHeader("Pragma", "no-cache");
				context.getResponse().setHeader("Expires", "-1");

				out.write(bout);
				out.flush();
				out.close();
			}
		} 
		catch (Exception e) 
		{
			throw new PhaseException(messages.rpcResponseRenderPhaseError(e.getLocalizedMessage()), e);
		}
	}
	
	protected JsonResult handleErrors(PhaseContext context)
	{
		if (context.isInterruptCycle())
		{
			return new JsonResult(JsonResult.CODE_ERR_VALIDATION, context.getInterruptMessage());
		}
		if (context.getPhaseException() != null)
		{
			return new JsonResult(JsonResult.CODE_ERR_METHOD, context.getPhaseException().getLocalizedMessage());
		}
		return null;
	}
	
	/**
	 * Render all dirty properties
	 * @param dto
	 * @param dispatchData 
	 * @param writer
	 */
	protected String getDtoChanges(Object dto, DispatchData dispatchData) 
	{
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		renderDtos(dto, dispatchData, writer);
		String updateScreen = strWriter.toString();
		if (updateScreen.length() > 0)
		{
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><span><span id=\"_dtos_\">"+updateScreen+"</span></span>";
		}
		return null;
	}
	
	/**
	 * Render all dirty properties
	 * @param dto
	 * @param dispatchData 
	 * @param writer
	 */
	protected void renderDtos(Object dto, DispatchData dispatchData, PrintWriter writer) 
	{
		for (String key : dispatchData.getParameters()) 
		{
			try
			{
				Object propValueAnt = dispatchData.getParameter(key);
				String propValue = BeanUtils.getProperty(dto, key);
				if (propValue == null && propValueAnt != null)
				{
					writer.println("<span id=\""+key+"\" value=\"\" ></span>");
				}
				else if (propValue != null && (propValueAnt == null || !propValueAnt.equals(propValue)))
				{
					writer.println("<span id=\""+key+"\" value=\""+HtmlUtils.filterValue(propValue)+"\" ></span>");
				}
			}
			catch (Throwable e) 
			{
				// Se o DTO não tiver uma propriedade declarada, isto não é um erro.
			}			
		}
	}
}
