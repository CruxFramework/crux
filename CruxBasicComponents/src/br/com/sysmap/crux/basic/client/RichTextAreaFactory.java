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
package br.com.sysmap.crux.basic.client;

import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.component.InterfaceConfigException;
import br.com.sysmap.crux.core.client.component.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.component.ScreenLoadHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.BasicFormatter;
import com.google.gwt.user.client.ui.RichTextArea.ExtendedFormatter;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;
import com.google.gwt.user.client.ui.RichTextArea.Justification;


/**
 * Represents a rich text area component
 * @author Thiago Bustamante
 */
public class RichTextAreaFactory extends FocusWidgetFactory<RichTextArea>
{
	@Override
	protected RichTextArea instantiateWidget(Element element, String widgetId) 
	{
		return new RichTextArea();
	}
	
	@Override
	protected void processAttributes(final RichTextArea widget, Element element, String widgetId) throws InterfaceConfigException 
	{
		super.processAttributes(widget, element, widgetId);
		final Map<String, String> declaredProperties = readDeclaredProperties(element);

		// We need to give UI thread time to render the textArea before try to focus it
		addScreenLoadedHandler(new ScreenLoadHandler()
		{
			public void onLoad(ScreenLoadEvent event) 
			{
				widget.setFocus(true);// Necessary to work around a bug in mozzila
				initBasicFormatterOptions(widget, declaredProperties);
				initExtendedFormatterOptions(widget, declaredProperties);
			}
		});

		String innerHtml = element.getInnerHTML();
		if (innerHtml != null && innerHtml.length() > 0)
		{
			((HasHTML)widget).setHTML(innerHtml);
			element.setInnerHTML("");
		}		
	}
	
	/**
	 * Reads all declared properties in the component span tag. These properties will be used
	 * to initialise the basic formatter. It will be done by method initBasicFormatterOptions
	 * @param element
	 */
	protected Map<String, String> readDeclaredProperties(Element element)
	{
		 Map<String, String> declaredProperties = new HashMap<String, String>();
		String backColor = element.getAttribute("_backColor");
		if (backColor != null && backColor.length() > 0)
		{
			declaredProperties.put("backColor",backColor);
		}
		String fontName = element.getAttribute("_fontName");
		if (fontName != null && fontName.length() > 0)
		{
			declaredProperties.put("fontName",fontName);
		}
		String fontSize = element.getAttribute("_fontSize");
		if (fontSize != null && fontSize.length() > 0)
		{
			declaredProperties.put("fontSize",fontSize);
		}
		String foreColor = element.getAttribute("_foreColor");
		if (foreColor != null && foreColor.length() > 0)
		{
			declaredProperties.put("foreColor",foreColor);
		}
		String justification = element.getAttribute("_justification");
		if (justification != null && justification.length() > 0)
		{
			declaredProperties.put("justification",justification);
		}
		String bold = element.getAttribute("_bold");
		if (bold != null && bold.length() > 0)
		{
			declaredProperties.put("bold",bold);
		}
		String italic = element.getAttribute("_italic");
		if (italic != null && italic.length() > 0)
		{
			declaredProperties.put("italic",italic);
		}
		String subscript = element.getAttribute("_subscript");
		if (subscript != null && subscript.length() > 0)
		{
			declaredProperties.put("subscript",subscript);
		}
		String superscript = element.getAttribute("_superscript");
		if (superscript != null && superscript.length() > 0)
		{
			declaredProperties.put("superscript",superscript);
		}
		String underline = element.getAttribute("_underline");
		if (underline != null && underline.length() > 0)
		{
			declaredProperties.put("underline",underline);
		}
		String strikethrough = element.getAttribute("_strikethrough");
		if (strikethrough != null && strikethrough.length() > 0)
		{
			declaredProperties.put("strikethrough",strikethrough);
		}
		return declaredProperties;
	}

	/**
	 * Render basic formatter options
	 */
	protected void initBasicFormatterOptions(RichTextArea widget, Map<String, String> declaredProperties)
	{
		final BasicFormatter basicFormatter = widget.getBasicFormatter();
		if (basicFormatter != null)
		{
			if (declaredProperties.containsKey("backColor"))
			{
				widget.getBasicFormatter().setBackColor(declaredProperties.get("backColor"));
			}

			if (declaredProperties.containsKey("fontName"))
			{
				widget.getBasicFormatter().setFontName(declaredProperties.get("fontName"));
			}

			if (declaredProperties.containsKey("fontSize"))
			{
				switch (Integer.parseInt(declaredProperties.get("fontSize"))) 
				{
				case 1:
					widget.getBasicFormatter().setFontSize(FontSize.XX_SMALL);
					break;
				case 2:
					widget.getBasicFormatter().setFontSize(FontSize.X_SMALL);
					break;
				case 3:
					widget.getBasicFormatter().setFontSize(FontSize.SMALL);
					break;
				case 4:
					widget.getBasicFormatter().setFontSize(FontSize.MEDIUM);
					break;
				case 5:
					widget.getBasicFormatter().setFontSize(FontSize.LARGE);
					break;
				case 6:
					widget.getBasicFormatter().setFontSize(FontSize.X_LARGE);
					break;
				case 7:
					widget.getBasicFormatter().setFontSize(FontSize.XX_LARGE);
					break;

				default:
					widget.getBasicFormatter().setFontSize(FontSize.MEDIUM);
				}
			}

			if (declaredProperties.containsKey("foreColor"))
			{
				widget.getBasicFormatter().setForeColor(declaredProperties.get("foreColor"));
			}

			if (declaredProperties.containsKey("justification"))
			{
				String justification = declaredProperties.get("justification");
				if (justification.equalsIgnoreCase("center"))
				{
					widget.getBasicFormatter().setJustification(Justification.CENTER);
				}
				else if (justification.equalsIgnoreCase("left"))
				{
					widget.getBasicFormatter().setJustification(Justification.LEFT);
				}
				else if (justification.equalsIgnoreCase("right"))
				{
					widget.getBasicFormatter().setJustification(Justification.RIGHT);
				}
			}

			if (declaredProperties.containsKey("bold") && Boolean.parseBoolean(declaredProperties.get("bold")))
			{
				widget.getBasicFormatter().toggleBold();
			}
			if (declaredProperties.containsKey("italic") && Boolean.parseBoolean(declaredProperties.get("italic")))
			{
				widget.getBasicFormatter().toggleItalic();
			}
			if (declaredProperties.containsKey("subscript") && Boolean.parseBoolean(declaredProperties.get("subscript")))
			{
				widget.getBasicFormatter().toggleSubscript();
			}
			if (declaredProperties.containsKey("superscript") && Boolean.parseBoolean(declaredProperties.get("superscript")))
			{
				widget.getBasicFormatter().toggleSuperscript();
			}
			if (declaredProperties.containsKey("underline") && Boolean.parseBoolean(declaredProperties.get("underline")))
			{
				widget.getBasicFormatter().toggleUnderline();
			}
		}
	}
	
	/**
	 * Render extended formatter options
	 */
	protected void initExtendedFormatterOptions(RichTextArea widget, Map<String, String> declaredProperties) 
	{
		final ExtendedFormatter extendedFormatter = widget.getExtendedFormatter();
		if (extendedFormatter != null)
		{
			if (declaredProperties.containsKey("strikethrough") && Boolean.parseBoolean(declaredProperties.get("strikethrough")))
			{
				widget.getExtendedFormatter().toggleStrikethrough();
			}
		}		
	}
	
}
