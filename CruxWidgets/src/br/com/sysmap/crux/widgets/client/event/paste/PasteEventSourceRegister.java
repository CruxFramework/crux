package br.com.sysmap.crux.widgets.client.event.paste;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public interface PasteEventSourceRegister
{
	void registerPasteEventSource(HasPasteHandlers handler, Element element);
}