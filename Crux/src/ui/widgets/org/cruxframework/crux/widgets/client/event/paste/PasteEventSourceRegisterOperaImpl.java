package org.cruxframework.crux.widgets.client.event.paste;

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
					@org.cruxframework.crux.widgets.client.event.paste.PasteEvent::fire(Lorg/cruxframework/crux/widgets/client/event/paste/HasPasteHandlers;)(source);
				}
				,10
			);
		};
	}-*/;
}