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
package org.cruxframework.crux.core.rebind.serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.screen.CruxSerializable;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.serializable.annotation.SerializableName;
import org.cruxframework.crux.core.server.scan.ClassScanner;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
public class Serializers 
{
	private static final Log logger = LogFactory.getLog(Serializers.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> serializers;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (serializers != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (serializers != null)
			{
				return;
			}
			
			initializeSerializers();
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
	protected static void initializeSerializers()
	{
		serializers = new HashMap<String, String>();
		Set<String> serializerNames =  ClassScanner.searchClassesByInterface(CruxSerializable.class);
		if (serializerNames != null)
		{
			for (String serializer : serializerNames) 
			{
				try 
				{
					Class<? extends CruxSerializable> serializerClass = (Class<? extends CruxSerializable>) Class.forName(serializer);
					SerializableName annot = serializerClass.getAnnotation(SerializableName.class);
					if (annot != null)
					{
						if (serializers.containsKey(annot.value()))
						{
							throw new CruxGeneratorException("Duplicated SerializableName ["+annot.value()+"].");
						}
						serializers.put(annot.value(), serializerClass.getCanonicalName());
					}
					else
					{
						String simpleName = serializerClass.getSimpleName();
						if (serializers.containsKey(simpleName))
						{
							throw new CruxGeneratorException("Duplicated SerializableName ["+simpleName+"].");
						}
						serializers.put(simpleName, serializerClass.getCanonicalName());
					}
				} 
				catch (Throwable e) 
				{
					logger.error("Error initializing serializer.",e);
				}
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getCruxSerializable(String name)
	{
		if (serializers == null)
		{
			initialize();
		}
		
		if (name == null)
		{
			return null;
		}
		
		return serializers.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateSerializables()
	{
		if (serializers == null)
		{
			initialize();
		}
		
		return serializers.keySet().iterator();
	}
}
