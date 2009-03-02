package br.com.sysmap.crux.core.server.lifecycle.phase.bind;

import java.util.Enumeration;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.server.lifecycle.PhaseContext;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.lifecycle.phase.dispatch.DispatchData;
import br.com.sysmap.crux.core.server.screen.Component;
import br.com.sysmap.crux.core.server.screen.Screen;

/**
 * 
 * @author Thiago
 *
 */
public class AutoParametersBindPhase extends AbstractParametersBindPhase 
{
	private static final String PARAM_SCREEN_PREFIX = "screen.";
	protected static final String PARAM_COMPONENT_PREFIX = "c(";

	private static final Log logger = LogFactory.getLog(AutoParametersBindPhase.class);

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
				if (parameter.startsWith(PARAM_SCREEN_PREFIX))
				{
					BeanUtils.copyProperty(screen, parameter.substring(7), context.getRequest().getParameter(parameter));
				}
				else if (parameter.startsWith(PARAM_COMPONENT_PREFIX))
				{
					int indexFechaPar = parameter.indexOf(")");
					if (indexFechaPar+2<parameter.length())
					{
						String componentId = parameter.substring(2, indexFechaPar);
						Component component = screen.getComponent(componentId);
						BeanUtils.copyProperty(component, parameter.substring(indexFechaPar+2), context.getRequest().getParameter(parameter));
					}
				}
				else
				{
					dispatchData.addParameter(parameter, FormatParameters.unformat(screen, parameter, context.getRequest().getParameter(parameter)));
				}
			} 
			catch (Throwable e) 
			{
				logger.error(messages.parametersBindPhaseErrorPopulatingBean(parameter, e.getMessage()), e);
			}
		}
		
		screen.setCheckChanges(true);
		context.setScreen(screen);
		context.setDispatchData(dispatchData);
	}
	
	protected boolean isControlParameter(String parName)
	{
		return (super.isControlParameter(parName) || SCREEN_ID.equals(parName));
	}
}
