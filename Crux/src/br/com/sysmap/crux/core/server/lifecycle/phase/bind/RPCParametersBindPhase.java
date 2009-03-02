package br.com.sysmap.crux.core.server.lifecycle.phase.bind;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;
import br.com.sysmap.crux.core.server.screen.Screen;

/**
 * 
 * @author Thiago
 *
 */
public class RPCParametersBindPhase extends AbstractParametersBindPhase
{
	private static final Log logger = LogFactory.getLog(AutoParametersBindPhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		DispatchData dispatchData = new DispatchData();
		dispatchData.setEvtCall(context.getRequest().getParameter(PARAM_EVT_CALL));
		dispatchData.setIdSender(context.getRequest().getParameter(PARAM_ID_SENDER));

		String screenName = context.getRequest().getParameter(SCREEN_ID);
		Screen screen = getScreen(screenName, context.getRequest());
		
		Enumeration<?> parameterNames = context.getRequest().getParameterNames();

		while (parameterNames.hasMoreElements())
		{
			String parameter = parameterNames.nextElement().toString();
			if (isControlParameter(parameter)) continue;
			try 
			{
				dispatchData.addParameter(parameter, FormatParameters.unformat(screen, parameter, context.getRequest().getParameter(parameter)));
			} 
			catch (Throwable e) 
			{
				logger.error(messages.parametersBindPhaseErrorPopulatingBean(parameter, e.getMessage()), e);
			}
		}
		
		context.setDispatchData(dispatchData);
	}
	
	
}
