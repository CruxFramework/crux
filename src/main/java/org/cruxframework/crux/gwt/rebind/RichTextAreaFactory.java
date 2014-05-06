/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.gwt.rebind;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasInitializeHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;
import com.google.gwt.user.client.ui.RichTextArea.Formatter;
import com.google.gwt.user.client.ui.RichTextArea.Justification;


class RichTextAreaContext extends WidgetCreatorContext
{

	protected FastMap<String> declaredProperties;
	
}

/**
 * Represents a rich text area component
 * @author Thiago Bustamante
 */
@DeclarativeFactory(id="richTextArea", library="gwt", targetWidget=RichTextArea.class)
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
	@TagChildren({
		@TagChild(value=RichTextAreaFactory.ContentProcessor.class, autoProcess=false)
	})

public class RichTextAreaFactory extends FocusWidgetFactory<RichTextAreaContext> 
implements HasHTMLFactory<RichTextAreaContext>, HasInitializeHandlersFactory<RichTextAreaContext>
{
	@Override
	public void processAttributes(SourcePrinter out, final RichTextAreaContext context) throws CruxGeneratorException 
	{
		super.processAttributes(out, context);
		context.declaredProperties = readDeclaredProperties(context);
	}
	
	@Override
	public void postProcess(SourcePrinter out, RichTextAreaContext context) throws CruxGeneratorException 
	{
		super.postProcess(out, context);
		String widget = context.getWidget();
		// We need to give UI thread time to render the textArea before try to focus it
		String widgetClassName = getWidgetClassName();
		printlnPostProcessing("final "+widgetClassName+" "+widget+" = ("+widgetClassName+")"+ getViewVariable()+".getWidget("+EscapeUtils.quote(context.getWidgetId())+");");
		printlnPostProcessing(widget+".setFocus(true);");// Necessary to work around a bug in mozzila
		printFormatterOptions(context);
	}
	
	/**
	 * Reads all declared properties in the component span tag. These properties will be used
	 * to initialise the basic formatter. It will be done by method initBasicFormatterOptions
	 * @param element
	 */
	protected FastMap<String> readDeclaredProperties(WidgetCreatorContext context)
	{
		FastMap<String> declaredProperties = new FastMap<String>();
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
	protected void printFormatterOptions(RichTextAreaContext context)
	{
		String formatter = ViewFactoryCreator.createVariableName("formatter");
		printlnPostProcessing(Formatter.class.getCanonicalName()+" "+formatter+" = "+context.getWidget()+".getFormatter();");
		
		printlnPostProcessing("if ("+formatter+" != null){");
			if (context.declaredProperties.containsKey("backColor"))
			{
				printlnPostProcessing(formatter+".setBackColor("+EscapeUtils.quote(context.declaredProperties.get("backColor"))+");");
			}

			if (context.declaredProperties.containsKey("fontName"))
			{
				printlnPostProcessing(formatter+".setFontName("+EscapeUtils.quote(context.declaredProperties.get("fontName"))+");");
			}

			if (context.declaredProperties.containsKey("fontSize"))
			{
				switch (Integer.parseInt(context.declaredProperties.get("fontSize"))) 
				{
				case 1:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".XX_SMALL);");
					break;
				case 2:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".X_SMALL);");
					break;
				case 3:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".SMALL);");
					break;
				case 4:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".MEDIUM);");
					break;
				case 5:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".LARGE);");
					break;
				case 6:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".X_LARGE);");
					break;
				case 7:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".XX_LARGE);");
					break;

				default:
					printlnPostProcessing(formatter+".setFontSize("+FontSize.class.getCanonicalName()+".MEDIUM);");
				}
			}
			printlnPostProcessing("}");

		if (context.declaredProperties.containsKey("foreColor"))
		{
			printlnPostProcessing(formatter+".setForeColor("+EscapeUtils.quote(context.declaredProperties.get("foreColor"))+");");
		}

		if (context.declaredProperties.containsKey("justification"))
		{
			String justification = context.declaredProperties.get("justification");
			if (justification.equalsIgnoreCase("center"))
			{
				printlnPostProcessing(formatter+".setJustification("+Justification.class.getCanonicalName()+".CENTER);");
			}
			else if (justification.equalsIgnoreCase("left"))
			{
				printlnPostProcessing(formatter+".setJustification("+Justification.class.getCanonicalName()+".LEFT);");
			}
			else if (justification.equalsIgnoreCase("right"))
			{
				printlnPostProcessing(formatter+".setJustification("+Justification.class.getCanonicalName()+".RIGHT);");
			}
		}

		if (context.declaredProperties.containsKey("bold") && Boolean.parseBoolean(context.declaredProperties.get("bold")))
		{
			printlnPostProcessing(formatter+".toggleBold();");
		}
		if (context.declaredProperties.containsKey("italic") && Boolean.parseBoolean(context.declaredProperties.get("italic")))
		{
			printlnPostProcessing(formatter+".toggleItalic();");
		}
		if (context.declaredProperties.containsKey("subscript") && Boolean.parseBoolean(context.declaredProperties.get("subscript")))
		{
			printlnPostProcessing(formatter+".toggleSubscript();");
		}
		if (context.declaredProperties.containsKey("superscript") && Boolean.parseBoolean(context.declaredProperties.get("superscript")))
		{
			printlnPostProcessing(formatter+".toggleSuperscript();");
		}
		if (context.declaredProperties.containsKey("underline") && Boolean.parseBoolean(context.declaredProperties.get("underline")))
		{
			printlnPostProcessing(formatter+".toggleUnderline();");
		}
		if (context.declaredProperties.containsKey("strikethrough") && Boolean.parseBoolean(context.declaredProperties.get("strikethrough")))
		{
			printlnPostProcessing(formatter+".toggleStrikethrough();");
		}
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", type=HTMLTag.class)
	public static class ContentProcessor extends WidgetChildProcessor<RichTextAreaContext> {}

	@Override
    public RichTextAreaContext instantiateContext()
    {
	    return new RichTextAreaContext();
    }	
}
