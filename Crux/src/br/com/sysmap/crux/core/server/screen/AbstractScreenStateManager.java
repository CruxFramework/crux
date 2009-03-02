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
