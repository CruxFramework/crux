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
package org.cruxframework.crux.core.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Toolkit for XML manipulations.
 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
 */
public class XMLUtils
{
	private static DocumentBuilder nsUnawareDocumentBuilder;
	private static final Log log = LogFactory.getLog(XMLUtils.class);
		
	static 
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		builderFactory.setIgnoringComments(true);
		
		try
		{
			nsUnawareDocumentBuilder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * Creates a {@link Document} from the given stream. 
	 * @param stream
	 * @return
	 * @throws XMLException
	 */
	public static Document createNSUnawareDocument(InputStream stream) throws XMLException
	{
		try
		{
			InputStreamReader reader = new InputStreamReader(stream, Charset.defaultCharset());
			return nsUnawareDocumentBuilder.parse(new InputSource(reader));
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new XMLException(e.getMessage(), e);
		}
	}
	
	/**
	 * Creates a {@link Document}. 
	 * @return
	 * @throws XMLException
	 */
	public static Document createNSUnawareDocument() 
	{
		return nsUnawareDocumentBuilder.newDocument();
	}

	/**
	 * A XML manipulation error.
	 * @author Gesse S. F. Dafe - <code>gessedafe@gmail.com</code>
	 */
	@SuppressWarnings("serial")
	public static class XMLException extends Exception
	{
		public XMLException(String message, Throwable cause)
		{
			super(message, cause);
		}		
	}
}