/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.widget.creator;

import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="pageSize", type=Integer.class, description="The number of widgets that is loaded from the datasource on each data request."),
})
public abstract class AbstractPageableFactory<C extends WidgetCreatorContext> extends HasPagedDataProviderFactory<C>
{
	protected void generateWidgetCreationForCell(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String widgetFactoryClassName = WidgetFactory.class.getCanonicalName()+"<"+dataObjectName+">";
		out.println("new " + widgetFactoryClassName + "(){");
		
		String childName = getChildName(child);
		
		if (childName.equals("widgetFactory"))
		{
			generateWidgetCreationForCellByTemplate(out, context, child, dataObject);
		}
		else if (childName.equals("widgetFactoryOnController"))
		{
			generateWidgetCreationForCellOnController(out, context, child, dataObject);
		}
		else
		{
        	throw new CruxGeneratorException("Invalid child tag on widget ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}
		
		out.println("};");
    }

	private void generateWidgetCreationForCellByTemplate(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
		child = ensureFirstChild(child, false, context.getWidgetId());
		String widgetClassName =  getChildWidgetClassName(child);
		JClassType widgetClassType =  getContext().getTypeOracle().findType(widgetClassName);
		WidgetConsumer widgetConsumer = new PageableWidgetConsumer(getContext(), widgetClassType, 
											dataObject, 
											"value", getView().getId(), context.getWidgetId());
		
		out.println("public "+IsWidget.class.getCanonicalName()+" createWidget("+dataObject.getParameterizedQualifiedSourceName()+" value){");
	    String childWidget = createChildWidget(out, child, widgetConsumer, true, context);
	    out.println("return "+childWidget+";");
	    out.println("}");
    }

	private void generateWidgetCreationForCellOnController(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
	    try
	    {
	        String onCreateWidget = child.getString("onCreateWidget");
	    	Event event = EventFactory.getEvent("onCreateWidget", onCreateWidget);
	    	if (event != null)
	    	{
	    		String controllerClass = getControllerAccessorHandler().getControllerImplClassName(event.getController(), getDevice());
	    		out.println("private "+controllerClass+" controller = " + getControllerAccessorHandler().getControllerExpression(event.getController(), getDevice())+";");
	    	}
	    	ControllerAccessHandler controllerAccessHandler = new ControllerAccessHandler()
	    	{
	    		@Override
	    		public String getControllerImplClassName(String controller, Device device)
	    		{
	    			return getControllerAccessorHandler().getControllerImplClassName(controller, device);
	    		}
	    		
	    		@Override
	    		public String getControllerExpression(String controller, Device device)
	    		{
	    			return "this.controller";
	    		}
	    	};
	    	
	    	String dataObjectClassName = dataObject.getParameterizedQualifiedSourceName();
			out.println("public "+IsWidget.class.getCanonicalName()+" createWidget("+dataObjectClassName+" value){");

	    	out.print("return ");
	    	EvtProcessor.printEvtCall(out, onCreateWidget, "onCreateWidget", dataObjectClassName, "value", 
	    			getContext(), getView(), controllerAccessHandler, getDevice(), false);
	    	
	    	out.println("}");
	    }
	    catch (JSONException e)
	    {
	    	throw new CruxGeneratorException("Missing required attribute [onCreateWidget], on widgetFactoryOnController tag on widget declaration. WidgetID ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
	    }
    }
	
	@TagChildren({
		@TagChild(WidgetFactoryChildCreator.class),
		@TagChild(WidgetFactoryControllerChildCreator.class)
	})
	public static class WidgetListChildCreator extends ChoiceChildProcessor<WidgetCreatorContext>{}
	
	@TagConstraints(tagName="widgetFactory", description="Describes the widget factory used by the widgetList. This factory is called to create a widget for each object provided by the datasource.")
	@TagChildren({
		@TagChild(value=WidgetFactoryWidgetProcessor.class, autoProcess=false)
	})
	public static class WidgetFactoryChildCreator extends WidgetChildProcessor<WidgetCreatorContext>{}
	
	public static class WidgetFactoryWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagConstraints(tagName="widgetFactoryOnController", description="Describes the widget factory method to be called on a controller to create widgets for this list. This factory is called to create a widget for each object provided by the datasource.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="onCreateWidget", required=true, description="")
	})
	public static class WidgetFactoryControllerChildCreator extends WidgetChildProcessor<WidgetCreatorContext>{}
}
