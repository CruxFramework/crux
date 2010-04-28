package br.com.sysmap.crux.widgets.client.event.paste;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class PasteEventSourceRegisterImpl implements PasteEventSourceRegister
{
	public native void registerPasteEventSource(HasPasteHandlers source, Element element)/*-{
		element.onpaste = function()
		{
			setTimeout
			(
				function()
				{
					@br.com.sysmap.crux.widgets.client.event.paste.PasteEvent::fire(Lbr/com/sysmap/crux/widgets/client/event/paste/HasPasteHandlers;)(source);
				},
				10
			);
		};
	}-*/;
}