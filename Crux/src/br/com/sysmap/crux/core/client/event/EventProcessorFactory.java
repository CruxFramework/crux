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
