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
package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

import com.google.gwt.core.client.GWT;


/**
 * Builds EventProcessors, according with the Event object passed as parameter.
 * @author Thiago
 *
 */
public class EventProcessorFactory 
{
	private static EventProcessorFactory instance = null;
	
	private IEventProcessorFactory factoryImpl;
	
	private EventProcessorFactory() 
	{
		this.factoryImpl = (IEventProcessorFactory)GWT.create(IEventProcessorFactory.class);
	}
	
	/**
	 * Retrieve the EventProcessorFactory instance.
	 * Is not synchronized, but it is not a problem. The screen is always build on a single thread, because 
	 * Javascript does not run on a multi-thread environment.
	 * @return
	 */
	public static EventProcessorFactory getInstance()
	{
		if (instance == null)
		{
			instance = new EventProcessorFactory();
		}
		return instance;
	}
	
	/**
	 * Create a EventProcessor for the event.
	 * @param event
	 * @return
	 * @throws InterfaceConfigException
	 */
	public EventProcessor createEventProcessor(final Event event) throws InterfaceConfigException
	{
		return factoryImpl.createEventProcessor(event);
	}
	
}
