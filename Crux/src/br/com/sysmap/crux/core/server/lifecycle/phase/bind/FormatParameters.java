package br.com.sysmap.crux.core.server.lifecycle.phase.bind;

import br.com.sysmap.crux.core.server.screen.Screen;

public class FormatParameters 
{
	
	public static Object unformat(Screen screen, String parName, String parValue)
	{
		if (screen.containsFormatter(parName))
		{
			return screen.getFormatter(parName).unformat(parValue);
			// TODO: Decidir se isso deve estar em volta de um try cacth ou se deve estourar a exceção.
		}
		
		return parValue;
	}

}
