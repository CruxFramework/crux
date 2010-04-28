package br.com.sysmap.crux.widgets.client.event.paste;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 */
public class PasteEventSourceRegisterFactory
{
	private static PasteEventSourceRegister pasteRegister = null;
	
	/**
	 * @return
	 */
	public static PasteEventSourceRegister getRegister()
	{
		if (pasteRegister == null)
		{
			pasteRegister = GWT.create(PasteEventSourceRegisterImpl.class);
		}
		return pasteRegister;
	}
}