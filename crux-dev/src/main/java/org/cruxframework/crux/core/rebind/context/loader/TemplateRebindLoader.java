/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.context.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cruxframework.crux.core.declarativeui.template.TemplateException;
import org.cruxframework.crux.core.declarativeui.template.TemplateLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.google.gwt.core.ext.GeneratorContext;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class TemplateRebindLoader implements TemplateLoader
{
	private Map<String, Document> cache = new HashMap<String, Document>();
	private GeneratorContext context;
	private DocumentBuilder documentBuilder;
	private boolean initialized = false;
	
	public TemplateRebindLoader(GeneratorContext context)
	{
		this.context = context;
	}

	private void initialize()
    {
		if (!initialized)
		{
			try
			{
				DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilderFactory.setNamespaceAware(true);
				this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e)
			{
				throw new TemplateException("Error creating XML Parser.", e);
			}
			initialized = true;
		}
    }

	@Override
	public Document getTemplate(String library, String id)
	{
		initialize();
		String cacheKey = library + "_" + id;
		if (cache.containsKey(cacheKey))
		{
			return (Document) cache.get(cacheKey).cloneNode(true);
		}
		
		Set<String> pathNames = context.getResourcesOracle().getPathNames();

		for (String pathName : pathNames)
		{
			int index = pathName.lastIndexOf('/');
			String fileName;
			if (index > 0)
			{
				fileName = pathName.substring(index+1);
			}
			else
			{
				fileName = pathName;
			}

			if (fileName.equals(id+".template.xml"))
			{
				InputStream stream = context.getResourcesOracle().getResourceAsStream(pathName);
				try
                {
	                Document template = documentBuilder.parse(stream);
	                stream.close();
	        		Element templateElement = template.getDocumentElement();
	        		String templateLibrary = templateElement.getAttribute("library");
	        		String templateCacheKey = templateLibrary + "_" + id;
	                if (templateLibrary.equals(library))
	                {
	                	cache.put(cacheKey, template);
	                	return (Document) template.cloneNode(true);
	                }
	                else if (!cache.containsKey(templateCacheKey))
	                {
						cache.put(templateCacheKey, template);
	                }
                }
                catch (SAXException | IOException e)
                {
	                throw new TemplateException("Error parsing template file: ["+pathName+"]", e);
                }
			}
		}

		return null;
	}
}
