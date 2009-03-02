package br.com.sysmap.crux.core.client.component;

import br.com.sysmap.crux.core.client.JSEngine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

/**
 * Helper class to serialize screen data in communications between client and server.
 * @author Thiago
 *
 */
public class ScreenSerialization 
{
	/**
	 * Serialize the screen, to be posted to server
	 * @return
	 */
	public String getPostData(Screen screen)
	{
		StringBuilder builder = new StringBuilder();
		
		boolean notFirst = screen.serialize(builder);
		if (notFirst)
			builder.append("&");
		buildBeansPostData(screen, builder);
		return builder.toString();
	}

	/**
	 * Serialize the screen, sending only data. This method does not send component 
	 * tree structure information.
	 * @return
	 */
	public String getDTOPostData(Screen screen)
	{
		StringBuilder builder = new StringBuilder();
		buildBeansPostData(screen, builder);
		return builder.toString();
	}
	
	/**
	 * Construct post data for server DTO bound to components.
	 * @param screen
	 * @param builder
	 * @return true if any DTO was serialized.
	 */
	protected boolean buildBeansPostData(Screen screen, StringBuilder builder)
	{
		boolean first = true;
		for (String beanProperty : screen.beansProperties.keySet()) 
		{
			if (!first)
				builder.append("&");
			first = false;
			buildPostParameter(builder, beanProperty, screen.getSerializedBeanProperty(beanProperty));
		}
		return !first;
	}
	
	/**
	 * Construct a parameter block to send with a request to server 
	 * 
	 * @param builder
	 * @param parName
	 * @param parValue
	 */
	public static void buildPostParameter(StringBuilder builder, String parName, String parValue)
	{
		if(parName != null && parName.length() > 0) 
		{
			if (parValue != null && parValue.length() > 0)
			{
				builder.append(parName+"="+URL.encodeComponent(parValue));
			}
			else
			{
				builder.append(parName+"=");
			}
		}
	}
	
	/**
	 * Update screen with information sent by server.
	 * @param screen
	 * @param responseText
	 */
	public void updateScreen(Screen screen, String responseText)
	{
		try
		{
			Document document = XMLParser.parse(responseText);
			NodeList children = document.getDocumentElement().getChildNodes();
			
			for (int i = 0; i < children.getLength(); i++)
			{
				if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					Element element = (Element)children.item(i);
					String componentId = element.getAttribute("id");
					if ("_dtos_".equals(componentId))
					{
						updateDTOs(screen, element);
					}
					else if ("_components_".equals(componentId))
					{
						updateComponents(screen, element);
					}
					else if ("_screen_".equals(componentId))
					{
						screen.update(element);
					}
					else if ("_interrupt_error_".equals(componentId))
					{
						Window.alert(element.getAttribute("_value"));
						break;
					}
					else if ("_server_error_".equals(componentId))
					{
						Window.alert(JSEngine.messages.screenSerializationServerError(element.getAttribute("_value")));
						break;
					}
				}
			}
		}
		catch (DOMParseException e) 
		{
			GWT.log(e.getLocalizedMessage(), e);
			Window.alert(JSEngine.messages.eventProcessorServerAutoResponseParserError());
		}
	}
	
	protected void updateDTOs(Screen screen, Element dtosElement)
	{
		NodeList children = dtosElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element)children.item(i);
				String key = element.getAttribute("id");
				String value = element.getAttribute("value");
				screen.setBeanProperty(key, value);
			}			
		}
	}
	
	protected void updateComponents(Screen screen, Element componentsElement)
	{
		NodeList children = componentsElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element)children.item(i);
				String componentId = element.getAttribute("id");
				Component component = screen.getComponent(componentId);
				if (component == null)
				{
					Window.alert(JSEngine.messages.screenSerializationInvalidComponent(componentId));
				}
				else
				{
					component.update(element);
				}
			}
		}
	}

	public void confirmSerialization(Screen screen) 
	{
		screen.confirmSerialization();
	}
}
