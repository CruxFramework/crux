package br.com.sysmap.crux.core.server.screen;

import javax.servlet.http.HttpServletRequest;

public class ScreenStateManagerClientImpl extends AbstractScreenStateManager implements ScreenStateManager
{
	@Override
	public Screen getScreen(String screenName, HttpServletRequest request) throws ScreenConfigException 
	{
		return getScreen(screenName);
	}

	@Override
	public boolean clientMustPreserveState() 
	{
		return true;
	}
}
