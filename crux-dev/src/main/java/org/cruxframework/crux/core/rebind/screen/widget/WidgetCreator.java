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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.Iterator;
import java.util.Map;

import org.cruxframework.crux.core.client.permission.Permissions;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.DataObjectBinder;
import org.cruxframework.crux.core.client.screen.views.ExpressionBinder;
import org.cruxframework.crux.core.client.screen.views.ExpressionBinder.BindingContext;
import org.cruxframework.crux.core.client.screen.views.PropertyBinder;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.core.declarativeui.ViewParser;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ObjectDataBinding.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.AttachEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.DettachEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadWidgetEvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.PartialSupport;

/**
 * Generate code for gwt widgets creation. Generates code based on a JSON meta data array
 * containing the information declared on crux pages. 
 * 
 * @author Thiago da Rosa de Bustamante
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="id", required=true, description="Sets the identifier used to reference this widget on the crux view. ")
})
@TagAttributes({
	@TagAttribute(value="width", description="Sets the object's width, in CSS units (e.g. \"10px\", \"1em\"). This width does not include decorations such as border, margin, and padding."),
	@TagAttribute(value="height", description="Sets the object's height, in CSS units (e.g. \"10px\", \"1em\"). This height does not include decorations such as border, margin, and padding."),
	@TagAttribute(value="styleName", processor=WidgetCreator.StyleNameProcessor.class, supportsResources=true, description="Clears all of the element's style names and sets it to the given style."),
	@TagAttribute(value="visible", type=Boolean.class, description="Sets whether this object is visible. true to show the object, false to hide it"),
	@TagAttribute(value="tooltip", supportsI18N=true, property="title", description="Sets the HTML title property associated with this object. The title is the 'tool-tip' displayed to users when they hover over the object."),
	@TagAttribute(value="style", processor=WidgetCreator.StyleProcessor.class, description="Sets the HTML style property of the widget's element"),
	@TagAttribute(value="viewPermission", type=String.class, processor=WidgetCreator.ViewPermissionAttributeProcessor.class, description="A role that must be checked to verify if user can see this widget on the Screen. You must define a RoleManager to handle these permission validations.")
})
@TagEvents({
	@TagEvent(value=LoadWidgetEvtProcessor.class, description="Inform the handler for onLoadWidget event. This event is fired when the widget is loaded into its parent view."),
	@TagEvent(value=AttachEvtBind.class, description="Inform the handler for onLoadWidget event. This event is fired every time the widget is attached to the DOM in the browser."),
	@TagEvent(value=DettachEvtBind.class, description="Inform the handler for onLoadWidget event. This event is fired every time the widget is removed from the DOM in the browser.")
})
public abstract class WidgetCreator <C extends WidgetCreatorContext>
{
	private WidgetCreatorAnnotationsProcessor annotationProcessor;
	private ViewFactoryCreator viewFactory = null;
	
	/**
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 * @throws CruxGeneratorException 
	 */
	public JSONArray ensureChildren(JSONObject metaElem, boolean acceptsNoChild, String parentWidgetId) throws CruxGeneratorException 
	{
		if (!acceptsNoChild && !metaElem.has("_children"))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain at least one child.");
		}
		
		JSONArray children = metaElem.optJSONArray("_children");
		if (acceptsNoChild && children == null)
		{
			return null;
		}

		if (!acceptsNoChild && (children == null || children.length() == 0 || children.opt(0)==null))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain at least one child.");
		}
		return children;
	}	
	
	/**
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 */
	public JSONObject ensureFirstChild(JSONObject metaElem, boolean acceptsNoChild, String parentWidgetId) throws CruxGeneratorException
	{
		if (!acceptsNoChild && !metaElem.has("_children"))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain at least one child.");
		}
		JSONArray children = metaElem.optJSONArray("_children");
		if (acceptsNoChild && children == null)
		{
			return null;
		}
		if (!acceptsNoChild && (children == null || children.length() == 0))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on view ["+getView().getId()+"], must contain at least one child.");
		}
		JSONObject firstChild = children.optJSONObject(0);
		if (!acceptsNoChild && firstChild == null)
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain at least one child.");
		}
		return firstChild;
	}
	
	/**
	 * 
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 * @throws CruxGeneratorException 
	 */
	public String ensureHtmlChild(JSONObject metaElem, boolean acceptsNoChild, String parentWidgetId) throws CruxGeneratorException
	{
		String result = metaElem.optString("_html");
		if (!acceptsNoChild && (result == null || result.length() == 0))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain an inner HTML.");
		}
		if (result != null)
		{
			result = EscapeUtils.quote(result).replace(ViewParser.CRUX_VIEW_PREFIX, "\"+"+getViewVariable()+".getPrefix()+\"");
		}
		return result;
	}
	
	/**
	 * 
	 * @param metaElem
	 * @param acceptsNoChild
	 * @return
	 * @throws CruxGeneratorException 
	 */
	public String ensureTextChild(JSONObject metaElem, boolean acceptsNoChild, String parentWidgetId, boolean addQuotes) throws CruxGeneratorException
	{
		String result = metaElem.optString("_text");
		if (!acceptsNoChild && (result == null || result.length() == 0))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain a text node child.");
		}
		if (result != null && addQuotes)
		{
			result = EscapeUtils.quote(result);
		}
		return result;
	}
	
	/**
	 * Retrieve the widget child element name
	 * @param childElement element representing the child
	 * @return child name
	 */
	public static String getChildName(JSONObject childElement)
	{
		return childElement.optString("_childTag");
	}

	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	public static boolean hasHeight(JSONObject metaElem)
	{
		if (!metaElem.has("height"))
		{
			return false;
		}
		String width = metaElem.optString("height");
		return width != null && (width.length() > 0);
	}
	
	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	public static boolean hasWidth(JSONObject metaElem)
	{
		if (!metaElem.has("width"))
		{
			return false;
		}
		String width = metaElem.optString("width");
		return width != null && (width.length() > 0);
	}

	/**
	 * @param metaElem
	 * @return
	 * @throws CruxGeneratorException
	 */
	public static boolean isHtmlChild(JSONObject metaElem) throws CruxGeneratorException
	{
		String result = metaElem.optString("_html");
		return (!StringUtils.isEmpty(result));
	}

	/**
	 * @param metaElem
	 * @return
	 * @throws CruxGeneratorException
	 */
	public static boolean isTextChild(JSONObject metaElem) throws CruxGeneratorException
	{
		String result = metaElem.optString("_text");
		return (!StringUtils.isEmpty(result));
	}	
	
	/**
	 * Used by widgets that need to create new widgets as children. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createChildWidget(SourcePrinter out, JSONObject metaElem, WidgetCreatorContext context) throws CruxGeneratorException
	{
		return createChildWidget(out, metaElem, null, true, context);
	}

	/**
	 * Used by widgets that need to create new widgets as children. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param consumer
	 * @param allowWrapperForCreatedWidget
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createChildWidget(SourcePrinter out, JSONObject metaElem, WidgetConsumer consumer, boolean allowWrapperForCreatedWidget, WidgetCreatorContext context) throws CruxGeneratorException
	{
		if (!metaElem.has("id"))
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+getView().getId()+"], there is an widget of type ["+viewFactory.getMetaElementType(metaElem)+"] without id.");
		}
		String widgetId = metaElem.optString("id");
		return createChildWidget(out, metaElem, widgetId, viewFactory.getMetaElementType(metaElem), consumer, allowWrapperForCreatedWidget, context);
	}		
	
	/**
	 * Used by widgets that need to create new widgets as children. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @param consumer
	 * @param allowWrapperForCreatedWidget
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createChildWidget(SourcePrinter out, JSONObject metaElem, String widgetId, 
			String widgetType, WidgetConsumer consumer, boolean allowWrapperForCreatedWidget, WidgetCreatorContext context) throws CruxGeneratorException
	{
		WidgetConsumer widgetConsumer = consumer != null ? consumer : context.getWidgetConsumer();
		return viewFactory.newWidget(out, metaElem, widgetId, widgetType, widgetConsumer, allowWrapperForCreatedWidget);
	}
	
	/**
	 * @param varName
	 * @return
	 */
	public String createVariableName(String varName)
	{
		return ViewFactoryCreator.createVariableName(varName);
	}
	
	/**
	 * Generates the code for the given widget creation. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param widgetId
	 * @param consumer
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createWidget(SourcePrinter out, JSONObject metaElem, String widgetId, WidgetConsumer consumer) throws CruxGeneratorException
	{
		boolean partialSupport = hasPartialSupport();
		C context = createContext(out, metaElem, widgetId, consumer);
		if (partialSupport)
		{
			out.println("if ("+getWidgetClassName()+".isSupported()){");
		}
		if (context != null)
		{
			processAttributes(out, context);
			annotationProcessor.processAttributes(out, context);
			processEvents(out, context);
			annotationProcessor.processEvents(out, context);
			processChildren(out, context);
			annotationProcessor.processChildren(out, context);
			context.setChildElement(context.getWidgetElement());
			processDataObjectBindings(out, context);
			processDataExpressionBindings(out, context);
			postProcess(out, context);
			if (partialSupport)
			{
				out.println("}");
			}
			return context.getWidget();
		}
		else
		{
			if (partialSupport)
			{
				out.println("}");
				out.println("else {");
				out.println("Crux.getErrorHandler().handleError(Crux.getMessages().viewContainerUnsupportedWidget());");
				out.println("}");
			}
			return null;
		}
	}

	/**
	 * 
	 * @param element
	 * @return
	 */
	public JSONObject ensureWidget(JSONObject metaElem, String parentWidgetId) 
	{
		if (!isWidget(metaElem))
		{
			throw new CruxGeneratorException("The widget ["+parentWidgetId+"], declared on View ["+getView().getId()+"], must contain a valid widget as child.");
		}
		return metaElem;
	}
	
	/**
	 * @param metaElem
	 * @return
	 */
	public String getChildWidgetClassName(JSONObject metaElem)
	{
		return viewFactory.getWidgetCreator(viewFactory.getMetaElementType(metaElem)).getWidgetClassName();
	}
	
	/**
	 * 
	 * @param metaElem
	 * @return
	 */
	public Class<?> getChildWidgetClass(JSONObject metaElem)
	{
		return viewFactory.getWidgetCreator(viewFactory.getMetaElementType(metaElem)).getWidgetClass();
	}
	
	/**
	 * @return
	 */
	public GeneratorContext getContext()
	{
		return viewFactory.getContext();
	}

	/**
	 * @param subType
	 * @param superClass
	 * @param interfaces
	 * @param imports
	 * @return
	 */
	public SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports)
	{
		return viewFactory.getSubTypeWriter(subType, superClass, interfaces, imports);
	}
	
	/**
	 * Create a new printer for a subType. That subType will be declared on the same package of the
	 * {@code ViewFactory}. 
	 * 
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @param isInterface
     * @return 
     */
    public SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
    	return viewFactory.getSubTypeWriter(subType, superClass, interfaces, imports, isInterface);
    }
    
    
    /**
	 * Create a new printer for a subType.  That subType will be declared on the package name informed in the first parameter
	 * 
	 * @param packageName
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @param isInterface
     * @return 
     */
    public SourcePrinter getSubTypeWriter(String packageName,String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
    	return viewFactory.getSubTypeWriter(packageName,subType, superClass, interfaces, imports, isInterface);
    }
	
	
	/**
	 * @param property
	 * @return
	 */
	public String getDeclaredMessage(String property)
	{
		return viewFactory.getDeclaredMessage(property);
	}
	
	/**
	 * 
	 * @param property
	 * @return
	 */
	public String getResourceAccessExpression(String property)
	{
		return viewFactory.getResourceAccessExpression(property);
	}
	
	/**
	 * 
	 * @param property
	 * @return
	 */
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetPropertyPath)
	{
		return getObjectDataBinding(propertyValue, getWidgetClassName(), widgetPropertyPath);
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath)
	{
		return viewFactory.getObjectDataBinding(propertyValue, widgetClassName, widgetPropertyPath);
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public boolean isResourceReference(String property)
	{
		return viewFactory.isResourceReference(property);
	}
	
	/**
	 * @return
	 */
	public TreeLogger getLogger()
	{
		return viewFactory.getLogger();
	}

	/**
	 * @return
	 */
	public View getView()
	{
		return viewFactory.getView();
	}
	
	/**
	 * @return
	 */
	public Device getDevice()
	{
		return (viewFactory.getDevice() == null?null:Device.valueOf(viewFactory.getDevice()));
	}

	/**
	 * @return
	 */
	public Class<?> getWidgetClass()
    {
	    return getWidgetClass(getWidgetFactoryDeclaration());
    }

	/**
	 * @return
	 */
	public Class<?> getWidgetClass(String widgetDeclaration)
    {
		WidgetCreatorHelper widgetCreatorHelper = viewFactory.getWidgetCreatorHelper(widgetDeclaration);
		if (widgetCreatorHelper == null)
		{
			throw new CruxGeneratorException("No widget registered for declaration ["+widgetDeclaration+"]."); 
		}
		return widgetCreatorHelper.getWidgetType();
    }

	/**
	 * @return
	 */
	public String getWidgetClassName()
    {
	    return getWidgetClass().getCanonicalName();
    }
	
	/**
	 * @return
	 */
	public String getWidgetFactoryDeclaration()
	{
		DeclarativeFactory declarativeFactory = getClass().getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.library()+"_"+declarativeFactory.id();
		}
		throw new CruxGeneratorException("Error reading viewFactory declaration."); 
	}
	
	/**
	 * @param metaElem
	 * @return
	 */
	public boolean hasChildPartialSupport(JSONObject metaElem)
	{
		return viewFactory.getWidgetCreator(viewFactory.getMetaElementType(metaElem)).hasPartialSupport();
	}
	
	/**
	 * @return
	 */
	public boolean hasPartialSupport()
    {
	    JClassType widgetClassType = getViewFactory().getContext().getTypeOracle().findType(getWidgetClassName());
		return widgetClassType != null && widgetClassType.getAnnotation(PartialSupport.class) != null 
	    	   && ClassUtils.hasMethod(getWidgetClass(), "isSupported");
    }
	
	/**
	 * @return
	 */
    public abstract C instantiateContext();
	
	
	/**
	 * @param out
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public void instantiateWidget(SourcePrinter out, C context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		if (hasPartialSupport() && ClassUtils.hasMethod(getWidgetClass(), "createIfSupported"))
		{
			out.println("final "+className + " " + context.getWidget()+" = "+className+".createIfSupported();");
		}
		else
		{
			out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
		}
	}
	
	/**
	 * 
	 * @param metaElem
	 * @return
	 */
    public boolean isWidget(JSONObject metaElem)
	{
		return viewFactory.isValidWidget(metaElem);
	}
	
	/**
	 * Process element children
	 * @param out 
	 * @param context
	 * @throws CruxGeneratorException 
	 */
	public void postProcess(SourcePrinter out, C context) throws CruxGeneratorException
	{
	}
	
	/**
	 * Process any dataObject binding on this widget
	 * @param out
	 * @param context
	 */
	protected void processDataObjectBindings(SourcePrinter out, C context)
    {
		Iterator<String> dataObjects = context.iterateDataObjects();
		
		while (dataObjects.hasNext())
		{
			String dataObjectAlias = dataObjects.next();
			ObjectDataBinding dataBindingInfo = context.getObjectDataBinding(dataObjectAlias);
			
			String dataObjectClassName = dataBindingInfo.getDataObjectClassName();
			String dataObjectBinder = createVariableName("dataObjectBinder");
			
			out.println(DataObjectBinder.class.getCanonicalName() + "<" + dataObjectClassName + "> " + dataObjectBinder + "=" + 
					getViewVariable() + ".getDataObjectBinder("+EscapeUtils.quote(dataObjectAlias)+");");

			Iterator<PropertyBindInfo> propertyBindings = dataBindingInfo.iterateBindings();
			
			while (propertyBindings.hasNext())
			{
				PropertyBindInfo bind = propertyBindings.next(); 
				out.println(dataObjectBinder + ".addPropertyBinder(" + EscapeUtils.quote(context.getWidgetId()) + 
						", new " + PropertyBinder.class.getCanonicalName() + "<" + dataObjectClassName + ", "+ bind.getWidgetClassName() +">(){");
				String converterDeclaration = bind.getConverterDeclaration();
				if (converterDeclaration != null)
				{
					out.println(converterDeclaration);
				}

				out.println("public void copyTo(" + dataObjectClassName + " dataObject){");
				out.println(bind.getWriteExpression("dataObject"));
				out.println("}");
				
				out.println("public void copyFrom(" + dataObjectClassName + " dataObject){");
				out.println(bind.getReadExpression("dataObject"));
				out.println("}");
				out.println("});");
			}
		}
    }
	
	/**
	 * Process any dataObject binding expression on this widget
	 * @param out
	 * @param context
	 */
	protected void processDataExpressionBindings(SourcePrinter out, C context)
    {
		Iterator<ExpressionDataBinding> expressionBindings = context.iterateExpressionBindings();
		
		while (expressionBindings.hasNext())
		{
			ExpressionDataBinding expressionBinding = expressionBindings.next();
			
			String expressionBinder = createVariableName("expressionBinder");
			out.println(ExpressionBinder.class.getCanonicalName() + " " + expressionBinder + " = "
					+ "new " + ExpressionBinder.class.getCanonicalName() + "(){");
			
			out.println("public void updateExpression(" + BindingContext.class.getCanonicalName() +" context, Widget w);{");
			out.print(expressionBinding.getWriteExpression("context", "w"));
			out.println("}");

			out.println("});");
			
			
			Iterator<String> dataObjects = expressionBinding.iterateDataObjects();
			
			while (dataObjects.hasNext())
			{
				String dataObjectAlias = dataObjects.next();
				out.println(getViewVariable() + ".getDataObjectBinder("+EscapeUtils.quote(dataObjectAlias)+")"
						+ ".addExpressionBinder(" + EscapeUtils.quote(context.getWidgetId()) + ", " + expressionBinder + ");");
			}
			
		}
    }
	
	/**
	 * Process widget attributes
	 * @param out 
	 * @param element page DOM element representing the widget (Its &lt;span&gt; tag)
	 * @throws CruxGeneratorException 
	 */
	public void processAttributes(SourcePrinter out, C context) throws CruxGeneratorException
	{
	}

	/**
	 * Process element children
	 * @param out 
	 * @param context
	 * @throws CruxGeneratorException 
	 */
	public void processChildren(SourcePrinter out, C context) throws CruxGeneratorException
	{
	}

	/**
	 * Process widget events
	 * @param out 
	 * @param context 
	 * @throws CruxGeneratorException
	 */
	public void processEvents(SourcePrinter out, C context) throws CruxGeneratorException
	{
	}
	
	/**
	 * @param srcWriter 
	 * @param element
	 * @param widgetId
	 * @param consumer
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected C createContext(SourcePrinter out, JSONObject metaElem, String widgetId, WidgetConsumer consumer) throws CruxGeneratorException
	{
		C context = instantiateContext();
		context.setWidgetElement(metaElem);
		context.setWidgetId(widgetId);
		context.setChildElement(metaElem);
		context.setWidgetConsumer(consumer);
		String widgetVariableName = createVariableName("widget");
		context.setWidget(widgetVariableName);

		instantiateWidget(out, context);
		if(consumer != null)
		{
			consumer.consume(out, widgetId, widgetVariableName, getWidgetFactoryDeclaration(), metaElem);
		}			
		return context;
	}

	/**
	 * 
	 * @param supported
	 * @return
	 */
	protected boolean isCurrentDeviceSupported(Device[] supported)
	{
		for (Device device : supported)
        {
			if (device.equals(Device.all))
			{
				return true;
			}
			if (viewFactory.getDevice().equals(device.toString()))
			{
				return true;
			}
        }
		return false;
	}
	
	/**
	 * Create a new post-processing scope
	 */
	protected void createPostProcessingScope()
	{
		viewFactory.createPostProcessingScope();
	}
	
	/**
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 * @param printer
	 */
	protected void commitPostProcessing(SourcePrinter printer)
	{
		viewFactory.commitPostProcessing(printer);
	}
	
	/**
	 * Print code that will be executed after the viewFactory completes the widgets construction
	 * @param s code string
	 */
	protected void printlnPostProcessing(String s)
	{
		viewFactory.printlnPostProcessing(s);
	}

	/**
	 * Retrieve the object responsible for print controller access expressions on client JS
	 * @return
	 */
	protected ControllerAccessHandler getControllerAccessorHandler()
	{
		return viewFactory.getControllerAccessHandler();
	}
	
	/**
	 * @return
	 */
	ViewFactoryCreator getViewFactory()
	{
		return this.viewFactory;
	}
	
	/**
	 * @param viewFactory
	 */
	void setViewFactory(ViewFactoryCreator factory)
	{
		this.viewFactory = factory;
		this.annotationProcessor = new WidgetCreatorAnnotationsProcessor(getClass(), this);
	}
	
	protected String getLoggerVariable()
	{
		return viewFactory.getLoggerVariable();
	}
	
	protected String getViewVariable()
	{
		return ViewFactoryCreator.getViewVariable();
	}
	
	protected Map<String, String> getDeclaredMessages()
	{
		return viewFactory.getDeclaredMessages();
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class StyleProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public StyleProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String style)
		{
			String[] styleAttributes = style.split(";");
			if (styleAttributes.length > 0)
			{
				String element = ViewFactoryCreator.createVariableName("elem");
				out.println(Element.class.getCanonicalName() +" "+element+" = "+context.getWidget()+".getElement();");
				for (int i=0; i<styleAttributes.length; i++)
				{
					String[] attr = styleAttributes[i].split(":");
					if (attr != null && attr.length == 2)
					{
						out.println(StyleUtils.class.getCanonicalName()+".addStyleProperty("+element+", "+EscapeUtils.quote(getStylePropertyName(attr[0]))+
								", "+EscapeUtils.quote(attr[1])+");");
					}
				}
			}
		}
		
		private String getStylePropertyName(String property)
		{
			int index = -1;
			while ((index = property.indexOf('-')) >0)
			{
				if (index < property.length()-1)
				{
					property = property.substring(0, index) + Character.toUpperCase(property.charAt(index+1)) + property.substring(index+2);
				}
			}
			return property;
		}
	}
	
	public static class StyleNameProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public StyleNameProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String styleName)
		{
			if(!StringUtils.isEmpty(styleName))
			{
				String[] classAttributes = styleName.split(" ");
				if (classAttributes.length > 1)
				{
					for (int i=0; i<classAttributes.length; i++)
					{
						styleName = classAttributes[i];
						if (getWidgetCreator().isResourceReference(styleName))
				        {
				        	styleName = widgetCreator.getResourceAccessExpression(styleName);
				        	addStyleName(out, context.getWidget(), styleName, i > 0);
				        }
				        else
				        {
				        	addStyleName(out, context.getWidget(), EscapeUtils.quote(styleName), i > 0);
				        }
					}
				} 
				else 
				{
					if (getWidgetCreator().isResourceReference(styleName))
			        {
			        	styleName = widgetCreator.getResourceAccessExpression(styleName);
			        	addStyleName(out, context.getWidget(), styleName, false);
			        }
			        else
			        {
			        	addStyleName(out, context.getWidget(), EscapeUtils.quote(styleName), false);
			        }
				}
			}
		}
		
		private void addStyleName(SourcePrinter out, String widgetName, String valueExpr, boolean add)
		{
			if (add)
			{
				out.println(widgetName+".setStyleName("+valueExpr+", true);");
			}
			else
			{
				out.println(widgetName+".setStyleName("+valueExpr+");");
			}
		}
	}
	
	/**
	 * Process viewPermission attribute
	 * @author Thiago da Rosa de Bustamante
	 *
	 */	
	public static class ViewPermissionAttributeProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public ViewPermissionAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
			out.println("if (!"+Permissions.class.getCanonicalName()+".hasRole("+EscapeUtils.quote(attributeValue)+")){");
			out.println(Permissions.class.getCanonicalName()+".markAsUnauthorizedForViewing("+context.getWidget()+");");
			out.println("}");
        }
	}
}
