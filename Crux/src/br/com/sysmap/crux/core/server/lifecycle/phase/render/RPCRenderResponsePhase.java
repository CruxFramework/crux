package br.com.sysmap.crux.core.server.lifecycle.phase.render;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.json.JsonResult;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;

/**
 * Render the response for a RPC event
 * @author Thiago
 *
 */
public class RPCRenderResponsePhase extends AbstractRenderResponsePhase implements Phase 
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
	
}
