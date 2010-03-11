/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.client.screen;

import java.util.Date;

import br.com.sysmap.crux.core.client.Crux;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Helper class used to pass parameters between different crux windows.
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class ModuleComunicationSerializer
{	
	private RegisteredCruxSerializables registeredSerializables;

	/**
	 * Constructor
	 */
	public ModuleComunicationSerializer()
	{
		this.registeredSerializables = GWT.create(RegisteredCruxSerializables.class);
	}
	
	public void registerCruxSerializable(String type, CruxSerializable serializable)
	{
		this.registeredSerializables.registerCruxSerializable(type, serializable);
	}
	
	/**
	 * Serialize parameter
	 * @param param
	 * @return
	 * @throws ModuleComunicationException
	 */
	public String serialize(Object param) throws ModuleComunicationException
	{
		if (param != null)
		{
			Document document = XMLParser.createDocument();

			Element root = document.createElement("data");
			document.appendChild(root);
			serializeValue(document, root, param);
			return document.toString();
		}
		
		return null;
	}

	/**
	 * Deserialize parameter
	 * @param serializedData
	 * @return
	 * @throws ModuleComunicationException
	 */
	public Object deserialize(String serializedData) throws ModuleComunicationException
	{
		if (serializedData != null && serializedData.length() > 0)
		{
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			return deserialize(data);
		}
		return null;
	}
	
	/**
	 * 
	 * @param document
	 * @param parent
	 * @param param
	 * @throws ModuleComunicationException
	 */
	private void serializeValue(Document document, Element parent, Object param) throws ModuleComunicationException
	{
		Element value = document.createElement("value");
		if (param != null)
		{
			value.setAttribute("t", param.getClass().getName());
			if (param instanceof Object[])
			{
				Object[] paramItems = (Object[])param;
				for (Object p : paramItems)
				{
					Element item = document.createElement("i");
					serializeValue(document, item, p);
					value.appendChild(item);
				}
			}
			else 
			{
				value.setAttribute("v", getParamValue(param));
			}
		}
		parent.appendChild(value);		
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws ModuleComunicationException
	 */
	private String getParamValue(Object param) throws ModuleComunicationException
	{
		if (param == null)
		{
			return null;
		}
		if ((param instanceof CharSequence) || (param instanceof Number) || 
			(param instanceof Boolean) || (param instanceof Character))
		{
			return param.toString();
		}
		else if(param instanceof Date)
		{
			return Long.toString(((Date)param).getTime());
		}
		else if (param instanceof CruxSerializable)
		{
			return ((CruxSerializable)param).serialize();
		}
		throw new ModuleComunicationException(Crux.getMessages().moduleComunicationInvalidParamType(param.getClass().getName()));
	}

	/**
	 * 
	 * @param data
	 * @param dataType
	 * @return
	 * @throws ModuleComunicationException
	 */
	private Object deserialize(Element data) throws ModuleComunicationException
	{
		Element value = (Element) data.getFirstChild();
		if (value != null)
		{
			String dataType = value.getAttribute("t");
			if (dataType != null)
			{
				if (dataType.startsWith("[["))
				{
					throw new ModuleComunicationException(Crux.getMessages().moduleComunicationInvalidParamType(dataType));
				}
				else 
				{
					if (dataType.startsWith("["))
					{
						dataType = dataType.substring(2, dataType.length()-1);
					}
					NodeList items = value.getChildNodes();
					int size = items.getLength();
					if (size > 0)
					{
						Object[] result = getParamObjectArray(dataType, size);
						for (int i=0; i < size; i++)
						{
							result[i] = deserialize((Element) items.item(i));
						}
						return result;
					}
					else
					{
						return getParamObject(value.getAttribute("v"), dataType);
					}
				}
			}	
		}
		return null;
	}

	/**
	 * 
	 * @param value
	 * @param type
	 * @return
	 * @throws ModuleComunicationException
	 */
	private Object getParamObject(String value, String type) throws ModuleComunicationException
	{
		CruxSerializable serializer = registeredSerializables.getCruxSerializable(type);
		if (serializer != null)
		{
			return serializer.deserialize(value);
		}
		throw new ModuleComunicationException(Crux.getMessages().moduleComunicationInvalidParamType(type));
	}

	/**
	 * 
	 * @param type
	 * @param size
	 * @return
	 * @throws ModuleComunicationException
	 */
	private Object[] getParamObjectArray(String type, int size) throws ModuleComunicationException
	{
		CruxSerializable serializer = registeredSerializables.getCruxSerializable(type);
		if (serializer != null)
		{
			return serializer.newArray(size);
		}
		return new Object[size];
	}
}