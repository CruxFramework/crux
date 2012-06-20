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
package org.cruxframework.crux.widgets.rebind.wizard;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.widgets.client.wizard.WizardCommandEvent;
import org.cruxframework.crux.widgets.client.wizard.WizardCommandHandler;
import org.cruxframework.crux.widgets.client.wizard.WizardDataSerializer;
import org.cruxframework.crux.widgets.client.wizard.WizardPage;
import org.cruxframework.crux.widgets.client.wizard.Wizard.NoData;


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@DeclarativeFactory(id="wizardPage", library="widgets", targetWidget=WizardPage.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="wizardId", required=true)
})
@TagEvents({
	@TagEvent(EnterEvtBind.class),
	@TagEvent(LeaveEvtBind.class)
})
@TagChildren({
	@TagChild(WizardPageFactory.CommandsProcessor.class)
})
public class WizardPageFactory extends AbstractWizardFactory
{
	@Override
	public void instantiateWidget(SourcePrinter out, WizardContext context) throws CruxGeneratorException
	{
	    String wizardContextObject = context.readWidgetProperty("wizardContextObject");
	    String wizardId = context.readWidgetProperty("wizardId");
		String className = getGenericSignature(wizardContextObject);
		String wizardData = DataObjects.getDataObject(wizardContextObject);
	    String wizardDataSerializer = getWizardSerializerInterface(wizardContextObject);
	    if (StringUtils.isEmpty(wizardData))
	    {
	    	wizardData = NoData.class.getCanonicalName();
	    }
		
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+EscapeUtils.quote(wizardId)+", ("+
				             WizardDataSerializer.class.getCanonicalName()+"<"+wizardData+">)GWT.create("+wizardDataSerializer+".class));");
	}

	@TagConstraints(tagName="commands", minOccurs="0")
	@TagChildren({
		@TagChild(WizardCommandsProcessor.class)
	})
	public static class CommandsProcessor extends WidgetChildProcessor<WizardContext> {}
	
	@TagConstraints(tagName="command", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration(value="onCommand", required=true)
	})
	public static class WizardCommandsProcessor extends WidgetChildProcessor<WizardContext>
	{
		WizardCommandEvtBind commandEvtBind;

		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
						"On page ["+getWidgetCreator().getScreen().getId()+"], there is an widget of type ["+context.readChildProperty("_type")+"] without id.");
			}
			
			String label = getWidgetCreator().getDeclaredMessage(context.readChildProperty("label"));
			int order = Integer.parseInt(context.readChildProperty("order"));
			
			String onCommand = context.readChildProperty("onCommand");
			
			String rootWidget = context.getWidget();
			out.println(rootWidget+".addCommand("+EscapeUtils.quote(id)+", "+label+", new "+
					WizardCommandHandler.class.getCanonicalName()+"<"+context.wizardObject+">(){");
			out.println("public void onCommand("+WizardCommandEvent.class.getCanonicalName()+"<"+context.wizardObject+"> event){");
			
			if (commandEvtBind == null) commandEvtBind = new WizardCommandEvtBind(getWidgetCreator());
			commandEvtBind.printEvtCall(out, onCommand, "event");

			out.println("}");
			out.println("}, "+order+");");
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				out.println(rootWidget+".getCommand("+EscapeUtils.quote(id)+").setStyleName("+EscapeUtils.quote(styleName)+");");
			}
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				out.println(rootWidget+".getCommand("+EscapeUtils.quote(id)+").setWidth("+EscapeUtils.quote(width)+");");
			}
			String height = context.readChildProperty("height");
			if (!StringUtils.isEmpty(height))
			{
				out.println(rootWidget+".getCommand("+EscapeUtils.quote(id)+").setHeight("+EscapeUtils.quote(height)+");");
			}
		}
	}
}