package br.com.sysmap.crux.core.server.lifecycle.phase.dispatch;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.event.ValidateException;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

/**
 * Dispatcher for handle crux auto events at the server side.
 * @author Thiago
 *
 */
public class AutoDispatchPhase extends AbstractDispatchPhase
{
	private static final Log logger = LogFactory.getLog(AutoDispatchPhase.class);
	private ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	@Override
	public void execute(PhaseContext context) throws PhaseException 
	{
		try 
		{
			DispatchData dispatchData = context.getDispatchData();
			String evtCall = dispatchData.getEvtCall();
			
			String[] call = RegexpPatterns.REGEXP_DOT.split(evtCall);
				
			Object controller = getController(call[0]);
			bindControllerParameters(controller, dispatchData);
			try
			{
				BeanUtils.copyProperty(controller, "screen", context.getScreen());
			}
			catch (Throwable e) 
			{
				if (logger.isInfoEnabled()) logger.info(messages.dispatchPhasePropertyNotBound("screen"));
			}
			
			Class<?>[] parametersTypes = {};
			Object[] parametersValues = {};
			dispatch(controller, call[1], parametersTypes, parametersValues);
			context.setCycleResult(context.getScreen());
			context.setDto(controller);
		} 
		catch (ValidateException e) 
		{
			context.interruptCycle(e.getLocalizedMessage());
		} 
		catch (Throwable e) 
		{
			throw new PhaseException(e);
		}
	}
}
