package br.com.sysmap.crux.core.server.screen;

import java.io.PrintWriter;

import br.com.sysmap.crux.core.utils.HtmlUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

public class ComponentRendererImpl implements ComponentRenderer
{
	@Override
	public void renderStart(Component component, PrintWriter writer) 
	{
		writer.print("<span");
		renderAttributes(component, writer);
		writer.print(">");
	}

	@Override
	public void renderEnd(Component component, PrintWriter writer) 
	{
		writer.print("</span>");
	}

	protected void renderAttributes(Component component, PrintWriter writer)
	{
		if (component.id != null)
			writer.print(" id='"+HtmlUtils.filterValue(component.getId())+"'");
		if (component.type != null)
			writer.print(" _type='"+RegexpPatterns.REGEXP_INVALID_HTML_CHARS.matcher(component.type).replaceAll("")+"'");
		if (component.serverBind != null)
			writer.print(" _serverBind='"+HtmlUtils.filterValue(component.serverBind)+"'");
		if (component.width != null)
			writer.print(" _width='"+RegexpPatterns.REGEXP_INVALID_HTML_CHARS.matcher(component.width).replaceAll("")+"'");
		if (component.height != null)
			writer.print(" _height='"+RegexpPatterns.REGEXP_INVALID_HTML_CHARS.matcher(component.height).replaceAll("")+"'");
		if (component.className != null)
			writer.print(" _class='"+RegexpPatterns.REGEXP_INVALID_HTML_CHARS.matcher(component.className).replaceAll("")+"'");
		if (component.style != null)
			writer.print(" _style='"+RegexpPatterns.REGEXP_INVALID_HTML_CHARS.matcher(component.style).replaceAll("")+"'");
	}
}
