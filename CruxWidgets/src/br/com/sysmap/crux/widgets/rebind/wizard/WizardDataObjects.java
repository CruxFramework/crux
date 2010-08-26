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
package br.com.sysmap.crux.widgets.rebind.wizard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.widgets.client.wizard.WizardData;
import br.com.sysmap.crux.widgets.rebind.WidgetGeneratorMessages;

/**
 * Maps all controllers in a module.
 * @author Thiago Bustamante
 *
 */
public class WizardDataObjects 
{
	private static final Log logger = LogFactory.getLog(WizardDataObjects.class);
	private static final Lock lock = new ReentrantLock();
	protected static WidgetGeneratorMessages messages = (WidgetGeneratorMessages)MessagesFactory.getMessages(WidgetGeneratorMessages.class);
	private static Map<String, String> wizardDataObjects;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (wizardDataObjects != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (wizardDataObjects != null)
			{
				return;
			}
			
			initializeWizardDataObjects();
		}
		finally
		{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
	protected static void initializeWizardDataObjects()
	{
		wizardDataObjects = new HashMap<String, String>();
		
		Set<String> wizardDataNames =  ClassScanner.searchClassesByAnnotation(WizardData.class);
		if (wizardDataNames != null)
		{
			for (String wizardData : wizardDataNames) 
			{
				try 
				{
					Class<?> wizardDataClass = Class.forName(wizardData);
					WizardData annot = wizardDataClass.getAnnotation(WizardData.class);
					if (wizardDataObjects.containsKey(annot.value()))
					{
						throw new CruxGeneratorException(messages.wizardDataDuplicatedObject(annot.value()));
					}
					
					wizardDataObjects.put(annot.value(), wizardDataClass.getCanonicalName());
				} 
				catch (ClassNotFoundException e) 
				{
					logger.error(messages.wizardDataInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}
	
	/**
	 * @param name
	 * @return
	 */
	public static String getWizardData(String name)
	{
		if (wizardDataObjects == null)
		{
			initialize();
		}
		return wizardDataObjects.get(name);
	}
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateWizardDatas()
	{
		if (wizardDataObjects == null)
		{
			initialize();
		}
		return wizardDataObjects.keySet().iterator();
	}
}
