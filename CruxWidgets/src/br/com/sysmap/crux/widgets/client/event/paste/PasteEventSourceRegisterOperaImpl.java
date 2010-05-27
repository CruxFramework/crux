package br.com.sysmap.crux.widgets.client.event.paste;

import com.google.gwt.dom.client.Element;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class PasteEventSourceRegisterOperaImpl implements PasteEventSourceRegister
{
	public native void registerPasteEventSource(HasPasteHandlers source, Element element)/*-{
		element.oninput = function()
		{
			setTimeout
			(
				function()
				{
					@br.com.sysmap.crux.widgets.client.event.paste.PasteEvent::fire(Lbr/com/sysmap/crux/widgets/client/event/paste/HasPasteHandlers;)(source);
				}
				,10
			);
		};
	}-*/;
}