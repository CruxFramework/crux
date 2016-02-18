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

import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.permission.Permissions;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.binding.DataObjectBinder.UpdatedStateBindingContext;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.DataBindingProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDataProviderDataBindingProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.AttachEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.DettachEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.creator.event.LoadWidgetEvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.utils.ClassUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.PartialSupport;

/**
 * Generate code for gwt widgets creation. Generates code based on a JSON meta data array
 * containing the information declared on crux pages. 
 * 
 * @author Thiago da Rosa de Bustamante
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="id", required=true, supportsDataBinding=false,
							description="Sets the identifier used to reference this widget on the crux view. ")
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
	private Class<?> widgetClass = getWidgetTypeFromClass();
	
	/**
	 * Used by widgets that need to create new widgets as children. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @param consumer
	 * @param dataBindingProcessor
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createChildWidget(SourcePrinter out, JSONObject metaElem, String widgetId, 
			String widgetType, WidgetConsumer consumer, DataBindingProcessor dataBindingProcessor, WidgetCreatorContext context) throws CruxGeneratorException
	{
		WidgetConsumer widgetConsumer = consumer != null ? consumer : context.getWidgetConsumer();
		DataBindingProcessor bindingProcessor = dataBindingProcessor != null ? dataBindingProcessor : context.getDataBindingProcessor();
		return viewFactory.newWidget(out, metaElem, widgetId, widgetType, widgetConsumer, bindingProcessor);
	}	
	
	/**
	 * Used by widgets that need to create new widgets as children. 
	 * 
	 * @param out
	 * @param metaElem
	 * @param consumer
	 * @param dataBindingProcessor
	 * @param context
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createChildWidget(SourcePrinter out, JSONObject metaElem, WidgetConsumer consumer, 
			DataBindingProcessor dataBindingProcessor, WidgetCreatorContext context) throws CruxGeneratorException
	{
		if (!metaElem.has("id"))
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+getView().getId()+"], there is an widget of type ["+viewFactory.getMetaElementType(metaElem)+"] without id.");
		}
		String widgetId = metaElem.optString("id");
		return createChildWidget(out, metaElem, widgetId, viewFactory.getMetaElementType(metaElem), consumer, dataBindingProcessor, context);
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
		return createChildWidget(out, metaElem, null, null, context);
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
	 * @param dataBindingProcessor
	 * @return
	 * @throws CruxGeneratorException
	 */
	public String createWidget(SourcePrinter out, JSONObject metaElem, String widgetId, 
			WidgetConsumer consumer, DataBindingProcessor dataBindingProcessor) throws CruxGeneratorException
	{
		boolean partialSupport = hasPartialSupport();
		C context = createContext(out, metaElem, widgetId, consumer, dataBindingProcessor);
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
			if (dataBindingProcessor != null)
			{
				dataBindingProcessor.processBindings(out, context);
			}
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
			result = viewFactory.getViewHTML(result);
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
	 * 
	 * @param out
	 * @param bindingContextVariable
	 * @param bindableContainerVariable
	 */
	public void generateBindingContextDeclaration(SourcePrinter out, String bindingContextVariable, String bindableContainerVariable)
    {
	    String bindingContextClassName = UpdatedStateBindingContext.class.getCanonicalName();
		out.println(bindingContextClassName + " " + bindingContextVariable + " = new " + bindingContextClassName + "("+
			bindableContainerVariable + ", 0);");
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
	 * @param metaElem
	 * @return
	 */
	public String getChildWidgetClassName(JSONObject metaElem)
	{
		return viewFactory.getWidgetCreator(viewFactory.getMetaElementType(metaElem)).getWidgetClassName();
	}
	
	/**
	 * @return
	 */
	public RebindContext getContext()
	{
		return viewFactory.getContext();
	}
	
	public JType getDataBindingReadExpression(String resultVariable, String dataObjectAlias, String bindingContextVariable, 
		String propertyValue, Set<String> converterDeclarations, String widgetClassName, String widgetPropertyPath, 
		HasDataProviderDataBindingProcessor dataBindingProcessor, StringBuilder expression)
	{
		return getDataBindingReadExpression(resultVariable, dataObjectAlias, bindingContextVariable, 
				propertyValue, converterDeclarations, widgetClassName, widgetPropertyPath, dataBindingProcessor, expression, true);
	}

	public JType getDataBindingReadExpression(String resultVariable, String dataObjectAlias, String bindingContextVariable, 
		String propertyValue, Set<String> converterDeclarations, String widgetClassName, String widgetPropertyPath, 
		HasDataProviderDataBindingProcessor dataBindingProcessor, StringBuilder expression, boolean acceptExpressions)
	{
		JType result = null;
		
		PropertyBindInfo binding = getObjectDataBinding(propertyValue, widgetClassName, widgetPropertyPath, true, dataBindingProcessor);
		
		String dataObjectVariable = dataBindingProcessor.getCollectionDataObjectVariable();
		String collectionItemVariable = dataBindingProcessor.getCollectionItemVariable();
		if (binding != null)
		{
			expression.append(binding.getDataObjectReadExpression(bindingContextVariable, resultVariable, dataObjectVariable, collectionItemVariable));
			String converterDeclaration = binding.getConverterDeclaration();
			if (converterDeclaration != null)
			{
				converterDeclarations.add(converterDeclaration);
			}
			result = binding.getType();
		}
		else if (acceptExpressions)
		{
			ExpressionDataBinding expressionBinding = getExpressionDataBinding(propertyValue, widgetClassName, widgetPropertyPath, dataBindingProcessor);
			if (expressionBinding != null)
			{
				expression.append(expressionBinding.getExpression(resultVariable, bindingContextVariable, dataObjectVariable, collectionItemVariable));
				converterDeclarations.addAll(expressionBinding.getConverterDeclarations());
				result = expressionBinding.getType();
			}
			else
			{
				expression.append(getDeclaredMessage(propertyValue));
				result = getContext().getGeneratorContext().getTypeOracle().findType(String.class.getCanonicalName());
			}
		}
		return result;
	}
	
	public JClassType getDataObjectFromProvider(String dataProviderId)
	{
		return viewFactory.getDataObjectFromProvider(dataProviderId);
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
	 * @param text
	 * @return
	 */
	public String resolveI18NString(String text)
	{
		return viewFactory.resolveI18NString(text);
	}

	/**
	 * @return
	 */
	public Device getDevice()
	{
		return (viewFactory.getDevice() == null?null:Device.valueOf(viewFactory.getDevice()));
	}
	
	/**
	 * 
	 * @param propertyValue
	 * @param widgetPropertyPath
	 * @return
	 */
	public ExpressionDataBinding getExpressionDataBinding(String propertyValue, String widgetPropertyPath, DataBindingProcessor dataBindingProcessor)
	{
		return getExpressionDataBinding(propertyValue, getWidgetClassName(), widgetPropertyPath, dataBindingProcessor);
	}

	/**
	 * 
	 * @param propertyValue
	 * @param widgetClassName
	 * @param widgetPropertyPath
	 * @return
	 */
	public ExpressionDataBinding getExpressionDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath, 
		DataBindingProcessor dataBindingProcessor)
	{
		return getExpressionDataBinding(propertyValue, widgetClassName, widgetPropertyPath, null, null, dataBindingProcessor, null, null);
	}

	/**
	 * 
	 * @param propertyValue
	 * @param widgetClassName
	 * @param widgetPropertyPath
	 * @param uiObjectClassName
	 * @param getUiObjectExpression
	 * @param dataBindingProcessor
	 * @param setterMethod
	 * @param propertyTypeName
	 * @return
	 */
	public ExpressionDataBinding getExpressionDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath, 
							String uiObjectClassName, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor, 
							String setterMethod, String propertyTypeName)
	{
		return viewFactory.getExpressionDataBinding(propertyValue, widgetClassName, widgetPropertyPath, 
			uiObjectClassName, getUiObjectExpression, dataBindingProcessor, setterMethod, propertyTypeName);
	}
	
	/**
	 * @return
	 */
	public TreeLogger getLogger()
	{
		return viewFactory.getLogger();
	}

	/**
	 * 
	 * @param propertyValue
	 * @param widgetPropertyPath
	 * @param boundToAttribute
	 * @return
	 */
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetPropertyPath, boolean boundToAttribute, 
		DataBindingProcessor dataBindingProcessor)
	{
		return getObjectDataBinding(propertyValue, getWidgetClassName(), widgetPropertyPath, boundToAttribute, dataBindingProcessor);
	}
	
	/**
	 * 
	 * @param propertyValue
	 * @param widgetClassName
	 * @param widgetPropertyPath
	 * @param boundToAttribute
	 * @return
	 */
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath, 
											boolean boundToAttribute, DataBindingProcessor dataBindingProcessor)
	{
		return getObjectDataBinding(propertyValue, widgetClassName, widgetPropertyPath, boundToAttribute, null, null, dataBindingProcessor);
	}
	
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath, boolean boundToAttribute, 
												String uiObjectClassName, String getUiObjectExpression, DataBindingProcessor dataBindingProcessor)
	{
		return viewFactory.getObjectDataBinding(propertyValue, widgetClassName, widgetPropertyPath, boundToAttribute, uiObjectClassName, 
												getUiObjectExpression, dataBindingProcessor);
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
	 * @return
	 */
	public View getView()
	{
		return viewFactory.getView();
	}

	public Class<?> getWidgetClass()
    {
	    return widgetClass;
    }

	/**
	 * @return
	 */
	public Class<?> getWidgetClass(String widgetDeclaration)
    {
		WidgetCreator<?> widgetCreator = viewFactory.getWidgetCreator(widgetDeclaration);
		if (widgetCreator == null)
		{
			throw new CruxGeneratorException("No widget registered for declaration ["+widgetDeclaration+"]."); 
		}
		return widgetCreator.getWidgetClass();
    }

	/**
	 * @return
	 */
	public String getWidgetClassName()
    {
	    return getWidgetClass().getCanonicalName();
    }	
	
	
	/**
	 * 
	 * @return
	 */
	public JClassType getWidgetClassType()
    {
	    return getViewFactory().getContext().getGeneratorContext().getTypeOracle().findType(getWidgetClassName());
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
	    JClassType widgetClassType = getWidgetClassType();
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
	 * @param property
	 * @return
	 */
	public boolean isResourceReference(String property)
	{
		return viewFactory.isResourceReference(property);
	}

	/**
	 * 
	 * @param metaElem
	 * @return
	 */
    public boolean isWidget(JSONObject metaElem)
	{
		return ViewFactory.isValidWidget(metaElem);
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
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 * @param printer
	 */
	protected void commitPostProcessing(SourcePrinter printer)
	{
		viewFactory.commitPostProcessing(printer);
	}
	
	
	/**
	 * 
	 * @param out
	 * @param metaElem
	 * @param widgetId
	 * @param consumer
	 * @param dataBindingProcessor
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected C createContext(SourcePrinter out, JSONObject metaElem, String widgetId, 
		WidgetConsumer consumer, DataBindingProcessor dataBindingProcessor) throws CruxGeneratorException
	{
		C context = instantiateContext();
		context.setWidgetElement(metaElem);
		context.setWidgetId(widgetId);
		context.setChildElement(metaElem);
		context.setWidgetConsumer(consumer);
		context.setDataBindingProcessor(dataBindingProcessor);
		String widgetVariableName = createVariableName("widget");
		context.setWidget(widgetVariableName);
		context.pushWidgetComponent(getWidgetClassName(), widgetVariableName);

		instantiateWidget(out, context);
		if(consumer != null)
		{
			consumer.consume(out, widgetId, widgetVariableName, getWidgetFactoryDeclaration(), metaElem);
		}			
		return context;
	}
	
	/**
	 * Create a new post-processing scope
	 */
	protected void createPostProcessingScope()
	{
		viewFactory.createPostProcessingScope();
	}

	/**
	 * Retrieve the object responsible for print controller access expressions on client JS
	 * @return
	 */
	protected ControllerAccessHandler getControllerAccessorHandler()
	{
		return viewFactory.getControllerAccessHandler();
	}
	
	protected Map<String, String> getDeclaredMessages()
	{
		return viewFactory.getDeclaredMessages();
	}

	protected String getLoggerVariable()
	{
		return viewFactory.getLoggerVariable();
	}

	/**
     * Retrieve the current PostProcessingPrinter
	 * @return
	 */
	protected SourcePrinter getPostProcessingPrinter()
	{
		return viewFactory.getPostProcessingPrinter();
	}
	
	protected String getViewVariable()
	{
		return ViewFactoryCreator.getViewVariable();
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
	
	protected boolean isCurrentDeviceSupported(String size, String input)
    {
		Device current = Device.valueOf(viewFactory.getDevice());
		if (current.equals(Device.all))
		{
			return true;
		}
		if (size != null && size.length() > 0)
		{
			if (!current.getSize().toString().equals(size))
			{
				return false;
			}
		}
		if (input != null && input.length() > 0)
		{
			if (!current.getInput().toString().equals(input))
			{
				return false;
			}
		}
		
	    return true;
    }
	
	protected boolean isWidgetRegisteredForPostProcessing(String widgetId)
	{
		return viewFactory.isWidgetRegisteredForPostProcessing(widgetId);
	}
	
	/**
	 * Print code that will be executed after the viewFactory completes the widgets construction
	 * @param s code string
	 */
	protected void printlnPostProcessing(String s)
	{
		viewFactory.printlnPostProcessing(s);
	}
		
	protected void registerWidgetForPostProcessing(String widgetId)
	{
		viewFactory.registerWidgetForPostProcessing(widgetId);
	}
	
	protected boolean targetsDevice(JSONObject child)
    {
	    return viewFactory.targetsDevice(child);
    }
	
	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	TagConstraints getTagConstraints(Class<?> processorClass)
	{
		TagConstraints attributes = processorClass.getAnnotation(TagConstraints.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && superClass.getSuperclass() != null)
			{
				attributes = getTagConstraints(superClass);
			}
		}
		
		return attributes;
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
		if (annotationProcessor == null)
		{
			this.annotationProcessor = new WidgetCreatorAnnotationsProcessor(this);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Class<?> getWidgetTypeFromClass()
	{
		DeclarativeFactory declarativeFactory = getClass().getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.targetWidget();
		}
		else
		{
			return null;
		}
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
	
	public static class StyleNameProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public StyleNameProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, String widget, String styleName)
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
				        	addStyleName(out, widget, styleName, i > 0);
				        }
				        else
				        {
				        	addStyleName(out, widget, EscapeUtils.quote(styleName), i > 0);
				        }
					}
				} 
				else 
				{
					if (getWidgetCreator().isResourceReference(styleName))
			        {
			        	styleName = widgetCreator.getResourceAccessExpression(styleName);
			        	addStyleName(out, widget, styleName, false);
			        }
			        else
			        {
			        	addStyleName(out, widget, EscapeUtils.quote(styleName), false);
			        }
				}
			}
		}
		
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String styleName)
		{
			processAttribute(out, context.getWidget(), styleName);	
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
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class StyleProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public StyleProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		public void processAttribute(SourcePrinter out, String widget, String style)
		{
			String[] styleAttributes = style.split(";");
			if (styleAttributes.length > 0)
			{
				String element = ViewFactoryCreator.createVariableName("elem");
				out.println(Element.class.getCanonicalName() +" "+element+" = "+widget+".getElement();");
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
		
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String style)
		{
			processAttribute(out, context.getWidget(), style);
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
