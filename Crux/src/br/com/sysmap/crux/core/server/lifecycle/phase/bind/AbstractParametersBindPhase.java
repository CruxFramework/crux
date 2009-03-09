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
package br.com.sysmap.crux.core.server.lifecycle.phase.bind;

import javax.servlet.http.HttpServletRequest;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.lifecycle.Phase;
import br.com.sysmap.crux.core.server.lifecycle.PhaseException;
import br.com.sysmap.crux.core.server.screen.Screen;
import br.com.sysmap.crux.core.server.screen.ScreenStateManagerInitializer;

public abstract class AbstractParametersBindPhase implements Phase 
{
	protected static final String PARAM_EVT_CALL = "evtCall";
	protected static final String PARAM_ID_SENDER = "idSender";
	protected static final String SCREEN_ID = "screenId";
	
	protected ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	protected Screen getScreen(String screenName, HttpServletRequest request) throws PhaseException
	{
		Screen screen;
		try 
		{
			screen = ScreenStateManagerInitializer.getScreenStateManager().getScreen(screenName, request);
		} 
		catch (Exception e) 
		{
			throw new PhaseException(messages.parametersBindPhaseErrorCreatingScreen(e.getLocalizedMessage()), e);
		}
		if (screen == null)
		{
			throw new PhaseException(messages.parametersBindPhaseInvalidScreenRequested(screenName));
		}
		return screen;
	}
	
	protected boolean isControlParameter(String parName)
	{
		return (PARAM_EVT_CALL.equals(parName));
	}	
}
