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
package org.cruxframework.crux.core.rebind.context.scanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.JClassScanner;
import org.cruxframework.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.ext.typeinfo.JClassType;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
@Legacy
public class FormatterScanner 
{
	private Map<String, String> formatters;
	private boolean initialized = false;
	private JClassScanner jClassScanner;

	public FormatterScanner(JClassScanner jClassScanner)
    {
		this.jClassScanner = jClassScanner;
    }

	/**
	 * 
	 * @param name
	 * @return
	 */
	public String getFormatter(String name)
	{
		initializeFormatters();
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
	 * @param formatter
	 * @return
	 */
	public String getFormatterInstantionCommand(String formatter)
	{
		initializeFormatters();
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

				String formatterClass = getFormatter(formatterName);
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
	
	/**
	 * @return
	 */
	public Iterator<String> iterateFormatters()
	{
		initializeFormatters();
		return formatters.keySet().iterator();
	}
	
	/**
	 * 
	 */
	protected void initializeFormatters()
	{
		if (!initialized)
		{
			try 
			{
				formatters = new HashMap<String, String>();
				JClassType[] formatterTypes =  jClassScanner.searchClassesByInterface(Formatter.class.getCanonicalName());
				if (formatterTypes != null)
				{
					for (JClassType formatterClass : formatterTypes) 
					{
						FormatterName annot = formatterClass.getAnnotation(FormatterName.class);
						if (annot != null)
						{
							if (formatters.containsKey(annot.value()))
							{
								throw new CruxGeneratorException("Duplicated alias for formatter: ["+annot.value()+"].");
							}
							formatters.put(annot.value(), formatterClass.getQualifiedSourceName());
						}
						else
						{
							String simpleName = formatterClass.getSimpleSourceName();
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
								throw new CruxGeneratorException("Duplicated alias for formatter: ["+simpleName+"].");
							}
							formatters.put(simpleName, formatterClass.getQualifiedSourceName());
						}
					}
				}
				initialized = true;
			} 
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error initializing formatters.",e);
			}
		}
	}
}
