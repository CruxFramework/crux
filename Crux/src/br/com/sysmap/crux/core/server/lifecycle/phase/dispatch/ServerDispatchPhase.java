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
 * Dispatcher for handle crux server events.
 * @author Thiago
 *
 */
public class ServerDispatchPhase extends AbstractDispatchPhase
{
	private static final Log logger = LogFactory.getLog(ServerDispatchPhase.class);
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
