package br.com.sysmap.crux.core.client.component;


import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

public class ComponentPanel extends SimplePanel
{
	public ComponentPanel(Element element)
	{
		super(element);
		onAttach();
	}
}
