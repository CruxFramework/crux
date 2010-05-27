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
package br.com.sysmap.crux.core.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.i18n.MessageName;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

import com.google.gwt.i18n.client.LocalizableResource;

/**
 * Class for retrieve the messages interface, based on the annotation MessageName or on the class SimpleName, if annotation not present
 * @author Thiago Bustamante
 */
public class MessageClasses 
{
	private static final Lock lock = new ReentrantLock();
	private static final Log logger = LogFactory.getLog(MessageClasses.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static Map<String, Class<? extends LocalizableResource>> messagesClasses = null;	
	
	/**
	 * Return the controller that implements the interface informed.
	 * @param interfaceName
	 * @return
	 */
	public static Class<?> getMessageClass(String message)
	{
		if (messagesClasses == null)
		{
			initialize();
		}
		return messagesClasses.get(message);
	}
	
	/**
	 * Initialise the ScreenResourceResolverScanner factory
	 * @param urls
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void initialize()
	{
		if (messagesClasses == null)
		{
			lock.lock();
			try 
			{
				if (messagesClasses == null)
				{
					messagesClasses = new HashMap<String, Class<? extends LocalizableResource>>();
					Set<String> messagesNames =  ClassScanner.searchClassesByInterface(LocalizableResource.class);
					if (messagesNames != null)
					{
						for (String message : messagesNames) 
						{
							Class<?> messageClass = Class.forName(message);
							MessageName messageNameAnnot = messageClass.getAnnotation(MessageName.class);
							br.com.sysmap.crux.core.client.i18n.Name nameAnnot = messageClass.getAnnotation(br.com.sysmap.crux.core.client.i18n.Name.class);
							if (messageNameAnnot!= null)
							{
								if (messagesClasses.containsKey(messageNameAnnot.value()))
								{
									throw new CruxGeneratorException(messages.messagesClassesDuplicatedMessageKey(messageNameAnnot.value()));
								}
								messagesClasses.put(messageNameAnnot.value(), (Class<? extends LocalizableResource>) messageClass);
							}
							else if (nameAnnot!= null)
							{
								if (messagesClasses.containsKey(nameAnnot.value()))
								{
									throw new CruxGeneratorException(messages.messagesClassesDuplicatedMessageKey(nameAnnot.value()));
								}
								messagesClasses.put(nameAnnot.value(), (Class<? extends LocalizableResource>) messageClass);
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
								messagesClasses.put(className, (Class<? extends LocalizableResource>) messageClass);
							}
						}
					}
				}
			} 
			catch (ClassNotFoundException e) 
			{
				logger.error(messages.messagesClassesInitializeError(e.getLocalizedMessage()),e);
			}
			finally
			{
				lock.unlock();
			}
		}
	}
}
