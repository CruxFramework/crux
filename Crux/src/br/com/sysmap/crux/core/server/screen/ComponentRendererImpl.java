/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
