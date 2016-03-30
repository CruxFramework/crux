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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.datasource.RegisteredDataSources;
import org.cruxframework.crux.core.client.formatter.RegisteredClientFormatters;
import org.cruxframework.crux.core.client.ioc.IocContainer;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.binding.DataObjectBinder;
import org.cruxframework.crux.core.client.screen.binding.NativeWrapper;
import org.cruxframework.crux.core.client.screen.views.BindableView;
import org.cruxframework.crux.core.client.screen.views.View.RenderCallback;
import org.cruxframework.crux.core.client.screen.views.ViewActivateEvent;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;
import org.cruxframework.crux.core.client.screen.views.ViewDeactivateEvent;
import org.cruxframework.crux.core.client.screen.views.ViewDeactivateHandler;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.screen.views.ViewLoadEvent;
import org.cruxframework.crux.core.client.screen.views.ViewLoadHandler;
import org.cruxframework.crux.core.client.screen.views.ViewPanel;
import org.cruxframework.crux.core.client.screen.views.ViewUnloadEvent;
import org.cruxframework.crux.core.client.screen.views.ViewUnloadHandler;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.ScriptTagHandler;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.declarativeui.ViewParser;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.context.scanner.ControllerScanner;
import org.cruxframework.crux.core.rebind.context.scanner.ResourceNotFoundException;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.controller.RegisteredControllersProxyCreator;
import org.cruxframework.crux.core.rebind.datasource.RegisteredDataSourcesProxyCreator;
import org.cruxframework.crux.core.rebind.formatter.RegisteredClientFormattersProxyCreator;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.rebind.screen.DataProvider;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.View.NativeDataBinding;
import org.cruxframework.crux.core.rebind.screen.resources.ResourcesHandlerProxyCreator;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.core.utils.RegexpPatterns;
import org.json.JSONObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.ext.CachedGeneratorResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryCreator extends AbstractProxyCreator
{
	private static Map<String, WidgetCreator<?>> creators = new HashMap<String, WidgetCreator<?>>();
	private static NameFactory nameFactory = new NameFactory();

	private static String viewVariable = "__view";
	protected ViewBindHandler bindHandler;
	protected ControllerAccessHandler controllerAccessHandler;
	protected String iocContainerClassName;
	protected View view;
	protected WidgetConsumer widgetConsumer;
	private Map<String, Boolean> attachToDOMFactories = new HashMap<String, Boolean>();
	private ViewDataBindingsProcessor dataBindingProcessor;
	private DataProviderHelper dataProviderHelper;
	private Map<String, String> declaredMessages = new HashMap<String, String>();
	private String device;
	private final LazyPanelFactory lazyFactory;
	private final Set<String> lazyPanels = new HashSet<String>();
	private String loggerVariable;
	private String nativeControllers;
	private final LinkedList<PostProcessingData> postProcessingCode = new LinkedList<PostProcessingData>();
	private Set<String> resources;
	private Set<String> rootPanelChildren;
	private boolean viewChanged;
	private String viewPanelInitializedVariable;
	private String viewPanelVariable;

	/**
	 * Constructor
	 *
	 * @param context
	 * @param logger
	 * @param view
	 * @param viewChanged 
	 * @param device
	 * @param module
	 */
	public ViewFactoryCreator(RebindContext context, View view, boolean viewChanged, String device)
    {
		super(context, true);
		this.view = view;
		this.viewChanged = viewChanged;
		this.device = device;
		this.lazyFactory = new LazyPanelFactory(this);
		this.loggerVariable = createVariableName("logger");
		this.viewPanelVariable = createVariableName("viewPanel");
		this.viewPanelInitializedVariable = createVariableName("viewPanelInitialized");
		this.widgetConsumer = new ViewWidgetConsumer(this);
		this.dataBindingProcessor = new ViewDataBindingsProcessor(this);
		this.dataProviderHelper = new DataProviderHelper(this);
		this.rootPanelChildren = new HashSet<String>();
		this.controllerAccessHandler = new DefaultControllerAccessor(viewVariable, context.getControllers());
		this.bindHandler = new ViewBindHandler(context, view, this);
    }
	
	/**
	 * Retrieve the variable name for the dataObjectBinder associated with the given alias.
	 * @param dataObjectAlias
	 * @param out
	 * @return
	 */
	public String getDataObjectBinderVariable(String dataObjectAlias, SourcePrinter out)
	{
		return bindHandler.getDataObjectBinderVariable(dataObjectAlias, out);
	}

	public JClassType getDataObjectFromProvider(String dataProviderId)
	{
		return dataProviderHelper.getDataObjectFromProvider(dataProviderId);
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
														String uiObjectClassName, String getUiObjectExpression, 
														DataBindingProcessor dataBindingProcessor, 
														String setterMethod, String propertyTypeName)
    {
		return bindHandler.getExpressionDataBinding(propertyValue, widgetClassName, 
						widgetPropertyPath, uiObjectClassName, getUiObjectExpression, dataBindingProcessor, setterMethod, propertyTypeName);
    }

	/**
	 * 
	 * @param propertyValue
	 * @param widgetClassName
	 * @param widgetPropertyPath
	 * @param boundToAttribute
	 * @param uiObjectClassName
	 * @param getUiObjectExpression
	 * @param dataBindingProcessor
	 * @return
	 */
	public PropertyBindInfo getObjectDataBinding(String propertyValue, String widgetClassName, String widgetPropertyPath, 
			boolean boundToAttribute, String uiObjectClassName, String getUiObjectExpression, 
			DataBindingProcessor dataBindingProcessor)
    {
	    return bindHandler.getObjectDataBinding(propertyValue, widgetClassName, widgetPropertyPath, boundToAttribute, 
	    										uiObjectClassName, getUiObjectExpression, dataBindingProcessor);
    }

	/**
	 * Return the qualified name of the ViewFactory class created for the associated screen
	 * @return
	 */
	public String getProxyQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getProxySimpleName();
    }

	/**
	 * Return the simple name of the ViewFactory class created for the associated screen
	 * @return
	 */
	public String getProxySimpleName()
    {
		String className = view.getId()+"_"+this.device;
		className = className.replaceAll("[\\W]", "_");
		return className;
    }

	/**
     * 
     * @return
     */
	public String getViewSuperClassName()
    {
	    if (isBindableView())
	    {
	    	return BindableView.class.getCanonicalName()+"<"+context.getDataObjects().getDataObject(view.getDataObject())+">";
	    }
	    return "View";
    }

	/**
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 *
	 * @param printer
	 */
	protected void commitPostProcessing(SourcePrinter printer)
	{
		PostProcessingData postProcessingData = this.postProcessingCode.removeLast();
		String postProcessingCode = postProcessingData.toString();
		if (!StringUtils.isEmpty(postProcessingCode))
		{
			printer.println(Scheduler.class.getCanonicalName()+".get().scheduleDeferred(new "+
					ScheduledCommand.class.getCanonicalName()+"(){");
			printer.println("public void execute(){");
			printer.print(postProcessingCode);
			printer.println("}");
			printer.println("});");
		}
	}

	/**
	 * Create a new scope for the post processing commands. All commands added by
	 * {@code printlnPostProcessing} method will be added to this same scope, what means
	 * that they will be fired together. When {@code commitPostProcessing} method is called,
	 * the scope is closed and all scope commands are programmed for execution.
	 */
	protected void createPostProcessingScope()
	{
		this.postProcessingCode.add(new PostProcessingData());
	}

	@Override
	protected boolean findCacheableImplementationAndMarkForReuseIfAvailable()
	{
		CachedGeneratorResult lastResult = context.getGeneratorContext().getCachedGeneratorResult();
		if (lastResult == null || !context.getGeneratorContext().isGeneratorResultCachingEnabled())
		{
			return false;
		}

		String proxyName = getProxyQualifiedName();

		// check that it is available for reuse
		if (!lastResult.isTypeCached(proxyName))
		{
			return false;
		}

		if (!viewChanged)
		{
			return context.getGeneratorContext().tryReuseTypeFromCache(proxyName);
		}
		return false;
	}

	protected void generateCreateDataObjectMethod(SourcePrinter printer)
    {
    	String dataObjectClass = context.getDataObjects().getDataObject(view.getDataObject());
		printer.println("protected "+ dataObjectClass +" createDataObject(){");
    	printer.println("return GWT.create("+dataObjectClass+".class);");
    	printer.println("}");
    }

    protected void generateGetIocContainerMethod(SourcePrinter printer)
    {
    	printer.println("public "+ IocContainer.class.getCanonicalName() +" getIocContainer(){");
    	printer.println("return iocContainer;");
    	printer.println("}");

    	printer.println("public "+ iocContainerClassName +" getTypedIocContainer(){");
    	printer.println("return iocContainer;");
    	printer.println("}");
    }
    
    protected void generateGetViewPanelMethod(SourcePrinter printer)
    {
    	printer.println("public "+ViewPanel.class.getCanonicalName()+" getViewPanel(){");
    	printer.println("return "+viewPanelVariable + ";");
    	printer.println("}");
    }

	/**
	 * Generate the View Constructor
	 */
	@Override
	protected void generateProxyContructor(SourcePrinter printer) throws CruxGeneratorException
	{
		String regsiteredControllersClass = new RegisteredControllersProxyCreator(context, view, iocContainerClassName, device).create();
		String regsiteredDataSourcesClass = new RegisteredDataSourcesProxyCreator(context, view, iocContainerClassName, device).create();
		String regsiteredFormattersClass = new RegisteredClientFormattersProxyCreator(context, view).create();

		printer.println("protected "+getProxySimpleName()+"(String id){");
		printer.println("super(id);");
		printer.println("setTitle("+resolveI18NString(view.getTitle())+");");
		printer.println("this.iocContainer = new "+iocContainerClassName+"(this);");
		printer.println("this.registeredControllers = new "+regsiteredControllersClass+"(this, iocContainer);");
		printer.println("this.registeredFormatters = new "+regsiteredFormattersClass+"();");
		printer.println("this.registeredDataSources = new "+regsiteredDataSourcesClass+"(this, iocContainer);");
		generateResources(printer);
		printer.println("new " + nativeControllers + "(this).init();");
		printer.println("}");
	}
    
    /**
	 * Generate the View fields
	 *
	 * @param printer
	 */
	protected void generateProxyFields(SourcePrinter printer)
    {

		printer.println("private "+RegisteredControllers.class.getCanonicalName()+" registeredControllers;");
		printer.println("private "+RegisteredDataSources.class.getCanonicalName()+" registeredDataSources;");
		printer.println("private "+RegisteredClientFormatters.class.getCanonicalName()+" registeredFormatters;");
		
		printer.println("protected "+ iocContainerClassName +" iocContainer;");

		for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
		printer.println("private final "+getViewSuperClassName()+" "+viewVariable+" = this;");
		printer.println("private static "+Logger.class.getCanonicalName()+" "+loggerVariable+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getProxySimpleName()+".class.getName());");
		printer.println("private "+ViewPanel.class.getCanonicalName()+" "+viewPanelVariable+" = null;");
		printer.println("private boolean "+viewPanelInitializedVariable+" = false;");
    }
    

    /**
	 * Generate the View methods.
	 *
     * @param printer
     */
    protected void generateProxyMethods(SourcePrinter printer)
    {
    	
    	if(printer != null)
    	{
	    	generateGetRegisteredControllersMethod(printer);
	    	generateGetRegisteredFormattersMethod(printer);
	    	generateCreateDataSourceMethod(printer);
	    	generateCreateWidgetsMethod(printer);
	    	generateRenderMethod(printer);
	    	generateUpdateDimensionsMethods(printer);
	    	generateInitializeLazyDependenciesMethod(printer);
	    	generateGetIocContainerMethod(printer);
	    	generateRegisterDataObjectBindersMethod(printer);
	    	generateGetViewPanelMethod(printer);
	    	if (isBindableView())
	    	{
	    		generateCreateDataObjectMethod(printer);
	    	}
    	}
    }

	protected void generateRegisterDataObjectBindersMethod(SourcePrinter printer)
    {
    	printer.println("protected void registerDataObjectBinders(){");

    	Iterator<String> dataObjects = bindHandler.iterateDataObjects();
    	while (dataObjects.hasNext())
    	{
    		String dataObjectAlias = dataObjects.next();
    		String dataObjectClass = context.getDataObjects().getDataObject(dataObjectAlias);
    		try
    		{
    			JClassType dataObjectType = context.getGeneratorContext().getTypeOracle().getType(dataObjectClass);
    			JClassType javaScriptObjectType = context.getGeneratorContext().getTypeOracle().findType(JavaScriptObject.class.getCanonicalName());

    			printer.println("addDataObjectBinder(new "+DataObjectBinder.class.getCanonicalName()+"<"+dataObjectClass+">(this){");
    			printer.println("protected "+dataObjectClass+" createDataObject() {");
    			if (dataObjectType.isAssignableTo(javaScriptObjectType))
    			{
    				printer.println(dataObjectClass+" obj = "+dataObjectClass+".createObject().cast();");
    				printer.println("return obj;");
    			}
    			else
    			{
    				printer.println("return GWT.create("+dataObjectClass+".class);");
    			}
    			printer.println("}");
    			printer.println("}, "+EscapeUtils.quote(dataObjectAlias)+");");
    		}
    		catch (NotFoundException e)
    		{
    			throw new CruxGeneratorException("DataObject class ["+dataObjectClass+"] not found", e);
    		}
    	}
		printer.println("}");
    }

	/**
	 * Create ClientBundles for the declared resources on View
	 * @param printer
	 */
	protected void generateResources(SourcePrinter printer)
    {
	   for (String resourceClass : resources)
	    {
	    	printer.println(resourceClass+".init();");
	    }
    }

	@Override
    protected void generateSubTypes(SourcePrinter srcWriter) throws CruxGeneratorException
    {
	    iocContainerClassName = new IocContainerRebind(context, view, device).create();
	    resources = new HashSet<String>();
	    Iterator<String> resources = view.iterateResources();
	    while (resources.hasNext())
	    {
	    	String resourceKey = resources.next();
	    	this.resources.add(new ResourcesHandlerProxyCreator(context, resourceKey, view, device).create());
	    }

	    nativeControllers = new NativeControllersProxyCreator(this).create();
    }

	/**
	 * @return
	 */
	protected RebindContext getContext()
	{
		return context;
	}

	/**
	 * Retrieve the object responsible for print controller access expressions on client JS
	 * @return
	 */
	protected ControllerAccessHandler getControllerAccessHandler()
	{
		return this.controllerAccessHandler;
	}

	/**
     * Gets the code necessary to access a i18n declared property or the own property, if
     * it is not in declarative i18n format.
     *
	 * @param property
	 * @return
	 */
	protected String getDeclaredMessage(String property)
    {
	    if (isKeyReference(property))
	    {
			if (isResourceReference(property))
			{
				return getResourceAccessExpression(property);
			}
			else
			{
				String[] messageParts = getKeyMessageParts(property);
				String messageClassName = context.getMessages().getMessageClass(messageParts[0]);

				// Checks if declared message is valid
				this.checkDeclaredMessage(messageClassName, messageParts[0], messageParts[1]);

				String messageVariable;

				if (!declaredMessages.containsKey(messageClassName))
				{
					messageVariable= createVariableName("mesg");
					declaredMessages.put(messageClassName, messageVariable);
				}
				else
				{
					messageVariable = declaredMessages.get(messageClassName);
				}
				return messageVariable+"."+messageParts[1]+"()";
			}
	    }
	    else
	    {
	    	return property==null?null:EscapeUtils.quote(property);
	    }
    }

	/**
	 * Gets all messages declared on this screen
	 * @return
	 */
	protected Map<String, String> getDeclaredMessages()
	{
		return declaredMessages;
	}

	/**
	 *
	 * @return
	 */
    protected String getDevice()
	{
		return this.device;
	}

	/**
	 * @return
	 */
	protected TreeLogger getLogger()
	{
		return context.getLogger();
	}
	
    /**
	 * Retrieves the logger variable name
	 * @return
	 */
	protected String getLoggerVariable()
	{
		return loggerVariable;
	}

	/**
	 * Return the type of a given crux meta tag. This type could be {@code "screen"} 
	 *  or another string referencing a registered {@code WidgetFactory}.
	 *
	 * @param cruxMetaElement
	 * @return
	 */
	protected String getMetaElementType(JSONObject cruxMetaElement)
	{
		return cruxMetaElement.optString("_type");
	}

	/**
     * Retrieve the current PostProcessingPrinter
     * @return
     */
    protected SourcePrinter getPostProcessingPrinter()
    {
    	return this.postProcessingCode.getLast().postProcessingPrinter;
    }

	/**
     * Gets the code necessary to access a resource property or the own property, if
     * it is not in a resource reference format.
     *
	 * @param property
	 * @return
	 */
	protected String getResourceAccessExpression(String property)
	{
	    if (isResourceReference(property))
	    {
	    	String[] resourceParts = getResourceParts(property);
	    	String resourceKey = resourceParts[0];
	    	String resourceProperty = resourceParts[1];
	    	String resourceClassName = context.getResources().getResource(resourceKey, Device.valueOf(device));
	    	
	    	if (!view.useResource(resourceKey))
	    	{
				throw new CruxGeneratorException("The resource ["+resourceKey+"] is not imported into view ["+view.getId()+"]. Use the useResource atribute of view tag to import it first.");
	    	}
	    	String resourceObject = "(("+resourceClassName+")getResource("+EscapeUtils.quote(resourceKey)+"))";
	    	
	    	StringBuilder out = new StringBuilder();
			JClassType resourceType = context.getGeneratorContext().getTypeOracle().findType(resourceClassName);
			if (resourceType == null)
			{
				String message = "Resource ["+resourceKey+"] , declared on view ["+view.getId()+"], could not be loaded. "
				   + "\n Possible causes:"
				   + "\n\t 1. Check if any type or subtype used by resource refers to another module and if this module is inherited in the .gwt.xml file."
				   + "\n\t 2. Check if your resource or its members belongs to a client package."
				   + "\n\t 3. Check the versions of all your modules."
				   ;
				throw new CruxGeneratorException(message);
			}
            try
            {
	            JClassUtils.buildGetValueExpression(out, resourceType, resourceProperty, resourceObject, false);
            }
            catch (NoSuchFieldException e)
            {
				throw new CruxGeneratorException("Resource ["+resourceKey+"] , declared on view ["+view.getId()+"], has an invalid expression ["+resourceProperty+"]", e);
            }
			return out.toString();
	    }
	    else
	    {
	    	return property==null?null:EscapeUtils.quote(property);
	    }
	}

	/**
	 * Creates and returns a new {@link SourceWriter}
     * @return a new {@link SourceWriter}
     */
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.getGeneratorContext().tryCreate(context.getLogger(), packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		composerFactory.setSuperclass(getViewSuperClassName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		return new SourceCodePrinter(composerFactory.createSourceWriter(context.getGeneratorContext(), printWriter), context.getLogger());
	}

	/**
	 * Create a new printer for a subType. That subType will be declared on the same package of the
	 * {@code ViewFactory}.
	 *
	 * @param packageName
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @param isInterface
     * @return
     */
    protected SourcePrinter getSubTypeWriter(String packageName, String subType, String superClass, String[] interfaces, String[] imports)
    {
    	return getSubTypeWriter(packageName, subType, superClass, interfaces, imports, false);
    }
	
	/**
	 * Create a new printer for a subType. That subType will be declared on the package name informed in the first parameter
	 * if packageName isEmpty, subType will be declared on the same package of the { @code ViewFactory }.
	 *
	 * @param packageName
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @param isInterface
     * @return
     */
    protected SourcePrinter getSubTypeWriter(String packageName, String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
    	if(StringUtils.isEmpty(packageName))
    	{
    		packageName = ViewFactory.class.getPackage().getName();
    	}
		PrintWriter printWriter = context.getGeneratorContext().tryCreate(context.getLogger(), packageName, subType);

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, subType);
		if (isInterface)
		{
			composerFactory.makeInterface();
		}

		if (imports != null)
		{
			for (String imp : imports)
			{
				composerFactory.addImport(imp);
			}
		}

		if (superClass != null)
		{
			composerFactory.setSuperclass(superClass);
		}

		if (interfaces != null)
		{
			for (String intf : interfaces)
			{
				composerFactory.addImplementedInterface(intf);
			}
		}

		return new SourceCodePrinter(composerFactory.createSourceWriter(context.getGeneratorContext(), printWriter), context.getLogger());
	}
	
	/**
	 * Create a new printer for a subType. That subType will be declared on the same package of the
	 * {@code ViewFactory}.
	 *
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @return
     */
    protected SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports)
    {
    	return getSubTypeWriter(subType, superClass, interfaces, imports, false);
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
    protected SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
    	return getSubTypeWriter(null, subType, superClass, interfaces, imports, isInterface);
    }

    /**
	 * @return
	 */
	protected View getView()
	{
		return this.view;
	}

    /**
	 * Process the given HTML code. It handles the placeholder div elements for crux widgets as any resource declaration.
	 * @param html
	 * @return
	 */
	protected String getViewHTML(String html)
    {
	    String result = resolveI18NString(html).replace(ViewParser.CRUX_VIEW_PREFIX, "\"+"+getViewVariable()+".getPrefix()+\"");
		return result;
    }

	/**
	 * Returns the creator of the widgets of the given type.
	 * @param widgetType
	 * @return the creator of the widgets of the given type.
	 */
	protected WidgetCreator<?> getWidgetCreator(String widgetType)
	{
		try
        {
	        if (!creators.containsKey(widgetType))
	        {
	        	String creatorClassName = WidgetLibraries.getInstance().getFactoryClass(widgetType);
	        	Class<?> widgetCreator = Class.forName(creatorClassName);
	        	WidgetCreator<?> factory = (WidgetCreator<?>) widgetCreator.newInstance();
	        	creators.put(widgetType, factory);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Error retrieveing widgetFactory for type ["+widgetType+"]."
					+ "\n Possible cause:"
					+ "\n\t 1. Check if your resource file (View, Template, Screen ...) is validated according to the Crux Catalog."
        			,e);
        }
		WidgetCreator<?> factory = creators.get(widgetType);
		factory.setViewFactory(this);
		return factory;
	}

    /**
	 * 
	 * @return
	 */
	protected boolean isBindableView()
	{
		return !StringUtils.isEmpty(view.getDataObject());
	}

	/**
     * Returns <code>true</code> if the given text is an internationalization key.
	 * @param text
	 * @return <code>true</code> if the given text is an internationalization key.
	 */
	protected boolean isKeyReference(String text)
	{
		return text!= null && RegexpPatterns.REGEXP_CRUX_MESSAGE.matcher(text).matches(); 
	}

    /**
     * Returns <code>true</code> if the given text is a reference to a resource.
	 * @param text
	 * @return <code>true</code> if the given text is a reference to a resource.
	 */
	protected boolean isResourceReference(String text)
	{
		if (text!= null &&  RegexpPatterns.REGEXP_CRUX_RESOURCE.matcher(text).matches())
		{
			String[] parts = getResourceParts(text);
			return (view.useResource(parts[0]));
		}
		return false;
	}
    
    /**
     * Check if the given widget is available for postProcessing 
     * @param widgetId widget identifier
     * @return true if available
     */
    protected boolean isWidgetRegisteredForPostProcessing(String widgetId)
    {
    	return this.postProcessingCode.getLast().referencedWidgets.contains(widgetId);
    }
    
    /**
	 * Creates a new widget based on its meta-data element.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType) throws CruxGeneratorException
	{
		return newWidget(printer, metaElem, widgetId, widgetType, this.widgetConsumer, this.dataBindingProcessor);
	}
    
    /**
	 * Creates a new widget based on its meta-data element.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetId
	 * @param consumer
	 * @param dataBindingProcessor
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType,
			WidgetConsumer consumer, DataBindingProcessor dataBindingProcessor)
				throws CruxGeneratorException
	{
		WidgetCreator<?> widgetFactory = getWidgetCreator(widgetType);
		if (widgetFactory == null)
		{
			throw new CruxGeneratorException("Can not found widgetFactory for type: ["+widgetType+"].");
		}

		String widget;
		if (consumer != null && consumer instanceof LazyCompatibleWidgetConsumer && mustRenderLazily(widgetType, metaElem, widgetId))
		{
			String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
			lazyPanels.add(lazyPanelId);
			widget = lazyFactory.getLazyPanel(printer, metaElem, widgetId, LazyPanelWrappingType.wrapWholeWidget);
			consumer.consume(printer, lazyPanelId, widget, widgetType, metaElem);
			((LazyCompatibleWidgetConsumer)consumer).handleLazyWholeWidgetCreation(printer, widgetId);
		}
		else
		{
			if (dataBindingProcessor == null)
			{
				dataBindingProcessor = this.dataBindingProcessor;
			}
			widget = widgetFactory.createWidget(printer, metaElem, widgetId, consumer, dataBindingProcessor);
		}
		if (widget == null)
		{
			throw new CruxGeneratorException("Can not create widget ["+widgetId+"]. Verify the widget type.");
		}

		return widget;
	}

    /**
	 * @param context
	 * @param changed 
     * @param view 
	 * @param device
	 */
    protected void prepare(RebindContext context, boolean changed, View view, String device)
	{
		this.context = context;
		this.view = view;
		this.lazyPanels.clear();
		this.declaredMessages.clear();
		this.postProcessingCode.clear();
		this.device = device;
		this.rootPanelChildren.clear();
		this.viewChanged = changed;
		if (context != null)
		{
			this.controllerAccessHandler = new DefaultControllerAccessor(viewVariable, context.getControllers());
			this.bindHandler = new ViewBindHandler(context, view, this);
		}
		else
		{
			this.controllerAccessHandler = null;
			this.bindHandler = null;
		}
	}
    
    /**
	 * Print code that will be executed after the viewFactory completes the widgets construction.
	 * Note that this code will be executed from inside a Command class. Any variable accessed in
	 * this code and declared outside need to be declared as final.
	 *
	 * @param s code string
	 */
    protected void printlnPostProcessing(String s)
	{
		this.postProcessingCode.getLast().postProcessingPrinter.println(s);
	}
    
	/**
	 * Generate the code for the View events creation
	 *
	 * @param printer
	 */
	protected void processViewEvents(SourcePrinter printer)
	{
		processHistoryChangedEvt(printer);
		processClosingEvt(printer);
		processCloseEvt(printer);
		processResizedEvt(printer);
		processLoadEvt(printer);
		processUnloadEvt(printer);
		processActivateEvt(printer);
		processDeactivateEvt(printer);
	}

	/**
     * Inform the factory that the given widget needs to be available for postProcessing
     * @param widgetId widget identifier
     */
    protected void registerWidgetForPostProcessing(String widgetId)
    {
    	this.postProcessingCode.getLast().referencedWidgets.add(widgetId);
    }

	/**
	 * 
	 * @param text
	 * @return
	 */
	protected String resolveI18NString(String text)
	{
		if (text == null)
		{
			return null;
		}
		Matcher matcher = RegexpPatterns.REGEXP_CRUX_MESSAGE.matcher(text);
	    int pos = 0;
	    StringBuilder result = new StringBuilder();
	    while (matcher.find())
	    {
	    	if (pos != matcher.start())
	    	{
	    		String literal = text.substring(pos, matcher.start());
			    if (result.length() > 0)
			    {
			    	result.append("+");
			    }
	    		result.append(EscapeUtils.quote(literal));
	    	}
	    	pos = matcher.end();
	    	String group = matcher.group();
		    if (result.length() > 0)
		    {
		    	result.append("+");
		    }
		    
		    result.append(getDeclaredMessage(group));
	    }	    	
	    
	    if (pos != text.length())
	    {
		    if (result.length() > 0)
		    {
		    	result.append("+");
		    }
			result.append(EscapeUtils.quote(text.substring(pos)));
	    }
	    
	    return result.toString();
	}

	protected boolean targetsDevice(JSONObject child)
    {
		Device device = (getDevice() == null?null:Device.valueOf(getDevice()));
		if (device == null)
		{
			return true;
		}
		
		String sizeAttr = child.optString("size");
		if (!StringUtils.isEmpty(sizeAttr))
		{
			Size size = Size.valueOf(sizeAttr);
			
			if (!size.equals(device.getSize()))
			{
				return false;
			}
		}
		
		String inputAttr = child.optString("input");
		if (!StringUtils.isEmpty(inputAttr))
		{
			Input input = Input.valueOf(sizeAttr);
			
			if (!input.equals(device.getInput()))
			{
				return false;
			}
		}
		
	    return true;
    }

	/**
	 * Gets the list of classes used by the ViewFactory.
	 *
     * @return
     */
    String[] getImports()
    {
        String[] imports = new String[] {
    		GWT.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName(),
    		StringUtils.class.getCanonicalName(),
    		Window.class.getCanonicalName(),
    		ViewFactoryUtils.class.getCanonicalName(),
    		ViewFactory.CreateCallback.class.getCanonicalName(),
    		RootPanel.class.getCanonicalName(),
    		RootLayoutPanel.class.getCanonicalName(),
    		Element.class.getCanonicalName(),
    		Node.class.getCanonicalName(),
    		ViewLoadEvent.class.getCanonicalName(),
    		Panel.class.getCanonicalName(),
    		InterfaceConfigException.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		Crux.class.getCanonicalName()
		};
	    return imports;
	}
	
	/**
	 * @return the widgetConsumer
	 */
	WidgetConsumer getScreenWidgetConsumer()
	{
		return widgetConsumer;
	}

	/**
	 * Checks if declared message is valid
	 * @param messageClassName
	 * @param messageKey
	 * @param messageMethod
	 * @throws CruxGeneratorException
	 */
	private void checkDeclaredMessage(String messageClassName, String messageKey, String messageMethod) throws CruxGeneratorException
	{
		if (StringUtils.isEmpty(messageClassName))
		{
			throw new CruxGeneratorException("Message ["+messageKey+"] , declared on view ["+view.getId()+"], not found.");
		}
		else
		{
			JClassType messageClass = context.getGeneratorContext().getTypeOracle().findType(messageClassName);
			if (messageClass == null)
			{
				String message = "Message class ["+messageKey+"] , declared on view ["+view.getId()+"], could not be loaded. "
							   + "\n Possible causes:"
							   + "\n\t 1. Check if any type or subtype used by message refers to another module and if this module is inherited in the .gwt.xml file."
							   + "\n\t 2. Check if your message or its members belongs to a client package."
							   + "\n\t 3. Check the versions of all your modules."
							   ;
				throw new CruxGeneratorException(message);
			}
			else
			{
				if (!hasMethod(messageClass, messageMethod, new JType[]{}))
				{
					throw new CruxGeneratorException("Method ["+messageMethod+"] of message ["+messageKey+"], declared on view ["+view.getId()+"], does not exist in message class ["+messageClassName+"].");
				}
			}
		}
	}
	
	/**
	 * Generate the code for a widget creation, based on its metadata.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetType
	 * @return
	 */
	private String createWidget(SourcePrinter printer, JSONObject metaElem, String widgetType)
	{
		if (!metaElem.has("id"))
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+view.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+view.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}

		widget = newWidget(printer, metaElem, widgetId, widgetType);
		if (isAttachToDOM(widgetType))
		{
			this.rootPanelChildren.add(widgetId);
		}
		return widget;
	}
	
	/**
	 * @param printer
	 */
	private void generateCreateDataSourceMethod(SourcePrinter printer)
    {
    	printer.println("public "+DataSource.class.getCanonicalName()+"<?> createDataSource(String dataSource){");
    	printer.println("return this.registeredDataSources.getDataSource(dataSource);");
    	printer.println("}");
    }
	
	/**
	 * @param printer
	 */
	private void generateCreateWidgetsMethod(SourcePrinter printer)
    {
	    createPostProcessingScope();
		printer.println("protected void createWidgets(){");

		String viewHTML = getViewHTML();
		printer.println("this."+viewPanelVariable+" = new " +
				ViewPanel.class.getCanonicalName() + "("+viewHTML+");");
		
		processViewEvents(printer);
		processViewDimensions(printer);
		
		generateDataProvidersCreationBlock(printer);
		generateWidgetsCreationBlock(printer);

		generateNativeDataBindingsBlock(printer);
		
		printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".fine(Crux.getMessages().viewContainerViewCreated(getId()));");
		printer.println("}");

		printer.println("}");
    }

	private void generateDataProvidersCreationBlock(SourcePrinter printer)
    {
	    Iterator<DataProvider> dataProviders = view.iterateDataProviders();
		while (dataProviders.hasNext())
		{
			DataProvider dataProvider = dataProviders.next();
			try
			{
				String dataProviderVariable = dataProviderHelper.createDataProvider(printer, dataProvider);
				printer.println("addDataProvider("+EscapeUtils.quote(dataProvider.getId())+", "+dataProviderVariable+");");
			}
			catch (Exception e)
			{
				throw new CruxGeneratorException("Error Creating dataProvider. See Log for more detail.", e);
			}
		}
    }

	/**
	 * @param printer
	 */
	private void generateGetRegisteredControllersMethod(SourcePrinter printer)
    {
    	printer.println("public "+RegisteredControllers.class.getCanonicalName()+" getRegisteredControllers(){");
    	printer.println("return this.registeredControllers;");
    	printer.println("}");
    }

	/**
	 * @param printer
	 */
	@Deprecated
	private void generateGetRegisteredFormattersMethod(SourcePrinter printer)
    {
    	printer.println("public "+RegisteredClientFormatters.class.getCanonicalName()+" getRegisteredFormatters(){");
    	printer.println("return this.registeredFormatters;");
    	printer.println("}");
    }

	/**
	 *
	 * @param printer
	 */
	private void generateInitializeLazyDependenciesMethod(SourcePrinter printer)
    {
    	printer.println("protected native "+org.cruxframework.crux.core.client.collection.Map.class.getCanonicalName()+"<String> initializeLazyDependencies()/*-{");
    	printer.println("return "+view.getLazyDependencies().toString()+";");
    	printer.println("}-*/;");
    }

	private void generateNativeDataBindingsBlock(SourcePrinter printer)
    {
	    Iterator<NativeDataBinding> nativeDataBindings = view.iterateNativeDataBindings();
	    Set<String> nativeWrappers = new HashSet<String>();
		while(nativeDataBindings.hasNext())
		{
			NativeDataBinding nativeDataBinding = nativeDataBindings.next();
			String nativeWrapper = createVariableName("nativeWrapper");
			String nativeWrapperClassName = NativeWrapper.class.getCanonicalName();
			if (!nativeWrappers.contains(nativeDataBinding.getElementId()))
			{
				printer.println(nativeWrapperClassName + " " + nativeWrapper + "= this."+viewPanelVariable+".wrapNative(" + 
								EscapeUtils.quote(nativeDataBinding.getElementId()) + ");");
				
				getScreenWidgetConsumer().consume(printer, nativeDataBinding.getElementId(), nativeWrapper, null, null);
				nativeWrappers.add(nativeDataBinding.getElementId());
			}
			PropertyBindInfo binding = getObjectDataBinding(nativeDataBinding.getBinding(), nativeWrapperClassName, 
										nativeDataBinding.getAttributeName(), !nativeDataBinding.getAttributeName().equals("value"), 
				null, null, this.dataBindingProcessor);
			WidgetCreatorContext ctx = new WidgetCreatorContext();
			ctx.setWidgetId(nativeDataBinding.getElementId());
			if (binding != null)
			{
				ctx.registerObjectDataBinding(binding);
			}
			else
			{
				ExpressionDataBinding expressionBinding = getExpressionDataBinding(nativeDataBinding.getBinding(), nativeWrapperClassName,
					nativeDataBinding.getAttributeName(), null, null,
					this.dataBindingProcessor, null, 
					DataBindingNativeTypeResolver.resolveTypeForProperty(nativeDataBinding.getAttributeName()).getType());
				if (expressionBinding != null)
				{
					ctx.registerExpressionDataBinding(expressionBinding);
				}
			}
			dataBindingProcessor.processBindings(printer, ctx);
		}
    }

	/**
	 * @param printer
	 */
	private void generateRenderMethod(SourcePrinter printer)
    {
	    String rootPanelVariable = createVariableName("rootPanel");
    	printer.println("protected void render("+Panel.class.getCanonicalName()+" "+rootPanelVariable+", final "+RenderCallback.class.getCanonicalName()+" renderCallback) throws InterfaceConfigException{");

		printer.println("if (!this."+viewPanelInitializedVariable+"){");
		String viewHTML = getViewHTML();
		
		boolean hasScript = viewHTML.indexOf("<script") >= 0;
		if (hasScript)
		{
			printer.println("this."+viewPanelVariable+".addAttachHandler(new "+Handler.class.getCanonicalName()+"(){");
			printer.println("private boolean scriptsProcessed = false;");
			printer.println("public void onAttachOrDetach("+AttachEvent.class.getCanonicalName()+" event){");
			printer.println("if (event.isAttached()){");
			printer.println("if (!scriptsProcessed){");
			printer.println(ScriptTagHandler.class.getCanonicalName()+".evaluateScripts("+getProxySimpleName()+".this."+viewPanelVariable+".getElement(), " +
					        "new " + ScriptTagHandler.ScriptLoadCallback.class.getCanonicalName()+"(){");
			printer.println("public void onLoaded(){");
			printer.println("renderCallback.onRendered();");
			printer.println("}");
			printer.println("});");
			printer.println("scriptsProcessed = true;");
			printer.println("} else {");
			printer.println("renderCallback.onRendered();");
			printer.println("}");
			printer.println("}");
			printer.println("}");
			printer.println("});");
		}
		
		printer.println(rootPanelVariable+".add(this."+viewPanelVariable+");");
    	for (String widgetId : rootPanelChildren)
        {
			String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
			String widgetViewId;
			
			if (lazyPanels.contains(lazyPanelId))
			{
				widgetViewId = lazyPanelId;
			}
			else
			{
				widgetViewId = widgetId;
			}
    		
    		printer.println("this."+viewPanelVariable+".addAndReplaceElement(widgets.get("+EscapeUtils.quote(widgetViewId)+"), " +
    				"ViewFactoryUtils.getEnclosingPanelId("+EscapeUtils.quote(widgetId)+", "+viewVariable+"));");
        }

		if (!hasScript)
		{
			printer.println("renderCallback.onRendered();");
		}

		commitPostProcessing(printer);
		
		printer.println("this."+viewPanelInitializedVariable+"=true;");
		printer.println("}");
		printer.println("else {");
		printer.println(rootPanelVariable+".add(this."+viewPanelVariable+");");
		if (!hasScript)
		{
			printer.println("renderCallback.onRendered();");
		}
		printer.println("}");

		printer.println("if(!StringUtils.isEmpty(this.width)){");
		printer.println("updateViewWidth(this.width);");
		printer.println("}");
		printer.println("if(!StringUtils.isEmpty(this.height)){");
		printer.println("updateViewHeight(this.height);");
		printer.println("}");
		
		
		printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".fine(Crux.getMessages().viewContainerViewRendered(getId()));");
		printer.println("}");

		printer.println("}");
    }

	private void generateUpdateDimensionsMethods(SourcePrinter printer)
    {
    	printer.println("protected void updateViewHeight(String height){");
		printer.println("if (this."+viewPanelVariable+" != null){");
		printer.println("this."+viewPanelVariable+".setHeight(height);");
		printer.println("}");
		printer.println("}");

		printer.println("protected void updateViewWidth(String width){");
		printer.println("if (this."+viewPanelVariable+" != null){");
		printer.println("this."+viewPanelVariable+".setWidth(width);");
		printer.println("}");
		printer.println("}");
    }

	private void generateWidgetsCreationBlock(SourcePrinter printer)
    {
	    Iterator<org.cruxframework.crux.core.rebind.screen.Widget> widgets = view.iterateWidgets();
		while (widgets.hasNext())
		{
			org.cruxframework.crux.core.rebind.screen.Widget widget = widgets.next();
			
			if (widget.getParent() == null)
			{
				JSONObject metaElement = widget.getMetadata();
				try
				{
					String type = getMetaElementType(metaElement);
					createWidget(printer, metaElement, type);
				}
				catch (Exception e)
				{
					throw new CruxGeneratorException("Error Creating widget. See Log for more detail.", e);
				}
			}
		}
    } 
	
	/**
	 * 
	 * @return
	 */
	private String getViewHTML()
    {
		return getViewHTML(view.getHtml());
    }
	
	/**
	 * Verifies if a method exists into a interface
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 */
	private boolean hasMethod(JClassType clazz, String methodName, JType[] params)
	{
		if (clazz != null && methodName != null)
		{
			JMethod method = clazz.findMethod(methodName, params);
			if (method != null)
			{
				return true;
			}

			JClassType[] interfaces = clazz.getImplementedInterfaces();
			if (interfaces != null)
			{
				for (JClassType intf : interfaces)
				{
					if (hasMethod(intf, methodName, params))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
     * Returns <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 * @param widgetType
	 * @return <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 */
	private boolean isAttachToDOM(String widgetType)
	{
		if (!attachToDOMFactories.containsKey(widgetType))
		{
			DeclarativeFactory declarativeFactory = getWidgetCreator(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			attachToDOMFactories.put(widgetType, declarativeFactory.attachToDOM());
		}
		return attachToDOMFactories.get(widgetType);
	}

	/**
	 * Returns <code>true</code> if the given widget should be rendered lazily
	 * @param widgetType
	 * @param metaElem
	 * @param widgetId
	 * @return <code>true</code> if the given widget should be rendered lazily
	 */
	private boolean mustRenderLazily(String widgetType, JSONObject metaElem, String widgetId)
	{
		Class<?> widgetClass = getWidgetCreator(widgetType).getWidgetClass();
		if (Panel.class.isAssignableFrom(widgetClass))
		{
			if (!metaElem.optBoolean("visible", true))
			{
				String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
				return !lazyPanels.contains(lazyPanelId);
			}
		}
		return false;
	}

	/**
	 * Processes the attach event.
	 *
	 * @param printer
	 */
	private void processActivateEvt(SourcePrinter printer)
	{
	    Event onActivate = view.getEvent("onActivate");
		if (onActivate != null)
		{
			printer.println(viewVariable+".addViewActivateHandler(new "+ViewActivateHandler.class.getCanonicalName()+"(){");
			printer.println("public void onActivate("+ViewActivateEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onActivate.getController()+"."+onActivate.getMethod(),
					"onActivate", ViewActivateEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

    /**
	 * Processes the close event.
	 *
	 * @param printer
	 */
	private void processCloseEvt(SourcePrinter printer)
    {
	    Event onClose = view.getEvent("onClose");
		if (onClose != null)
		{
			printer.println(viewVariable+".addWindowCloseHandler(new "+CloseHandler.class.getCanonicalName()+"<Window>(){");
			printer.println("public void onClose("+CloseEvent.class.getCanonicalName()+"<Window> event){");

			EvtProcessor.printEvtCall(printer, onClose.getController()+"."+onClose.getMethod(), "onClose",
					CloseEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the closing event.
	 *
	 * @param printer
	 */
	private void processClosingEvt(SourcePrinter printer)
    {
	    Event onClosing = view.getEvent("onClosing");
		if (onClosing != null)
		{
			printer.println(viewVariable+".addWindowClosingHandler(new Window.ClosingHandler(){");
			printer.println("public void onWindowClosing("+ClosingEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onClosing.getController()+"."+onClosing.getMethod(), "onClosing",
						ClosingEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }
	
	
	/**
	 * Processes the attach event.
	 *
	 * @param printer
	 */
	private void processDeactivateEvt(SourcePrinter printer)
	{
	    Event onDeactivate = view.getEvent("onDeactivate");
		if (onDeactivate != null)
		{
			printer.println(viewVariable+".addViewDeactivateHandler(new "+ViewDeactivateHandler.class.getCanonicalName()+"(){");
			printer.println("public void onDeactivate("+ViewDeactivateEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onDeactivate.getController()+"."+onDeactivate.getMethod(),
					"onDeactivate", ViewDeactivateEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the historyChanged event.
	 *
	 * @param printer
	 */
	private void processHistoryChangedEvt(SourcePrinter printer)
    {
	    Event onHistoryChanged = view.getEvent("onHistoryChanged");
		if (onHistoryChanged != null)
		{
			printer.println(viewVariable+".addWindowHistoryChangedHandler(new "+ValueChangeHandler.class.getCanonicalName()+"<String>(){");
			printer.println("public void onValueChange("+ValueChangeEvent.class.getCanonicalName()+"<String> event){");

			EvtProcessor.printEvtCall(printer, onHistoryChanged.getController()+"."+onHistoryChanged.getMethod(),
					"onHistoryChanged", ValueChangeEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the load event.
	 *
	 * @param printer
	 */
	private void processLoadEvt(SourcePrinter printer)
	{
	    Event onLoad = view.getEvent("onLoad");
		if (onLoad != null)
		{
			printer.println(viewVariable+".addViewLoadHandler(new "+ViewLoadHandler.class.getCanonicalName()+"(){");
			printer.println("public void onLoad("+ViewLoadEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onLoad.getController()+"."+onLoad.getMethod(),
					"onLoad", ViewLoadEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the resized event.
	 *
	 * @param printer
	 * @return
	 */
	private Event processResizedEvt(SourcePrinter printer)
    {
	    Event onResized = view.getEvent("onResized");
		if (onResized != null)
		{
			printer.println("screen.addResizeHandler(new "+ResizeHandler.class.getCanonicalName()+"(){");
			printer.println("public void onResize("+ResizeEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onResized.getController()+"."+onResized.getMethod(), "onResized",
					ResizeEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
	    return onResized;
    }
	
	/**
	 * Processes the unload event.
	 *
	 * @param printer
	 */
	private void processUnloadEvt(SourcePrinter printer)
	{
	    Event onUnload = view.getEvent("onUnload");
		if (onUnload != null)
		{
			printer.println(viewVariable+".addViewUnloadHandler(new "+ViewUnloadHandler.class.getCanonicalName()+"(){");
			printer.println("public void onUnload("+ViewUnloadEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onUnload.getController()+"."+onUnload.getMethod(),
					"onUnload", ViewUnloadEvent.class, "event", context, view, getControllerAccessHandler(), Device.valueOf(device));

			printer.println("}");
			printer.println("});");
		}
    }

	private void processViewDimensions(SourcePrinter printer)
    {
	    if (!StringUtils.isEmpty(this.view.getWidth()))
	    {
			printer.println("setWidth("+EscapeUtils.quote(this.view.getWidth())+");");
	    }
	    if (!StringUtils.isEmpty(this.view.getHeight()))
	    {
			printer.println("setHeight("+EscapeUtils.quote(this.view.getHeight())+");");
	    }
	    
    }

	/**
     * Creates a new unique name based off of{@code varName} and adds it to
     * the list of known names.
	 *
	 * @param varName
	 * @return
	 */
	public static String createVariableName(String varName)
	{
		return nameFactory.createName(varName);
	}
	
	/**
	 * Retrieve the name of the class responsible for native calls to controller methods.
	 * @param viewId
	 * @return
	 */
	public static String getViewNativeControllersType(String viewId, String device)
	{
		return viewId.replaceAll("\\W", "_") + "_NativeControllers_" + device;
	}
	
	/**
	 * Retrieves the view variable name
	 * @return
	 */
	public static String getViewVariable()
	{
		return viewVariable;
	}

	/**
	 * Split the i18n message and separate the messageClass alias from the message method
	 *
	 * @param text
	 * @return
	 */
	protected static String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}

    /**
	 * Split the resourceReference and separate the resourceClass alias from the requested property
	 *
	 * @param text
	 * @return
	 */
	protected static String[] getResourceParts(String text)
	{
		text = text.substring(2, text.length()-1);
		int index = text.indexOf('.');
		String[] result = new String[2];
		result[0] = text.substring(0, index);
		result[1] = text.substring(index+1, text.length());
		return result;
	}

    /**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface DataBindingProcessor
	{
		String getDataObjectAlias(String dataObject);
		String getNativeUiObjectExpression(String elementId);
		void processBindings(SourcePrinter out, WidgetCreatorContext context);
	}

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class EmptyWidgetConsumer implements WidgetConsumer
	{
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem)
		{
		}
	}

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface LazyCompatibleWidgetConsumer extends WidgetConsumer
	{
		void handleLazyWholeWidgetCreation(SourcePrinter out, String widgetId);
		void handleLazyWrapChildrenCreation(SourcePrinter out, String widgetId);
	}
	
    /**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface WidgetConsumer
	{
		public static EmptyWidgetConsumer EMPTY_WIDGET_CONSUMER = new EmptyWidgetConsumer();

		void consume(SourcePrinter out, String widgetId, String widgetVariableName, String widgetType, JSONObject metaElem);
	}

    private static class DefaultControllerAccessor implements ControllerAccessHandler
    {
		private ControllerScanner controllersScanner;
		private final String viewVariable;

		public DefaultControllerAccessor(String viewVariable, ControllerScanner controllersManager)
        {
			this.viewVariable = viewVariable;
			this.controllersScanner = controllersManager;
        }

		public String getControllerExpression(String controller, Device device)
        {

	        return "(("+getControllerImplClassName(controller, device)+")"+viewVariable+".getController("
			+EscapeUtils.quote(controller)+"))";
        }

		public String getControllerImplClassName(String controller, Device device)
        {
			String controllerClass;
            try
            {
	            controllerClass = controllersScanner.getController(controller, device);
            }
            catch (ResourceNotFoundException e)
            {
    			throw new CruxGeneratorException("Can not found the controller ["+controller+"]. Check your classpath and the inherit modules");
            }
	        return controllerClass + ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX;
        }
    }
    
    private static class PostProcessingData
    {
    	PostProcessingPrinter postProcessingPrinter = new PostProcessingPrinter();
    	Set<String> referencedWidgets = new HashSet<String>();
    	
    	@Override
    	public String toString()
    	{
    	    return postProcessingPrinter.toString();
    	}
    }
    
	/**
     * Printer for code that should be executed after the screen creation.
     *
     * @author Thiago da Rosa de Bustamante
     */
    private static class PostProcessingPrinter implements SourcePrinter
    {
		private StringBuilder builder = new StringBuilder();
		private String indentation = "";

    	@Override
        public void commit()
        {
	        // DO Nothing
        }

    	@Override
    	public void indent()
    	{
    		indentation+="\t";
    	}

    	@Override
    	public void outdent()
    	{
    		if (indentation.length() > 0)
    		{
    			indentation = indentation.substring(1);
    		}
    	}

    	@Override
        public void print(String s)
        {
    		builder.append(s);
    		if (s.equals("\n"))
    		{
    			builder.append(indentation);
    		}
        }

		@Override
        public void println()
        {
			print("\n");
        }

		@Override
    	public void println(String s)
    	{
    		String line = s.trim();

			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};") || line.endsWith("}-*/;"))
    		{
    			outdent();
    		}

    		builder.append(indentation+s+"\n");

    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}

		@Override
    	public String toString()
    	{
    		return builder.toString();
    	}
    }
}
