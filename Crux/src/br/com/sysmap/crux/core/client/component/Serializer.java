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
class Serializer
{
	static class SerializationException extends Exception
	{
		private static final long serialVersionUID = -880487149212227770L;

		public SerializationException(String message)
		{
			super(message);
		}
	}
	
	static String serialize(Object param) throws SerializationException
	{
		Document document = XMLParser.createDocument();

		if (param != null)
		{
			Element root = document.createElement("<data>");
			document.appendChild(root);

			Element element = document.createElement("<type>");
			element.setNodeValue(param.getClass().getName());
			root.appendChild(element);

			serializeValue(document, root, param);
		}
		
		return document.toString();
	}

	private static void serializeValue(Document document, Element parent, Object param) throws SerializationException
	{
		Element element = document.createElement("<value>");
		if (param instanceof Object[])
		{
			Object[] paramItems = (Object[])param;
			for (Object p : paramItems)
			{
				Element item = document.createElement("<item>");
				serializeValue(document, item, p);
				element.appendChild(item);
			}
		}
		else
		{
			element.setNodeValue(getParamValue(param));
		}
		parent.appendChild(element);		
	}
	
	private static String getParamValue(Object param) throws SerializationException
	{
		if ((param instanceof CharSequence) || (param instanceof Number) || 
			(param instanceof Boolean) || (param instanceof Character) || (param instanceof Date))
		{
			return param.toString();
		}
		throw new SerializationException(""); // TODO: colocar mensagem
	}

	static Object deserialize(String serializedData) throws SerializationException
	{
		Document root = XMLParser.parse(serializedData);
		
		NodeList childNodes = root.getDocumentElement().getChildNodes();
		
		if (childNodes.getLength() > 0)
		{
			String type = ((Element) childNodes.item(0)).getNodeValue();
			Element value = ((Element) childNodes.item(1));
			if (type.startsWith("[["))
			{
				throw new SerializationException("NOT SUPPORTED");//TODO: colocar mensagem
			}
			else if (type.startsWith("["))
			{
				NodeList items = value.getChildNodes();
				int size = items.getLength();
				Object[] result = getParamObjectArray(type, size);
				
				for (int i=0; i < size; i++)
				{
					result[i] = deserialize(items.item(i).getNodeValue());
				}
				return result;
			}
			else
			{
				return getParamObject(value, type);
			}
		}
		
		return null;
	}

	@SuppressWarnings("deprecation")
	private static Object getParamObject(Element element, String type) throws SerializationException
	{
		if ("Ljava/lang/Integer;".equals(type))
		{
			return new Integer(element.getNodeValue());
		}
		else if ("Ljava/lang/Short;".equals(type))
		{
			return new Short(element.getNodeValue());
		}
		else if ("Ljava/lang/Long;".equals(type))
		{
			return new Long(element.getNodeValue());
		}
		else if ("Ljava/lang/Byte;".equals(type))
		{
			return new Byte(element.getNodeValue());
		}
		else if ("Ljava/lang/Float;".equals(type))
		{
			return new Float(element.getNodeValue());
		}
		else if ("Ljava/lang/Double;".equals(type))
		{
			return new Double(element.getNodeValue());
		}
		else if ("Ljava/lang/String;".equals(type))
		{
			return new String(element.getNodeValue());
		}
		else if ("Ljava/lang/Character;".equals(type))
		{
			return new Character(element.getNodeValue().charAt(0));
		}
		else if ("Ljava/util/Date;".equals(type))
		{
			return new Date(element.getNodeValue());
		}
		else if ("Ljava/sql/Date;".equals(type))
		{
			return new java.sql.Date(new Date(element.getNodeValue()).getTime());
		}
		else if ("Ljava/sql/Timestamp;".equals(type))
		{
			return new java.sql.Timestamp(new Date(element.getNodeValue()).getTime());
		}
		else if ("Ljava/lang/StringBuilder;".equals(type))
		{
			return new StringBuilder(element.getNodeValue());
		}
		else if ("Ljava/lang/StringBuffer;".equals(type))
		{
			return new StringBuffer(element.getNodeValue());
		}
		
		throw new SerializationException(""); // TODO:colocar mensagem;
	}

	private static Object[] getParamObjectArray(String type, int size) throws SerializationException
	{
		if ("Ljava/lang/Integer;".equals(type))
		{
			return new Integer[size];
		}
		else if ("Ljava/lang/Short;".equals(type))
		{
			return new Short[size];
		}
		else if ("Ljava/lang/Long;".equals(type))
		{
			return new Long[size];
		}
		else if ("Ljava/lang/Byte;".equals(type))
		{
			return new Byte[size];
		}
		else if ("Ljava/lang/Float;".equals(type))
		{
			return new Float[size];
		}
		else if ("Ljava/lang/Double;".equals(type))
		{
			return new Double[size];
		}
		else if ("Ljava/lang/String;".equals(type))
		{
			return new String[size];
		}
		else if ("Ljava/lang/Character;".equals(type))
		{
			return new Character[size];
		}
		else if ("Ljava/util/Date;".equals(type))
		{
			return new Date[size];
		}
		else if ("Ljava/sql/Date;".equals(type))
		{
			return new java.sql.Date[size];
		}
		else if ("Ljava/sql/Timestamp;".equals(type))
		{
			return new java.sql.Timestamp[size];
		}
		else if ("Ljava/lang/StringBuilder;".equals(type))
		{
			return new StringBuilder[size];
		}
		else if ("Ljava/lang/StringBuffer;".equals(type))
		{
			return new StringBuffer[size];
		}
		
		throw new SerializationException(""); // TODO:colocar mensagem;
	}
}

