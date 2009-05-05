package br.com.sysmap.crux.basic.client;

import br.com.sysmap.crux.core.client.component.Component;
import br.com.sysmap.crux.core.client.component.ComponentPanel;
import br.com.sysmap.crux.core.client.component.Container;

import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class HTMLContainer extends Container{

	public HTMLContainer(String id)
	{
		super(id, new HTMLPanel(DOM.getInnerHTML(DOM.getElementById(id))));
		Element element = DOM.getElementById(id);
		element.setInnerHTML("");
	}
	
	protected HTMLContainer(String id, Widget widget) 
	{
		super(id, widget);
	}

	@Override
	public void addComponent(Component component)
	{
		components.put(component.getId(), component);
		Element element = DOM.getElementById(component.getId());
		ComponentPanel panel = new ComponentPanel(element.getParentElement());
		panel.add(getComponentWidget(component));
	}

	@Override
	protected void addWidget(Widget widget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearWidgetChildren(Widget widget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void removeWidget(Widget widget) {
		// TODO Auto-generated method stub
		
	}
	
}
