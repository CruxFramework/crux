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
package org.cruxframework.crux.core.rebind.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.client.i18n.MessageName;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;

import com.google.gwt.i18n.client.LocalizableResource;

/**
 * Class for retrieve the messages interface, based on the annotation MessageName or on the class SimpleName, if annotation not present
 * @author Thiago da Rosa de Bustamante
 */
public class MessageClasses 
{
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> messagesClasses = null;	
	
	/**
	 * Return the className associated with the name informed
	 * @param interfaceName
	 * @return
	 */
	public static String getMessageClass(String message)
	{
		if (messagesClasses == null)
		{
			initialize();
		}
		return messagesClasses.get(message);
	}
	
	/**
	 * @param urls
	 */
	public static void initialize()
	{
		if (messagesClasses == null)
		{
			lock.lock();
			try 
			{
				if (messagesClasses == null)
				{
					messagesClasses = new HashMap<String, String>();
					Set<String> messagesNames =  ClassScanner.searchClassesByInterface(LocalizableResource.class);
					if (messagesNames != null)
					{
						for (String message : messagesNames) 
						{
							Class<?> messageClass = Class.forName(message);
							MessageName messageNameAnnot = messageClass.getAnnotation(MessageName.class);
							if (messageNameAnnot!= null)
							{
								if (messagesClasses.containsKey(messageNameAnnot.value()))
								{
									throw new CruxGeneratorException("Duplicated Message Key: ["+messageNameAnnot.value()+"].");
								}
								messagesClasses.put(messageNameAnnot.value(), messageClass.getCanonicalName());
							}
							else
							{
								String className = messageClass.getSimpleName();
								if (className.length() > 1)
								{
									className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
								}
								else
								{
									className = className.toLowerCase();
								}
								if (messagesClasses.containsKey(className))
								{
									throw new CruxGeneratorException("Duplicated Message Key: ["+className+"].");
								}
								messagesClasses.put(className, messageClass.getCanonicalName());
							}
						}
					}
				}
			} 
			catch (ClassNotFoundException e) 
			{
				throw new CruxGeneratorException("Error initializing messagesClasses.",e);
			}
			finally
			{
				lock.unlock();
			}
		}
	}
}
