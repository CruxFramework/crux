package br.com.sysmap.crux.core.client.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * Base class for create new containers.
 * @author Thiago
 *
 */
public class Container extends Component 
{
	protected Map<String, Component> components = new HashMap<String, Component>(30);

	public Container(String id, Widget widget) 
	{
		super(id, widget);
	}

	public void addComponent(Component component)
	{
		components.put(component.getId(), component);
		if (widget instanceof Panel)
			((Panel)widget).add(component.widget);
	}
	
	public Component getComponent (String id)
	{
		return components.get(id);
	}
	
	public void update(Element element) 
	{
		super.update(element);
		
		NodeList children = element.getChildNodes();
		
		for (int i = 0; i < children.getLength(); i++)
		{
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element compElement = (Element)children.item(i);
				String componentId = compElement.getAttribute("id");
				Component component = getComponent(componentId);
				if (component == null)
				{
					// TODO: Report error.
					continue;
				}
				component.update(element);
			}
		}
	}

	public Iterator<Component> iterateComponents() 
	{
		return components.values().iterator();
	}
	
	/**
	 * Provide access to component's widget. Used for container subclasses that
	 * need to access widgets of their children. 
	 * @param component
	 * @return
	 */
	protected Widget getComponentWidget(Component component)
	{
		return (component.widget!=null?component.widget:null);
	}
}
