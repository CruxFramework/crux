package br.com.sysmap.crux.showcase.client.gxt;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;

public class GxtExample extends LayoutContainer {  

	public GxtExample()
	{
		Button button = new Button("GXT Button on Crux");
		add(button);
	} 
}  