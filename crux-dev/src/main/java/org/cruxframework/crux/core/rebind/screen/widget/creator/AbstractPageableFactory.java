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

import java.util.Set;

import org.cruxframework.crux.core.client.dto.DataObject;
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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
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
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="itemVar", required=true)
})
public abstract class AbstractPageableFactory<C extends WidgetCreatorContext> extends HasPagedDataProviderFactory<C>
{
	protected HasDataProviderDataBindingProcessor createDataBindingProcessor(C context, JClassType dataObject, String bindingContextVariable)
    {
	    String itemVar = context.readWidgetProperty("itemVar");
		String collectionObjectReference = "_value";
		DataObject dataObjectAnnotation = dataObject.getAnnotation(DataObject.class);
		if (dataObjectAnnotation == null)
		{
			throw new CruxGeneratorException("Invalid dataObject: "+dataObject.getQualifiedSourceName());
		}
		String dataObjectAlias = dataObjectAnnotation.value();
		if (dataObjectAlias == null)
		{
			throw new CruxGeneratorException("Invalid itemVariable on widget ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}
		HasDataProviderDataBindingProcessor bindingProcessor = new HasDataProviderDataBindingProcessor(getContext(), 
			bindingContextVariable, collectionObjectReference, dataObjectAlias, itemVar);
	    return bindingProcessor;
    }

	protected boolean generateWidgetCreationForCell(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String widgetFactoryClassName = WidgetFactory.class.getCanonicalName()+"<"+dataObjectName+">";
		
		String childName = getChildName(child);
		
		if (childName.equals("widget"))
		{
			out.println("new " + widgetFactoryClassName + "(){");
			String bindingContextVariable = "_context";
		    generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());
			Set<String> converterDeclarations = generateWidgetCreationForCellByTemplate(out, context, child, dataObject, bindingContextVariable);
		    for (String converterDeclaration : converterDeclarations)
	        {
		    	out.println(converterDeclaration);
		    }
			out.println("};");
		}
		else if (childName.equals("widgetFactory"))
		{
			out.println("new " + widgetFactoryClassName + "(){");
			generateWidgetCreationForCellOnController(out, context, child, dataObject);
			out.println("};");
		}
		else
		{
        	return false;
		}
		return true;
    }

	/**
	 * Generate the createWidget method and return the set of converter declarations used by the generated method
	 * @param out
	 * @param context
	 * @param child
	 * @param dataObject
	 * @param bindingContextVariable
	 * @return
	 */
	protected Set<String> generateWidgetCreationForCellByTemplate(SourcePrinter out, C context, JSONObject child, 
		JClassType dataObject, String bindingContextVariable)
	{
		HasDataProviderDataBindingProcessor bindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);
		
		return generateWidgetCreationForCellByTemplate(out, context, child, dataObject, bindingContextVariable, bindingProcessor);
	}

	/**
	 * Generate the createWidget method and return the set of converter declarations used by the generated method
	 * @param out
	 * @param context
	 * @param child
	 * @param dataObject
	 * @param bindingContextVariable
	 * @return
	 */
	protected Set<String> generateWidgetCreationForCellByTemplate(SourcePrinter out, C context, JSONObject child, 
						JClassType dataObject, String bindingContextVariable, HasDataProviderDataBindingProcessor bindingProcessor)
    {
		child = ensureFirstChild(child, false, context.getWidgetId());

		out.println("public "+IsWidget.class.getCanonicalName()+" createWidget("+dataObject.getParameterizedQualifiedSourceName()
				    +" "+bindingProcessor.getCollectionObjectReference()+"){");
	    String childWidget = createChildWidget(out, child, WidgetConsumer.EMPTY_WIDGET_CONSUMER, bindingProcessor, context);
	    out.println("return "+childWidget+";");
	    out.println("}");
	    
	    return bindingProcessor.getConverterDeclarations();
    }

	protected void generateWidgetCreationForCellOnController(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
	    try
	    {
	        String onCreateWidget = child.getString("onCreateWidget");
	    	Event event = EventFactory.getEvent("onCreateWidget", onCreateWidget);
	    	if (event != null)
	    	{
	    		String controllerClass = getControllerAccessorHandler().getControllerImplClassName(event.getController(), getDevice());
	    		out.println("private "+controllerClass+" controller = " + getControllerAccessorHandler().getControllerExpression(
	    																	event.getController(), getDevice())+";");
	    	}
	    	ControllerAccessHandler controllerAccessHandler = new ControllerAccessHandler()
	    	{
	    		@Override
	    		public String getControllerExpression(String controller, Device device)
	    		{
	    			return "this.controller";
	    		}
	    		
	    		@Override
	    		public String getControllerImplClassName(String controller, Device device)
	    		{
	    			return getControllerAccessorHandler().getControllerImplClassName(controller, device);
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
	    	throw new CruxGeneratorException("Missing required attribute [onCreateWidget], on widgetFactoryOnController "
	    		+ "tag on widget declaration. WidgetID ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
	    }
    }
	
	@TagConstraints(tagName="widget", 
		description="Describes the widget used by the widgetList. An Widget like this will be created for each object "
			+ "provided by the dataprovider.")
	@TagChildren({
		@TagChild(value=WidgetFactoryWidgetProcessor.class, autoProcess=false)
	})
	public static class WidgetFactoryChildCreator extends WidgetChildProcessor<WidgetCreatorContext>{}
	
	@TagConstraints(tagName="widgetFactory", description="Describes the widget factory method to be called on a controller "
		+ "to create widgets for this list. This factory is called to create a widget for each object provided by the dataprovider.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="onCreateWidget", required=true, description="")
	})
	public static class WidgetFactoryControllerChildCreator extends WidgetChildProcessor<WidgetCreatorContext>{}
	
	public static class WidgetFactoryWidgetProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildren({
		@TagChild(WidgetFactoryChildCreator.class),
		@TagChild(WidgetFactoryControllerChildCreator.class)
	})
	public static class WidgetListChildCreator extends ChoiceChildProcessor<WidgetCreatorContext>{}
}
