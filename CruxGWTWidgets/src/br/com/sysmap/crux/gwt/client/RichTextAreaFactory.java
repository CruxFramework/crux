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
package br.com.sysmap.crux.gwt.client;

import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.screen.factory.HasTextFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.RichTextArea.Justification;


/**
 * Represents a rich text area component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="richTextArea", library="gwt")
public class RichTextAreaFactory extends FocusWidgetFactory<RichTextArea> implements HasTextFactory<RichTextArea>
{
	@Override
	public RichTextArea instantiateWidget(Element element, String widgetId) 
	{
		return new RichTextArea();
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("backColor"),
		@TagAttributeDeclaration("fontName"),
		@TagAttributeDeclaration(value="fontSize", type=Integer.class),
		@TagAttributeDeclaration("foreColor"),
		@TagAttributeDeclaration("justification"),
		@TagAttributeDeclaration(value="bold", type=Boolean.class),
		@TagAttributeDeclaration(value="italic", type=Boolean.class),
		@TagAttributeDeclaration(value="subscript", type=Boolean.class),
		@TagAttributeDeclaration(value="superscript", type=Boolean.class),
		@TagAttributeDeclaration(value="underline", type=Boolean.class),
		@TagAttributeDeclaration(value="strikethrough", type=Boolean.class)
	})
	public void processAttributes(final WidgetFactoryContext<RichTextArea> context) throws InterfaceConfigException 
	{
		super.processAttributes(context);
		
		Element element = context.getElement();
		final RichTextArea widget = context.getWidget();
		
		final Map<String, String> declaredProperties = readDeclaredProperties(context);

		// We need to give UI thread time to render the textArea before try to focus it
		addScreenLoadedHandler(new ScreenLoadHandler()
		{
			public void onLoad(ScreenLoadEvent event) 
			{
				widget.setFocus(true);// Necessary to work around a bug in mozzila
				initFormatterOptions(widget, declaredProperties);
			}
		});

		String text = context.readWidgetProperty("text");
		if (text == null || text.length() ==0)
		{
			String innerHtml = element.getInnerHTML();
			if (innerHtml != null && innerHtml.length() > 0)
			{
				((HasHTML)widget).setHTML(innerHtml);
			}
		}
	}
	
	/**
	 * Reads all declared properties in the component span tag. These properties will be used
	 * to initialise the basic formatter. It will be done by method initBasicFormatterOptions
	 * @param element
	 */
	protected Map<String, String> readDeclaredProperties(WidgetFactoryContext<RichTextArea> context)
	{
		 Map<String, String> declaredProperties = new HashMap<String, String>();
		String backColor = context.readWidgetProperty("backColor");
		if (backColor != null && backColor.length() > 0)
		{
			declaredProperties.put("backColor",backColor);
		}
		String fontName = context.readWidgetProperty("fontName");
		if (fontName != null && fontName.length() > 0)
		{
			declaredProperties.put("fontName",fontName);
		}
		String fontSize = context.readWidgetProperty("fontSize");
		if (fontSize != null && fontSize.length() > 0)
		{
			declaredProperties.put("fontSize",fontSize);
		}
		String foreColor = context.readWidgetProperty("foreColor");
		if (foreColor != null && foreColor.length() > 0)
		{
			declaredProperties.put("foreColor",foreColor);
		}
		String justification = context.readWidgetProperty("justification");
		if (justification != null && justification.length() > 0)
		{
			declaredProperties.put("justification",justification);
		}
		String bold = context.readWidgetProperty("bold");
		if (bold != null && bold.length() > 0)
		{
			declaredProperties.put("bold",bold);
		}
		String italic = context.readWidgetProperty("italic");
		if (italic != null && italic.length() > 0)
		{
			declaredProperties.put("italic",italic);
		}
		String subscript = context.readWidgetProperty("subscript");
		if (subscript != null && subscript.length() > 0)
		{
			declaredProperties.put("subscript",subscript);
		}
		String superscript = context.readWidgetProperty("superscript");
		if (superscript != null && superscript.length() > 0)
		{
			declaredProperties.put("superscript",superscript);
		}
		String underline = context.readWidgetProperty("underline");
		if (underline != null && underline.length() > 0)
		{
			declaredProperties.put("underline",underline);
		}
		String strikethrough = context.readWidgetProperty("strikethrough");
		if (strikethrough != null && strikethrough.length() > 0)
		{
			declaredProperties.put("strikethrough",strikethrough);
		}
		return declaredProperties;
	}

	/**
	 * Render basic formatter options
	 */
	protected void initFormatterOptions(RichTextArea widget, Map<String, String> declaredProperties)
	{
		final Formatter formatter = widget.getFormatter();
		if (formatter != null)
		{
			if (declaredProperties.containsKey("backColor"))
			{
				formatter.setBackColor(declaredProperties.get("backColor"));
			}

			if (declaredProperties.containsKey("fontName"))
			{
				formatter.setFontName(declaredProperties.get("fontName"));
			}

			if (declaredProperties.containsKey("fontSize"))
			{
				switch (Integer.parseInt(declaredProperties.get("fontSize"))) 
				{
				case 1:
					formatter.setFontSize(FontSize.XX_SMALL);
					break;
				case 2:
					formatter.setFontSize(FontSize.X_SMALL);
					break;
				case 3:
					formatter.setFontSize(FontSize.SMALL);
					break;
				case 4:
					formatter.setFontSize(FontSize.MEDIUM);
					break;
				case 5:
					formatter.setFontSize(FontSize.LARGE);
					break;
				case 6:
					formatter.setFontSize(FontSize.X_LARGE);
					break;
				case 7:
					formatter.setFontSize(FontSize.XX_LARGE);
					break;

				default:
					formatter.setFontSize(FontSize.MEDIUM);
				}
			}

			if (declaredProperties.containsKey("foreColor"))
			{
				formatter.setForeColor(declaredProperties.get("foreColor"));
			}

			if (declaredProperties.containsKey("justification"))
			{
				String justification = declaredProperties.get("justification");
				if (justification.equalsIgnoreCase("center"))
				{
					formatter.setJustification(Justification.CENTER);
				}
				else if (justification.equalsIgnoreCase("left"))
				{
					formatter.setJustification(Justification.LEFT);
				}
				else if (justification.equalsIgnoreCase("right"))
				{
					formatter.setJustification(Justification.RIGHT);
				}
			}

			if (declaredProperties.containsKey("bold") && Boolean.parseBoolean(declaredProperties.get("bold")))
			{
				formatter.toggleBold();
			}
			if (declaredProperties.containsKey("italic") && Boolean.parseBoolean(declaredProperties.get("italic")))
			{
				formatter.toggleItalic();
			}
			if (declaredProperties.containsKey("subscript") && Boolean.parseBoolean(declaredProperties.get("subscript")))
			{
				formatter.toggleSubscript();
			}
			if (declaredProperties.containsKey("superscript") && Boolean.parseBoolean(declaredProperties.get("superscript")))
			{
				formatter.toggleSuperscript();
			}
			if (declaredProperties.containsKey("underline") && Boolean.parseBoolean(declaredProperties.get("underline")))
			{
				formatter.toggleUnderline();
			}
			if (declaredProperties.containsKey("strikethrough") && Boolean.parseBoolean(declaredProperties.get("strikethrough")))
			{
				formatter.toggleStrikethrough();
			}
		}
	}
}
