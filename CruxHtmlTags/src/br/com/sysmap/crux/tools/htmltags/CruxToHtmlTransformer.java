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
package br.com.sysmap.crux.tools.htmltags;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.TransformerFactoryImpl;
import nu.xom.Builder;
import nu.xom.Serializer;
import nu.xom.xinclude.XIncluder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.core.utils.FileSystemUtils;
import br.com.sysmap.crux.tools.htmltags.util.StreamUtils;

/**
 * Generates HTML output based on Crux widget tags
 * @author Gessé Dafé <code>gessedafe@gmail.com</code>
 */
public class CruxToHtmlTransformer
{
	// Makes it easier to read the output files
	private static boolean forceIndent = false;
	
	// Makes it easier to read the output files
	private static String outputCharset = "ISO-8859-1";

	private static final Log log = LogFactory.getLog(CruxToHtmlTransformer.class);
	private static HTMLTagsMessages messages = (HTMLTagsMessages)MessagesFactory.getMessages(HTMLTagsMessages.class);
	private static Transformer transformer = null;
	private static final Lock lock = new ReentrantLock();

	/**
	 * Initializes the static resources
	 */
	private static void init()
	{
		if (transformer == null)
		{
			lock.lock();

			if (transformer == null)
			{
				try
				{
					TransformerFactory tfactory = new TransformerFactoryImpl();
					File tmpDir = new File(FileSystemUtils.getTempDirFile(), "crux_xslt");
					if (!tmpDir.exists())
					{
						tmpDir.mkdir();
					}
					InputStream is = generateHtmlTagsXSLT(tmpDir);
					transformer = tfactory.newTransformer(new StreamSource(is));
					
					File[] tmpXsltFiles = tmpDir.listFiles();
					for (File file : tmpXsltFiles)
					{
						file.delete();
					}
					tmpDir.delete();
				}
				catch (Throwable e)
				{
					log.error(messages.transformerErrorCreatingTransformer(e.getMessage()), e);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}

	/**
	 * Executes the transformation
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws InterfaceConfigException 
	 */
	public static void generateHTML(String filePath, OutputStream out) throws InterfaceConfigException
	{
		init();
		
		try
		{
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
			String source = resolveXInclude(filePath);
			transformer.transform(new StreamSource(new ByteArrayInputStream(source.getBytes("UTF-8"))), new StreamResult(buff));
			String result = new String(buff.toByteArray());
			result = handleHtmlDocument(source, result);
			StreamUtils.write(new ByteArrayInputStream(result.getBytes()), out, false);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * If source contains DOCTYPE declaration, inserts it in the result.
	 * Removes the XMLNS declaration from the root tag
	 * @param result
	 * @param result2 
	 */
	private static String handleHtmlDocument(String source, String result)
	{
		int doctypeBegin = source.indexOf("!DOCTYPE");

		if(doctypeBegin >= 0)
		{
			int lineEnd = source.indexOf("\n", doctypeBegin);
			String doctype = source.substring(doctypeBegin, lineEnd);
			result = "<" + doctype + ">\n" + result;
		}
		
		int htmlTagBegin = result.toUpperCase().indexOf("<HTML");

		if(htmlTagBegin >= 0)
		{
			int htmlTagEnd = result.indexOf(">", htmlTagBegin);
			result = result.substring(0, htmlTagBegin) + "<html>" + result.substring(htmlTagEnd + 1);
		}
		
		return result;
	}

	/**
	 * Generate the XSLT file based on template crux-ui.template.xslt, importing all files with extension .crux.xslt.
	 * @return
	 */
	private static InputStream generateHtmlTagsXSLT(File parentDir)
	{
		InputStream templateIs = CruxToHtmlTransformer.class.getResourceAsStream("/META-INF/crux-ui.template.xslt");
		if (templateIs == null)
		{
			throw new HTMLTagsTransformerException(messages.transformerTemplateNotFound());
		}
		try
		{
			String template = StreamUtils.readAsUTF8(templateIs);
			String imports = generateImportList(parentDir);
			template = template.replace("${imports}", imports);
			template = template.replace("${indent}", mustIndent() ? "yes" : "no");
			template = template.replace("${charset}", outputCharset);
			
			if (log.isDebugEnabled())
			{
				log.debug("Generated XSLT:\n" +template);
			}

			return new ByteArrayInputStream(template.getBytes("UTF-8"));
		}
		catch (IOException e)
		{
			throw new HTMLTagsTransformerException(messages.transformerErrorReadingTemplate(e.getMessage()),e);
		}
	}

	/**
	 * @return
	 */
	private static boolean mustIndent()
	{
		return !Environment.isProduction() || forceIndent;
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	private static String generateImportList(File parentDir) throws IOException
	{
		StringBuilder importList = new StringBuilder();
		Iterator<String> iterator = WidgetTagConfig.iterateStyleSheets();

		int xsltCount = 0;
		
		while (iterator.hasNext())
		{
			InputStream streamXslt = new ByteArrayInputStream(iterator.next().getBytes());
			File xsltFile = new File(parentDir, "cruxHtmlTag"+(xsltCount++)+".xslt");
			FileOutputStream out = new FileOutputStream(xsltFile);
			StreamUtils.write(streamXslt, out, false);
			out.close();
			String filePath = xsltFile.getCanonicalPath().replaceAll("\\\\", "/");
			if (!filePath.startsWith("/"))
			{
				filePath = "/"+filePath;
			}
			filePath = "file://"+filePath;

			importList.append("<xsl:import href=\"" + filePath + "\"/>");
		}
		
		return importList.toString();
	}

	/**
	 * Executes the X-Include processing
	 * @param fileName
	 * @return
	 * @throws InterfaceConfigException
	 */
	private static String resolveXInclude(String filePath) throws InterfaceConfigException
	{
		Builder builder = new Builder();

		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Serializer outputter = new Serializer(out, "ISO-8859-1");
			nu.xom.Document input = builder.build(new File(filePath));
			XIncluder.resolveInPlace(input);
			outputter.write(input);
			return new String(out.toByteArray());
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException(e.getMessage(), e);
		}
	}

	/**
	 * Makes it easier to read the output files
	 * @param force
	 */
	public static void setForceIndent(boolean force)
	{
		forceIndent = force;
	}

	/**
	 * @param outputCharset
	 */
	public static void setOutputCharset(String charset)
	{
		outputCharset = charset;
	}	
}