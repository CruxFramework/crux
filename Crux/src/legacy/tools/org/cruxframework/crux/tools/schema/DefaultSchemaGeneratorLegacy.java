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
package org.cruxframework.crux.tools.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.template.TemplateParser;
import org.cruxframework.crux.core.declarativeui.template.Templates;
import org.cruxframework.crux.core.declarativeui.template.TemplatesScanner;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AllChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.SequenceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.TextChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyTag;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.HTMLTag;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.classpath.ClassPathResolverInitializer;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.cruxframework.crux.scannotation.ClasspathUrlFinder;
import org.w3c.dom.Document;

import com.google.gwt.resources.client.ResourcePrototype;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Legacy(value=DefaultSchemaGenerator.class)
public class DefaultSchemaGeneratorLegacy 
{
	/**
	 * 
	 * @param out
	 */
	@Deprecated
	@Legacy
	private void generateElementAttributesForAllViewElements(PrintStream out)
	{
		out.println("<xs:attribute name=\"title\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"fragment\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useResource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useSerializable\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useView\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClosing\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onClose\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onResized\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onLoad\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onActivate\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"onHistoryChanged\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"width\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"height\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"dataObject\" type=\"xs:string\"/>");
	}

	/**
	 * 
	 * @param out
	 */
	@Deprecated
	@Legacy
	private void generateTemplateElement(PrintStream out)
	{
		out.println("<xs:element name=\"template\" type=\"Template\" >");
		out.println("<xs:annotation>");
		out.println("<xs:documentation>"+StringEscapeUtils.escapeXml(schemaMessages.templateDescription())+"</xs:documentation>");
		out.println("</xs:annotation>");
		out.println("</xs:element>");
		out.println("<xs:complexType name=\"Template\">");
		out.println("<xs:choice>");
		out.println("<xs:any minOccurs=\"0\" maxOccurs=\"unbounded\"/>");
		out.println("</xs:choice>");
		out.println("<xs:attribute name=\"library\" type=\"xs:string\" use=\"required\"/>");
		out.println("<xs:attribute name=\"useController\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useResource\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useSerializable\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useFormatter\" type=\"xs:string\"/>");
		out.println("<xs:attribute name=\"useDataSource\" type=\"xs:string\"/>");
		out.println("</xs:complexType>");
	}
}