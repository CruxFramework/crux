package org.cruxframework.crux.widgets.client.event.paste;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 */
public interface PasteEventSourceRegister
{
	void registerPasteEventSource(HasPasteHandlers handler, Element element);
}