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
package org.cruxframework.crux.core.rebind.context.scanner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.converter.TypeConverter.Converter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.JClassScanner;

import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Maps all converters.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ConverterScanner 
{
	private Map<String, String> converters;
	private boolean initialized = false;
	private JClassScanner jClassScanner;

	public ConverterScanner(JClassScanner jClassScanner)
	{
		this.jClassScanner = jClassScanner;
	}

	/**
	 * @param name
	 * @return
	 */
	public String getConverter(String name)
	{
		initializeConverters();
		return converters.get(name);
	}

	/**
	 * @return
	 */
	public Iterator<String> iterateConverters()
	{
		initializeConverters();
		return converters.keySet().iterator();
	}

	/**
	 * 
	 */
	protected void initializeConverters()
	{
		if (!initialized)
		{
			converters = new HashMap<String, String>();
			JClassType[] converterTypes;
			try 
			{
				converterTypes =  jClassScanner.searchClassesByAnnotation(Converter.class);
			} 
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error initializing Converters scanner.",e);
			}
			if (converterTypes != null)
			{
				for (JClassType converterClass : converterTypes) 
				{
					Converter annot = converterClass.getAnnotation(Converter.class);
					if (converters.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated alias for Converter found: ["+annot.value()+"].");
					}

					converters.put(annot.value(), converterClass.getQualifiedSourceName());
				}
			}
			initialized = true;
		}
	}
}
