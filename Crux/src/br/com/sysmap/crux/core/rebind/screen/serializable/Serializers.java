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
package br.com.sysmap.crux.core.rebind.screen.serializable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.screen.CruxSerializable;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.serializable.annotation.SerializableName;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Deprecated
public class Serializers 
{
	private static final Log logger = LogFactory.getLog(Serializers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Class<? extends CruxSerializable>> serializers;
	
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
		serializers = new HashMap<String, Class<? extends CruxSerializable>>();
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
							throw new CruxGeneratorException(messages.serializersDuplicatedMessageKey(annot.value()));
						}
						serializers.put(annot.value(), serializerClass);
					}
					else
					{
						String simpleName = serializerClass.getSimpleName();
						if (serializers.containsKey(simpleName))
						{
							throw new CruxGeneratorException(messages.serializersDuplicatedMessageKey(simpleName));
						}
						serializers.put(simpleName, serializerClass);
					}
				} 
				catch (Throwable e) 
				{
					logger.error(messages.serializersSerializersInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Class<? extends CruxSerializable> getCruxSerializable(String name)
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
