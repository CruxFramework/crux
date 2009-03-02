package br.com.sysmap.crux.core.server.screen;

import javax.servlet.http.HttpServletRequest;

public interface ScreenStateManager 
{
	Screen getScreen(String screenName, HttpServletRequest request) throws ScreenConfigException;
	boolean clientMustPreserveState();
}
