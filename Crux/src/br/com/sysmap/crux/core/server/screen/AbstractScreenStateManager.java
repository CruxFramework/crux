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
package br.com.sysmap.crux.core.server.screen;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.ServerMessages;

public abstract class AbstractScreenStateManager implements ScreenStateManager
{
	protected ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	
	public Screen getScreen(String id) throws ScreenConfigException
	{
		try
		{
			return (Screen)ScreenFactory.getInstance().getScreen(id).clone();
		}
		catch (CloneNotSupportedException e) 
		{
			throw new ScreenConfigException(messages.screenStateManagerErrorCloningScreen(id, e.getLocalizedMessage()), e);
		}
	}
}
