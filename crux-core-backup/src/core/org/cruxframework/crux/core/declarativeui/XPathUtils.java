/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.declarativeui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class XPathUtils
{
	/**
	 * @return
	 */
	public static XPath getCruxPagesXPath()
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath findPath = factory.newXPath();
		findPath.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				if (prefix.equals("c"))
				{
					return "http://www.cruxframework.org/crux";
				}
				else if (prefix.equals("v"))
				{
					return "http://www.cruxframework.org/view";
				}
				
				return "";
			}

			public String getPrefix(String namespaceURI)
			{
				if (namespaceURI.equals("http://www.cruxframework.org/crux"))
				{
					return "c";
				}
				else if (namespaceURI.equals("http://www.cruxframework.org/view"))
				{
					return "v";
				}
				return "";
			}

			public Iterator<?> getPrefixes(String namespaceURI)
			{
				List<String> prefixes = new ArrayList<String>();
				prefixes.add("c");
				prefixes.add("v");

				return prefixes.iterator();
			}
		});

		return findPath;
	}
	
	/**
	 * @return
	 */
	public static XPath getHtmlXPath()
	{
		XPathFactory factory = XPathFactory.newInstance();
		XPath htmlPath = factory.newXPath();
		htmlPath.setNamespaceContext(new NamespaceContext()
		{
			public String getNamespaceURI(String prefix)
			{
				return "http://www.w3.org/1999/xhtml";
			}

			public String getPrefix(String namespaceURI)
			{
				return "h";
			}

			public Iterator<?> getPrefixes(String namespaceURI)
			{
				List<String> prefixes = new ArrayList<String>();
				prefixes.add("h");

				return prefixes.iterator();
			}
		});
		
		return htmlPath;
	}
	
}
