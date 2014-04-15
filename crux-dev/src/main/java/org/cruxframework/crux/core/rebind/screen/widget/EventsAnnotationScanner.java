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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorAnnotationsProcessor.EventCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class EventsAnnotationScanner
{
	private WidgetCreatorHelper factoryHelper;

	private final WidgetCreator<?> widgetCreator;

	EventsAnnotationScanner(WidgetCreator<?> widgetCreator, Class<?> type)
    {
		this.widgetCreator = widgetCreator;
		this.factoryHelper = new WidgetCreatorHelper(type);
    }
	
	/**
	 * @param factoryClass
	 * @throws CruxGeneratorException
	 */
	List<EventCreator> scanEvents() throws CruxGeneratorException
	{
		ArrayList<EventCreator> events = new ArrayList<EventCreator>();
		scanEvents(factoryHelper.getFactoryClass(), events, new HashSet<String>());
		return events;
	}
	
	/**
	 * @param factoryClass
	 * @param events
	 * @param added
	 * @throws CruxGeneratorException
	 */
	private void scanEvents(Class<?> factoryClass, List<EventCreator> events, Set<String> added) throws CruxGeneratorException
	{
		try
        {
			TagEvents tagEvents = factoryClass.getAnnotation(TagEvents.class);
			if (tagEvents != null)
			{
				for (TagEvent evt : tagEvents.value())
				{
					String evtBinderClassName = evt.value().getCanonicalName();
					if (!added.contains(evtBinderClassName))
					{
						added.add(evtBinderClassName);
						events.add(createEventProcessor(evt));
					}
				}
			}
	        Class<?> superclass = factoryClass.getSuperclass();
	        if (superclass!= null && !superclass.equals(Object.class))
	        {
	        	scanEvents(superclass, events, added);
	        }
	        Class<?>[] interfaces = factoryClass.getInterfaces();
	        for (Class<?> interfaceClass : interfaces)
	        {
	        	scanEvents(interfaceClass, events, added);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	

	/**
	 * @param evt
	 * @return
	 */
	private EventCreator createEventProcessor(TagEvent evt)
    {
		final EvtProcessor evtBinder;
		try
        {
	        evtBinder = evt.value().getConstructor(new Class<?>[]{WidgetCreator.class}).newInstance(widgetCreator);
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Error creating evtBinder.");
        }
		final Device[] supportedDevices = evt.supportedDevices();
		return new EventCreator()
		{
			public void createEvent(SourcePrinter out, WidgetCreatorContext context)
			{
				if (widgetCreator.isCurrentDeviceSupported(supportedDevices))
				{
					String eventValue = context.readWidgetProperty(evtBinder.getEventName());
					if (!StringUtils.isEmpty(eventValue))
					{
						evtBinder.processEvent(out, context, eventValue);
					}
				}
			}
		};
    }
}
