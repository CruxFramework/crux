package br.com.sysmap.crux.ext.server.component;

import java.io.PrintWriter;

import br.com.sysmap.crux.core.server.screen.Component;
import br.com.sysmap.crux.core.server.screen.ComponentRendererImpl;

public class TextAreaRenderer extends ComponentRendererImpl
{
	@Override
	protected void renderAttributes(Component component, PrintWriter writer) 
	{
		super.renderAttributes(component, writer);
		TextArea textArea = (TextArea)component;
		if (textArea.rows > 0)
			writer.print(" _rows='"+textArea.rows+"'");

	}
}
