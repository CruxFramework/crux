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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.dto.DataObjectIdentifier;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.JClassScanner;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;


/**
 * Maps all data objects.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataObjectScanner 
{
	private Map<String, String[]> dataObjectIdentifiers;
	private Map<String, String> dataObjects;
	private boolean initialized = false;
	private JClassScanner jClassScanner;

	public DataObjectScanner(JClassScanner jClassScanner)
	{
		this.jClassScanner = jClassScanner;
	}

	/**
	 * @param name
	 * @return
	 */
	public String getDataObject(String name)
	{
		initializeDataObjects();
		return dataObjects.get(name);
	}

	public String[] getDataObjectIdentifiers(String name)
	{
		initializeDataObjects();
		return dataObjectIdentifiers.get(name);
	}

	/**
	 * @return
	 */
	public Iterator<String> iterateDataObjects()
	{
		initializeDataObjects();
		return dataObjects.keySet().iterator();
	}

	/**
	 * 
	 */
	protected void initializeDataObjects()
	{
		if (!initialized)
		{
			dataObjects = new HashMap<String, String>();
			dataObjectIdentifiers = new HashMap<String, String[]>();
			JClassType[] dataObjectTypes;
			try 
			{
				initializeDefaultDataObjects();
				dataObjectTypes =  jClassScanner.searchClassesByAnnotation(DataObject.class);
			} 
			catch (Exception e) 
			{
				throw new CruxGeneratorException("Error initializing DataObjects scanner.",e);
			}
			if (dataObjectTypes != null)
			{
				for (JClassType dataClass : dataObjectTypes) 
				{
					DataObject annot = dataClass.getAnnotation(DataObject.class);
					if (dataObjects.containsKey(annot.value()))
					{
						throw new CruxGeneratorException("Duplicated alias for DataObject found: ["+annot.value()+"].");
					}

					dataObjects.put(annot.value(), dataClass.getQualifiedSourceName());
					dataObjectIdentifiers.put(annot.value(), extractIdentifiers(dataClass));
				}
			}
			initialized = true;
		}
	}

	/**
	 * @param dataClass
	 * @return
	 */
	private String[] extractIdentifiers(JClassType dataClass)
	{
		List<String> ids = new ArrayList<String>();
		JField[] fields = JClassUtils.getDeclaredFields(dataClass);
		for (JField field : fields)
		{
			if (field.getAnnotation(DataObjectIdentifier.class) != null)
			{
				if (field.isPublic())
				{
					ids.add(field.getName());
				}
				else
				{
					ids.add(JClassUtils.getGetterMethod(field.getName(), dataClass)+"()");
				}
			}
		}
		return ids.toArray(new String[ids.size()]);
	}

	/**
	 * 
	 */
	private void initializeDefaultDataObjects()
	{
		dataObjects.put(String.class.getSimpleName(), String.class.getCanonicalName());
		dataObjects.put(Integer.class.getSimpleName(), Integer.class.getCanonicalName());
		dataObjects.put(Short.class.getSimpleName(), Short.class.getCanonicalName());
		dataObjects.put(Byte.class.getSimpleName(), Byte.class.getCanonicalName());
		dataObjects.put(Long.class.getSimpleName(), Long.class.getCanonicalName());
		dataObjects.put(Float.class.getSimpleName(), Float.class.getCanonicalName());
		dataObjects.put(Double.class.getSimpleName(), Double.class.getCanonicalName());
		dataObjects.put(Boolean.class.getSimpleName(), Boolean.class.getCanonicalName());
		dataObjects.put(Date.class.getSimpleName(), Date.class.getCanonicalName());
		dataObjects.put(Character.class.getSimpleName(), Character.class.getCanonicalName());
		dataObjects.put("int","int");
		dataObjects.put("long","long");
		dataObjects.put("byte","byte");
		dataObjects.put("short","short");
		dataObjects.put("float","float");
		dataObjects.put("double","double");
		dataObjects.put("boolean","boolean");
		dataObjects.put("char","char");
	}
}
