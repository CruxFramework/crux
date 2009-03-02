package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;

public interface IEventProcessorFactory 
{
	/**
	 * Create a EventProcessor for the event.
	 * @param event
	 * @return
	 * @throws InterfaceConfigException
	 */
	EventProcessor createEventProcessor(final Event event) throws InterfaceConfigException;

}
