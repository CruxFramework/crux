package br.com.sysmap.crux.showcase.client.controller;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.screen.Screen;

import com.google.gwt.user.client.ui.Label;

@Controller("frameController")
public class FrameController implements FrameControllerCrossDoc
{
	public void setMyLabel(String text)
	{
		Screen.get("label", Label.class).setText(text);
	}
}
