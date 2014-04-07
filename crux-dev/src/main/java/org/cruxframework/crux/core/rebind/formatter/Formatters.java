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
package org.cruxframework.crux.core.rebind.formatter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.cruxframework.crux.scanner.ClassScanner;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Formatters 
{
	private static final Log logger = LogFactory.getLog(Formatters.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> formatters;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (formatters != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (formatters != null)
			{
				return;
			}
			
			initializeFormatters();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static void initializeFormatters()
	{
		formatters = new HashMap<String, String>();
		Set<String> formatterNames =  ClassScanner.searchClassesByInterface(Formatter.class);
		if (formatterNames != null)
		{
			for (String formatter : formatterNames) 
			{
				try 
				{
					Class<? extends Formatter> formatterClass = (Class<? extends Formatter>) Class.forName(formatter);
					FormatterName annot = formatterClass.getAnnotation(FormatterName.class);
					if (annot != null)
					{
						if (formatters.containsKey(annot.value()))
						{
							throw new CruxGeneratorException("Duplicated formatter: ["+annot.value()+"].");
						}
						formatters.put(annot.value(), formatterClass.getCanonicalName());
					}
					else
					{
						String simpleName = formatterClass.getSimpleName();
						if (simpleName.length() >1)
						{
							simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
						}
						else
						{
							simpleName = simpleName.toLowerCase();
						}
						if (formatters.containsKey(simpleName))
						{
							throw new CruxGeneratorException("Duplicated formatter: ["+simpleName+"].");
						}
						formatters.put(simpleName, formatterClass.getCanonicalName());
					}
				} 
				catch (Throwable e) 
				{
					logger.error("Error initializing formatters.",e);
				}
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getFormatter(String name)
	{
		if (formatters == null)
		{
			initialize();
		}
		
		if (name == null)
		{
			return null;
		}
		
		int index = name.indexOf("(");
		if (index > 0)
		{
			name = name.substring(0,index);
		}
		return formatters.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateFormatters()
	{
		if (formatters == null)
		{
			initialize();
		}

		return formatters.keySet().iterator();
	}
	
	/**
	 * @param formatter
	 * @return
	 */
	public static String getFormatterInstantionCommand(String formatter)
	{
		if (StringUtils.isEmpty(formatter))
		{
			return "null";
		}
		else
		{
			try
			{
				String formatterParams = null;
				String formatterName = formatter;
				StringBuilder parameters = new StringBuilder();
				int index = formatter.indexOf("(");
				if (index > 0)
				{
					formatterParams = formatter.substring(index+1,formatter.indexOf(")"));
					formatterName = formatter.substring(0,index).trim();
					String[] params = RegexpPatterns.REGEXP_COMMA.split(formatterParams);
					parameters.append("new String[]{");
					for (int i=0; i < params.length; i++) 
					{
						if (i>0)
						{
							parameters.append(",");
						}
						parameters.append(EscapeUtils.quote(params[i]).trim());
					}
					parameters.append("}");
				}

				String formatterClass = Formatters.getFormatter(formatterName);
				if (formatterClass == null || formatterClass.length() == 0)
				{
					throw new CruxGeneratorException("Formatter ["+formatterName+"] not found!");
				}
				return "new " + formatterClass + "("+parameters.toString()+")";
			}
			catch (Exception e)
			{
				throw new CruxGeneratorException(e.getMessage(), e);
			}
		}
	}
}
