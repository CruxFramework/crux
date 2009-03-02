package br.com.sysmap.crux.core.client.event;

import br.com.sysmap.crux.core.client.component.Screen;

/**
 * Abstraction for a event processor. Each processor process events according with the 
 * properties in the Event Object.
 * @author Thiago
 *
 */
public interface EventProcessor {
	void processEvent(Screen screen, String idSender);
}
