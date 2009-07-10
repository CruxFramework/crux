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

import br.com.sysmap.crux.core.client.i18n.Name;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;

import com.google.gwt.i18n.client.Messages;

/**
 * Class for retrieve the messages interface, based on the annotation Name or ont the class SimpleName, if annotation not present
 * @author Thiago Bustamante
 */
public class MessageClasses 
{
	private static final Log logger = LogFactory.getLog(MessageClasses.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static Map<String, Class<? extends Messages>> messagesClasses = null;
	private static final Lock lock = new ReentrantLock();	
	/**
	 * Initialise the ScreenResourceResolverScanner factory
	 * @param urls
	 */
	@SuppressWarnings("unchecked")
	public static void initialize()
	{
		if (messagesClasses == null)
		{
			lock.lock();
			try 
			{
				if (messagesClasses == null)
				{
					messagesClasses = new HashMap<String, Class<? extends Messages>>();
					Set<String> messagesNames =  ClassScanner.searchClassesByInterface(Messages.class);
					if (messagesNames != null)
					{
						for (String message : messagesNames) 
						{
							Class<?> messageClass = Class.forName(message);
							Name annot = messageClass.getAnnotation(Name.class);
							if (annot!= null)
							{
								messagesClasses.put(annot.value(), (Class<? extends Messages>) messageClass);
							}
							else
							{
								messagesClasses.put(messageClass.getSimpleName(), (Class<? extends Messages>) messageClass);
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
}
