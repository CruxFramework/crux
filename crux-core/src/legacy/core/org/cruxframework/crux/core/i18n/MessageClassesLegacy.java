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
package org.cruxframework.crux.core.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.i18n.MessageName;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.scan.ClassScanner;

import com.google.gwt.i18n.client.LocalizableResource;

/**
 * Class for retrieve the messages interface, based on the annotation MessageName or on the class SimpleName, if annotation not present
 * @author Thiago da Rosa de Bustamante
 */
@Legacy(MessageClasses.class)
public class MessageClassesLegacy 
{
	/**
	 * @param urls
	 */
	@SuppressWarnings("deprecation")
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
							org.cruxframework.crux.core.client.i18n.Name nameAnnot = messageClass.getAnnotation(org.cruxframework.crux.core.client.i18n.Name.class);
							if (messageNameAnnot!= null)
							{
								if (messagesClasses.containsKey(messageNameAnnot.value()))
								{
									throw new CruxGeneratorException("Duplicated Message Key: ["+messageNameAnnot.value()+"].");
								}
								messagesClasses.put(messageNameAnnot.value(), messageClass.getCanonicalName());
							}
							else if (nameAnnot!= null)
							{
								if (messagesClasses.containsKey(nameAnnot.value()))
								{
									throw new CruxGeneratorException("Duplicated Message Key: ["+nameAnnot.value()+"].");
								}
								messagesClasses.put(nameAnnot.value(), messageClass.getCanonicalName());
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
