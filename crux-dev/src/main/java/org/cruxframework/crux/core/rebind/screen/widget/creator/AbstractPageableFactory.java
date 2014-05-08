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

import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.datasource.PagedDataSource;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.datasource.DataSources;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
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
import org.cruxframework.crux.core.utils.JClassUtils;
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
	@TagAttribute(value="dataSource", processor=AbstractPageableFactory.DataSourceAttributeParser.class, required=true, description="The datasource that provides data for this widget.")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="autoLoadData", type=Boolean.class, description="If true, ask bound datasource for data when widget is created.")
})
public abstract class AbstractPageableFactory<C extends WidgetCreatorContext> extends WidgetCreator<C>
{
	/**
	 * @param context
	 * @return
	 */
	protected JClassType getDataObject(WidgetCreatorContext context)
    {
		JClassType dtoType;
		
		JClassType dataSourceClass = getContext().getTypeOracle().findType(DataSources.getDataSource(context.readWidgetProperty("dataSource"), getDevice()));
		JClassType dataSourceInterface = getContext().getTypeOracle().findType(DataSource.class.getCanonicalName());
		dtoType = JClassUtils.getActualParameterTypes(dataSourceClass, dataSourceInterface)[0];
		
		return dtoType;
    }

	protected void generateWidgetCreationForCell(SourcePrinter out, C context, JSONObject child, JClassType dataObject)
    {
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String widgetFactoryClassName = WidgetFactory.class.getCanonicalName()+"<"+dataObjectName+">";
		out.println("new " + widgetFactoryClassName + "(){");
		
		String childName = getChildName(child);
		
		if (childName.equals("widgetFactory"))
		{
			generateWidgetCreationForCellByTemplate(out, context, child, dataObjectName);
		}
		else if (childName.equals("widgetFactoryOnController"))
		{
			generateWidgetCreationForCellOnController(out, context, child, dataObject, dataObjectName);
		}
		else
		{
        	throw new CruxGeneratorException("Invalid child tag on widget ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}
		
		out.println("};");
    }

	private void generateWidgetCreationForCellByTemplate(SourcePrinter out, C context, JSONObject child, String dataObjectName)
    {
		child = ensureFirstChild(child, false, context.getWidgetId());
		String widgetClassName =  getChildWidgetClassName(child);
		JClassType widgetClassType =  getContext().getTypeOracle().findType(widgetClassName);
		WidgetConsumer widgetConsumer = new PageableWidgetConsumer(getContext(), widgetClassType, getDataObject(context), "value", getView().getId(), context.getWidgetId());
		
		out.println("public "+IsWidget.class.getCanonicalName()+" createWidget("+dataObjectName+" value){");
	    String childWidget = createChildWidget(out, child, widgetConsumer, true, context);
	    out.println("return "+childWidget+";");
	    out.println("}");
    }

	private void generateWidgetCreationForCellOnController(SourcePrinter out, C context, JSONObject child, JClassType dataObject, String dataObjectName)
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
	    	
	    	out.println("public "+IsWidget.class.getCanonicalName()+" createWidget("+dataObjectName+" value){");

	    	out.print("return ");
	    	EvtProcessor.printEvtCall(out, onCreateWidget, "onCreateWidget", dataObject.getParameterizedQualifiedSourceName(), "value", 
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
	
	public static class DataSourceAttributeParser extends AttributeProcessor<WidgetCreatorContext>
	{
		public DataSourceAttributeParser(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String propertyValue)
		{
			JClassType dataSourceClass = getWidgetCreator().getContext().getTypeOracle().findType(DataSources.getDataSource(propertyValue, getWidgetCreator().getDevice()));
			JClassType dataSourceInterface = getWidgetCreator().getContext().getTypeOracle().findType(DataSource.class.getCanonicalName());
			JClassType dtoType = JClassUtils.getActualParameterTypes(dataSourceClass, dataSourceInterface)[0];

			String dtoClassName = dtoType.getParameterizedQualifiedSourceName();
			String className = PagedDataSource.class.getCanonicalName()+"<"+dtoClassName+">";
			String dataSource = getWidgetCreator().createVariableName("dataSource");
			out.println(className+" "+dataSource+" = ("+className+") "+getViewVariable()+".createDataSource("+EscapeUtils.quote(propertyValue)+");");

			boolean autoLoadData = context.readBooleanWidgetProperty("autoLoadData", false);
			
			String widget = context.getWidget();			
			out.println(widget+".setDataSource("+dataSource+", "+autoLoadData+");");
		}
	}	
}
