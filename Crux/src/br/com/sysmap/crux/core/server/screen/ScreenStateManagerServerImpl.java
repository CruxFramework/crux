package br.com.sysmap.crux.core.server.screen;

import javax.servlet.http.HttpServletRequest;

public class ScreenStateManagerServerImpl extends AbstractScreenStateManager implements ScreenStateManager 
{
	private static final String SCREEN_KEY = "_SCREEN_KEY_";
	
	@Override
	public Screen getScreen(String screenName, HttpServletRequest request) throws ScreenConfigException {
		
		Screen result = (Screen)request.getSession().getAttribute(SCREEN_KEY);
		if (result == null)
		{
			result = getScreen(screenName);
			request.getSession().setAttribute(SCREEN_KEY, result);
		}
		
		return result;
	}

	@Override
	public boolean clientMustPreserveState() 
	{
		return false;
	}
}
