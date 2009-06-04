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
package br.com.sysmap.crux.core.client.component;

import java.util.Date;

import br.com.sysmap.crux.core.client.JSEngine;

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
class ModuleComunicationSerializer
{	
	/**
	 * Serialize parameter
	 * @param param
	 * @return
	 * @throws ModuleComunicationException
	 */
	
	static String serialize(Object param) throws ModuleComunicationException
	{
		Document document = XMLParser.createDocument();

		if (param != null)
		{
			Element root = document.createElement("data");
			document.appendChild(root);

			root.setAttribute("type", param.getClass().getName());
			serializeValue(document, root, param);
		}
		
		return document.toString();
	}

	/**
	 * Deserialize parameter
	 * @param serializedData
	 * @return
	 * @throws ModuleComunicationException
	 */
	static Object deserialize(String serializedData) throws ModuleComunicationException
	{
		if (serializedData != null && serializedData.length() > 0)
		{
			Document root = XMLParser.parse(serializedData);
			Element data = root.getDocumentElement();
			String type = data.getAttribute("type");
			return deserialize(data, type);
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
	private static void serializeValue(Document document, Element parent, Object param) throws ModuleComunicationException
	{
		Element value = document.createElement("value");
		if (param instanceof Object[])
		{
			Object[] paramItems = (Object[])param;
			for (Object p : paramItems)
			{
				Element item = document.createElement("item");
				serializeValue(document, item, p);
				value.appendChild(item);
			}
		}
		else
		{
			value.setAttribute("simpleValue", getParamValue(param));
		}
		parent.appendChild(value);		
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws ModuleComunicationException
	 */
	private static String getParamValue(Object param) throws ModuleComunicationException
	{
		if ((param instanceof CharSequence) || (param instanceof Number) || 
			(param instanceof Boolean) || (param instanceof Character))
		{
			return param.toString();
		}
		else if(param instanceof Date)
		{
			return Long.toString(((Date)param).getTime());
		}
		else if (param instanceof ModuleShareable)
		{
			return ((ModuleShareable)param).serialize();
		}
		throw new ModuleComunicationException(JSEngine.messages.moduleComunicationInvalidParamType(param.getClass().getName()));
	}

	/**
	 * 
	 * @param data
	 * @param dataType
	 * @return
	 * @throws ModuleComunicationException
	 */
	private static Object deserialize(Element data, String dataType) throws ModuleComunicationException
	{
		Element value = (Element) data.getFirstChild();
		if (value != null)
		if (dataType.startsWith("[["))
		{
			throw new ModuleComunicationException(JSEngine.messages.moduleComunicationInvalidParamType(dataType));
		}
		else if (dataType.startsWith("["))
		{
			NodeList items = value.getChildNodes();
			int size = items.getLength();
			dataType = dataType.substring(2, dataType.length()-1);
			Object[] result = getParamObjectArray(dataType, size);
			for (int i=0; i < size; i++)
			{
				result[i] = deserialize((Element) items.item(i), dataType);
			}
			return result;
		}
		else
		{
			return getParamObject(value.getAttribute("simpleValue"), dataType);
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
	@SuppressWarnings("deprecation")
	private static Object getParamObject(String value, String type) throws ModuleComunicationException
	{
		if ("java.lang.Integer".equals(type))
		{
			return new Integer(value);
		}
		else if ("java.lang.String".equals(type))
		{
			return new String(value);
		}
		else if ("java.lang.Float".equals(type))
		{
			return new Float(value);
		}
		else if ("java.lang.Double".equals(type))
		{
			return new Double(value);
		}
		else if ("java.util.Date".equals(type))
		{
			return new Date(Long.parseLong(value));
		}
		else if ("java.lang.Long".equals(type))
		{
			return new Long(value);
		}
		else if ("java.lang.Short".equals(type))
		{
			return new Short(value);
		}
		else if ("java.lang.Byte".equals(type))
		{
			return new Byte(value);
		}
		else if ("java.lang.Character".equals(type))
		{
			return new Character(value.charAt(0));
		}
		else if ("java.sql.Date".equals(type))
		{
			return new java.sql.Date(Long.parseLong(value));
		}
		else if ("java.sql.Timestamp".equals(type))
		{
			return new java.sql.Timestamp(Long.parseLong(value));
		}
		else if ("java.lang.StringBuilder".equals(type))
		{
			return new StringBuilder(value);
		}
		else if ("java.lang.StringBuffer".equals(type))
		{
			return new StringBuffer(value);
		}
		else 
		{
			//return registeredSerializers.get(type).deserialize(value);
		}
		
		throw new ModuleComunicationException(JSEngine.messages.moduleComunicationInvalidParamType(type));
	}

	private static Object[] getParamObjectArray(String type, int size) throws ModuleComunicationException
	{
		if ("java.lang.Integer".equals(type))
		{
			return new Integer[size];
		}
		else if ("java.lang.String".equals(type))
		{
			return new String[size];
		}
		else if ("java.lang.Float".equals(type))
		{
			return new Float[size];
		}
		else if ("java.lang.Double".equals(type))
		{
			return new Double[size];
		}
		else if ("java.util.Date".equals(type))
		{
			return new Date[size];
		}
		else if ("java.lang.Long".equals(type))
		{
			return new Long[size];
		}
		else if ("java.lang.Short".equals(type))
		{
			return new Short[size];
		}
		else if ("java.lang.Byte".equals(type))
		{
			return new Byte[size];
		}
		else if ("java.lang.Character".equals(type))
		{
			return new Character[size];
		}
		else if ("java.sql.Date".equals(type))
		{
			return new java.sql.Date[size];
		}
		else if ("java.sql.Timestamp".equals(type))
		{
			return new java.sql.Timestamp[size];
		}
		else if ("java.lang.StringBuilder".equals(type))
		{
			return new StringBuilder[size];
		}
		else if ("java.lang.StringBuffer".equals(type))
		{
			return new StringBuffer[size];
		}
		else
		{
			return new Object[size];
		}		
	}
}

