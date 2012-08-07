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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceDisplayHandler;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.ScreenFactory;
import org.cruxframework.crux.core.client.screen.ScreenLoadEvent;
import org.cruxframework.crux.core.client.screen.ViewFactory;
import org.cruxframework.crux.core.client.screen.ViewFactoryUtils;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.eventadapter.TapEventAdapter;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.i18n.MessageClasses;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.controller.RegisteredControllersProxyCreator;
import org.cruxframework.crux.core.rebind.datasource.RegisteredDataSourcesProxyCreator;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryCreator
{
	private static NameFactory nameFactory = new NameFactory();

	private Map<String, Boolean> attachToDOMfactories = new HashMap<String, Boolean>();
	private GeneratorContextExt context;
	private Map<String, String> declaredMessages = new HashMap<String, String>();
	private Map<String, WidgetCreator<?>> creators = new HashMap<String, WidgetCreator<?>>();
	private Map<String, WidgetCreatorHelper> creatorHelpers = new HashMap<String, WidgetCreatorHelper>();
	private Map<String, Boolean> htmlContainersfactories = new HashMap<String, Boolean>();
	private final LazyPanelFactory lazyFactory;
	private final Set<String> lazyPanels = new HashSet<String>();	
    private TreeLogger logger;
	private final LinkedList<PostProcessingPrinter> postProcessingCode = new LinkedList<PostProcessingPrinter>();
	private final Screen screen;
	private String screenVariable;
	private String loggerVariable;
	private String device;
	protected ControllerAccessHandler controllerAccessHandler = new DefaultControllerAccessor();
	protected WidgetConsumer screenWidgetConsumer;
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface WidgetConsumer 
	{
		public static EmptyWidgetConsumer EMPTY_WIDGET_CONSUMER = new EmptyWidgetConsumer();

		void consume(SourcePrinter out, String widgetId, String widgetVariableName);
	}
	
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
	private static class EmptyWidgetConsumer implements WidgetConsumer
	{
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName) 
		{
		}
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public class ScreenWidgetConsumer implements LazyCompatibleWidgetConsumer
	{
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName) 
		{
			out.println(getScreenVariable()+".addWidget("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +");");
			if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().renderWidgetsWithIDs()))
			{
				out.println("ViewFactoryUtils.updateWidgetElementId("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +");");
			}
		}

		@Override
        public void handleLazyWholeWidgetCreation(SourcePrinter out, String widgetId)
        {
			out.println(getScreenVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+ 
					EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget)) +");");
        }

		@Override
        public void handleLazyWrapChildrenCreation(SourcePrinter out, String widgetId)
        {
			out.println(getScreenVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+ 
					EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapChildren)) +");");
        }
	}
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param logger
	 * @param screen
	 */
	public ViewFactoryCreator(GeneratorContextExt context, TreeLogger logger, Screen screen, String device)
    {
		this.logger = logger;
		this.context = context;
		this.screen = screen;
		this.device = device;
		this.lazyFactory = new LazyPanelFactory(this);
		this.screenVariable = createVariableName("screen");
		this.loggerVariable = createVariableName("logger");
		this.screenWidgetConsumer = new ScreenWidgetConsumer();

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
	 * Creates the ViewFactory class.
	 * 
	 * @return generated class name .
	 * @throws CruxGeneratorException 
	 */
	public String create() throws CruxGeneratorException
	{
		SourceWriter sourceWriter = getSourceWriter();
		if (sourceWriter == null)
		{
			return getQualifiedName();
		}
		SourcePrinter printer = new SourcePrinter(sourceWriter, logger);

		generateProxyMethods(printer);
		generateProxyFields(printer);

		printer.commit();
		return getQualifiedName();
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
	 * Generate the code for the Screen creation
	 * 
	 * @param printer 
	 */
	protected void createScreen(SourcePrinter printer) 
	{
		if (screen.getTitle() != null && screen.getTitle().length() >0)
		{
			printer.println("Window.setTitle("+getDeclaredMessage(screen.getTitle())+");" );
		}

		printer.println("final Screen "+screenVariable+" = Screen.get();");
		
		//TODO: alterar os evtBinder para que criem uma unica subclasse de tratamento por tipo de evento... (tipo... um unico ClickHandler)
		
		createHistoryChangedEvt(printer);
		createClosingEvt(printer);
		createCloseEvt(printer);
		createResizedEvt(printer);		
		createLoadEvt();
	}	
	
	/**
	 * Generate the ViewFactory fields
	 * 
	 * @param printer
	 */
	protected void generateProxyFields(SourcePrinter printer)
    {
	    for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
	    printer.println("private static "+Logger.class.getCanonicalName()+" "+loggerVariable+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getSimpleName()+".class.getName());");
    }
	
	/**
	 * Generate the ViewFactory methods.
	 * 
     * @param printer 
     */
    protected void generateProxyMethods(SourcePrinter printer) 
    {
    	generateCreateRegisteredControllersMethod(printer);
    	generateCreateRegisteredDataSourcesMethod(printer);
    	generateCreateMethod(printer);
    }
	
	/**
	 * Return the type of a given crux meta tag. This type could be {@code "screen"} or 
	 * another string referencing a registered {@code WidgetFactory}.
	 * 
	 * @param cruxMetaElement
	 * @return
	 */
	protected String getMetaElementType(JSONObject cruxMetaElement) 
	{
		return cruxMetaElement.optString("_type");
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
	        	String creatorClassName = WidgetConfig.getClientClass(widgetType);
	        	Class<?> widgetCreator = Class.forName(creatorClassName);
	        	WidgetCreator<?> factory = (WidgetCreator<?>) widgetCreator.newInstance();
	        	factory.setViewFactory(this);
	        	creators.put(widgetType, factory);
	        	WidgetCreatorHelper creatorHelper = new WidgetCreatorHelper(widgetCreator);
				creatorHelpers.put(widgetType, creatorHelper);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Error retrieveing widgetFactory for type ["+widgetType+"].",e);
        }
		return creators.get(widgetType);
	}
	
	/**
     * Returns a helper object to create the code of the widgets of the given type. 
	 * @param widgetType
	 * @return a helper object to create the code of the widgets of the given type. 
	 */
	protected WidgetCreatorHelper getWidgetCreatorHelper(String widgetType)
	{
		if (!creatorHelpers.containsKey(widgetType))
		{
			if (getWidgetCreator(widgetType) == null)
			{
				return null;
			}
		}
		return creatorHelpers.get(widgetType);
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
		return newWidget(printer, metaElem, widgetId, widgetType, this.screenWidgetConsumer, true);
	}

	/**
	 * Creates a new widget based on its meta-data element.
	 *  
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param consumer
	 * @param allowWrapperForCreatedWidget
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType, 
			WidgetConsumer consumer, boolean allowWrapperForCreatedWidget) 
				throws CruxGeneratorException
	{
		WidgetCreator<?> widgetFactory = getWidgetCreator(widgetType);
		if (widgetFactory == null)
		{
			throw new CruxGeneratorException("Can not found widgetFactory for type: ["+widgetType+"].");
		}
		
		String widget;
		//TODO nao colocar na lista de lazyDeps qdo addToScreen for false
		if (consumer != null && consumer instanceof LazyCompatibleWidgetConsumer && mustRenderLazily(widgetType, metaElem, widgetId))
		{
			String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
			lazyPanels.add(lazyPanelId);
			widget = lazyFactory.getLazyPanel(printer, metaElem, widgetId, LazyPanelWrappingType.wrapWholeWidget);
			consumer.consume(printer, lazyPanelId, widget);
			((LazyCompatibleWidgetConsumer)consumer).handleLazyWholeWidgetCreation(printer, widgetId);
		}
		else
		{
			widget = widgetFactory.createWidget(printer, metaElem, widgetId, consumer);
			if (isHtmlContainer(widgetType))
			{
				String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
				printer.println(screenVariable+".cleanLazyDependentWidgets("+EscapeUtils.quote(lazyPanelId)+");");//TODO remover tratamento especial para HTMLPanel
			}
		}
		if (widget == null)
		{
			throw new CruxGeneratorException("Can not create widget ["+widgetId+"]. Verify the widget type.");
		}
		
		Class<?> widgetClass = getWidgetCreatorHelper(widgetType).getWidgetType();
		if (allowWrapperForCreatedWidget)
		{
			widget = getEventTapWrapperForHasClickWidget(printer, widget, widgetClass);
		}
		
		return widget;
	}

	/**
	 * Create a TapEventAdapter for touch devices, once click events has a delay to be fired
	 * @param printer
	 * @param widget
	 * @param widgetClass
	 * @return
	 */
	protected String getEventTapWrapperForHasClickWidget(SourcePrinter printer, String widget, Class<?> widgetClass)
    {
		if (getScreen().isTouchEventAdaptersEnabled())
		{
			Device currentDevice = Device.valueOf(getDevice());
			if (currentDevice.getInput().equals(Input.touch))
			{
				if (HasClickHandlers.class.isAssignableFrom(widgetClass) && HasAllTouchHandlers.class.isAssignableFrom(widgetClass))
				{
					String wrapperWidget = createVariableName("widget");
					printer.println(TapEventAdapter.class.getCanonicalName()+" "+wrapperWidget+" = new "+TapEventAdapter.class.getCanonicalName()+"("+widget+");" );
					widget = wrapperWidget;
				}
			}
		}
	    return widget;
    }

	/**
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 * 
	 * @param printer
	 */
	protected void commitPostProcessing(SourcePrinter printer)
	{
		PostProcessingPrinter postProcessingPrinter = this.postProcessingCode.removeLast();
		String postProcessingCode = postProcessingPrinter.toString();
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
		this.postProcessingCode.add(new PostProcessingPrinter());
	} 	
	
	/**
	 * @return
	 */
	protected TreeLogger getLogger()
	{
		return this.logger;
	}
	
	/**
	 * Retrieves the screen variable name
	 * @return
	 */
	protected String getScreenVariable()
	{
		return screenVariable;
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
	 * @return
	 */
	protected Screen getScreen()
	{
		return this.screen;
	}
	
	/**
     * Gets the code necessary to access a i18n declared property or the own property, if
     * it is not in declarative i18n format.
     * 
	 * @param title
	 * @return
	 */
	protected String getDeclaredMessage(String property)
    {
	    if (isKeyReference(property))
	    {
			String[] messageParts = getKeyMessageParts(property);
			String messageClassName = MessageClasses.getMessageClass(messageParts[0]);
			
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
	    else
	    {
	    	return property==null?null:EscapeUtils.quote(property);
	    }
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
			throw new CruxGeneratorException("Message ["+messageKey+"] , declared on screen ["+screen.getId()+"], not found.");
		}
		else
		{	
			JClassType messageClass = context.getTypeOracle().findType(messageClassName);
			if (messageClass == null)
			{	
				String message = "Message class ["+messageKey+"] , declared on screen ["+screen.getId()+"], could not be loaded. "
							   + "\n Possible causes:"
							   + "\n\t 1. Check if any type or subtype used by message refers to another module and if this module is inherited in the .gwt.xml file."
							   + "\n\t 2. Check if your message or its members belongs to a client package."
							   + "\n\t 3. Check the versions of all your modules."
							   ;
				throw new CruxGeneratorException(message);
			}
			else
			{	
				JMethod method = JClassUtils.getMethod(messageClass, messageMethod, new JType[]{});
				if (method == null)
				{	
					throw new CruxGeneratorException("Method ["+messageMethod+"] of message ["+messageKey+"], declared on screen ["+screen.getId()+"], does not exist in message class ["+messageClassName+"].");
				}
			}
		}
	}

    /**
     * Returns <code>true</code> if the given text is an internationalization key.
	 * @param text
	 * @return <code>true</code> if the given text is an internationalization key.
	 */
	protected boolean isKeyReference(String text)
	{
		return text.matches("\\$\\{\\w+\\.\\w+\\}");
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
	 * @param widgetId
	 * @return
	 */
	boolean containsWidget(String widgetId)
	{
		return screen.getWidget(widgetId) != null;
	}
	
	/**
	 * @return
	 */
	GeneratorContextExt getContext()
	{
		return context;
	}
	
    /**
	 * Gets the list of classes used by the ViewFactory.
	 * 
     * @return
     */
    String[] getImports()
    {
	    @SuppressWarnings("deprecation")
        String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		StringUtils.class.getCanonicalName(), 
    		Window.class.getCanonicalName(),
    		ViewFactoryUtils.class.getCanonicalName(),
    		RootPanel.class.getCanonicalName(),
    		RootLayoutPanel.class.getCanonicalName(),
    		Element.class.getCanonicalName(),
    		Node.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.Event.class.getCanonicalName(),
    		ScreenLoadEvent.class.getCanonicalName(), 
    		Panel.class.getCanonicalName(), 
    		InterfaceConfigException.class.getCanonicalName(), 
    		Widget.class.getCanonicalName(), 
    		Crux.class.getCanonicalName()
		};
	    return imports;
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
    SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports)
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
    SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, subType);

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
		
		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);       
	}
	
	/**
	 * Check if the given metaElement refers to a valid widget
	 * 
	 * @param element
	 * @return
	 */
	boolean isValidWidget(JSONObject metaElem)
	{
		String type =  metaElem.optString("_type");
		if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * @param context
	 * @param logger
	 * @param device 
	 */
	void prepare(GeneratorContextExt context, TreeLogger logger, String device)
	{
		this.context = context;
		this.logger = logger;
		this.lazyPanels.clear();
		this.declaredMessages.clear();
		this.postProcessingCode.clear();
		this.device = device;
	}
	
	/**
	 * 
	 * @return
	 */
	String getDevice()
	{
		return this.device;
	}
	
	/**
	 * Print code that will be executed after the viewFactory completes the widgets construction.
	 * Note that this code will be executed from inside a Command class. Any variable accessed in 
	 * this code and declared outside need to be declared as final.
	 * 
	 * @param s code string
	 */
	void printlnPostProcessing(String s)
	{
		this.postProcessingCode.getLast().println(s);
	}
	
	/**
	 * Creates the close event.
	 * 
	 * @param printer 
	 */
	private void createCloseEvt(SourcePrinter printer)
    {
	    Event onClose = screen.getEvent("onClose");
		if (onClose != null)
		{
			printer.println(screenVariable+".addWindowCloseHandler(new "+CloseHandler.class.getCanonicalName()+"<Window>(){");
			printer.println("public void onClose("+CloseEvent.class.getCanonicalName()+"<Window> event){"); 

			EvtProcessor.printEvtCall(printer, onClose.getController()+"."+onClose.getMethod(), "onClose", 
					CloseEvent.class, "event", context, screen.getId(), getControllerAccessHandler());
			
			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Creates the closing event.
	 * 
	 * @param printer 
	 */
	private void createClosingEvt(SourcePrinter printer)
    {
	    Event onClosing = screen.getEvent("onClosing");
		if (onClosing != null)
		{
			printer.println(screenVariable+".addWindowClosingHandler(new Window.ClosingHandler(){");
			printer.println("public void onWindowClosing("+ClosingEvent.class.getCanonicalName()+" event){"); 

			EvtProcessor.printEvtCall(printer, onClosing.getController()+"."+onClosing.getMethod(), "onClosing", 
						ClosingEvent.class, "event", context, screen.getId(), getControllerAccessHandler());
			
			printer.println("}");
			printer.println("});");
		}
    }
	
	/**
	 * Creates the historyChanged event.
	 * 
	 * @param printer 
	 */
	private void createHistoryChangedEvt(SourcePrinter printer)
    {
	    Event onHistoryChanged = screen.getEvent("onHistoryChanged");
		if (onHistoryChanged != null)
		{
			printer.println(screenVariable+".addWindowHistoryChangedHandler(new "+ValueChangeHandler.class.getCanonicalName()+"<String>(){");
			printer.println("public void onValueChange("+ValueChangeEvent.class.getCanonicalName()+"<String> event){");

			EvtProcessor.printEvtCall(printer, onHistoryChanged.getController()+"."+onHistoryChanged.getMethod(), 
					"onHistoryChanged", ValueChangeEvent.class, "event", context, screen.getId(), getControllerAccessHandler());
			
			printer.println("}");
			printer.println("});");
		}
    }
	
	
	/**
	 * Generate the code for an {@code HTMLContainer} widget creation and attach it to the page DOM.  
	 * 
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 */
	private String createHtmlContainerAndAttach(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType) 

	{
		String panelElement = createVariableName("panelElement");
		String parentElement =createVariableName("parentElement");
		String previousSibling = createVariableName("previousSibling");

		printer.println("Element "+panelElement+" = ViewFactoryUtils.getEnclosingPanelElement("+EscapeUtils.quote(widgetId)+");");
		printer.println("Element "+parentElement+" = "+panelElement+".getParentElement();");
		printer.println("Node "+previousSibling+" = "+panelElement+".getPreviousSibling();");

		String widget = newWidget(printer, metaElem, widgetId, widgetType);
		WidgetCreator<?> widgetFactory = getWidgetCreator(widgetType);
		boolean hasPartialSupport = widgetFactory.hasPartialSupport();
		if (hasPartialSupport)
		{
			printer.println("if ("+widgetFactory.getWidgetClassName()+".isSupported()){");
		}
		
		printer.println("if ("+previousSibling+" != null){");
		printer.println(parentElement+".insertAfter("+widget+".getElement(), "+previousSibling+");");
		printer.println("}");
		printer.println("else{");
		printer.println(parentElement+".appendChild("+widget+".getElement());");
		printer.println("}");
		printer.println("((HTMLContainer)"+widget+").onAttach();");
		printer.println("RootPanel.detachOnWindowClose("+widget+");");		
		if (hasPartialSupport)
		{
			printer.println("}");
		}
		return widget;
	}

	/**
	 * Creates the load event.
	 *  
	 * @param printer 
	 */
	private void createLoadEvt()
	{
		Event event =screen.getEvent("onLoad");
		if (event != null)
		{
			JClassType eventClassType = context.getTypeOracle().findType(ScreenLoadEvent.class.getCanonicalName());

			String controller = ClientControllers.getController(event.getController());
			if (controller == null)
			{
				throw new CruxGeneratorException("Controller ["+controller+"] , declared on screen ["+screen.getId()+"], not found.");
			}

			boolean hasEventParameter = true;
			JClassType controllerClass = context.getTypeOracle().findType(controller);
			if (controllerClass == null)
			{
				String message = "Controller class ["+controller+"] , declared on screen ["+screen.getId()+"], could not be loaded. "
							   + "\n Possible causes:"
							   + "\n\t 1. Check if any type or subtype used by controller refers to another module and if this module is inherited in the .gwt.xml file."
							   + "\n\t 2. Check if your controller or its members belongs to a client package."
							   + "\n\t 3. Check the versions of all your modules."
							   ;
				throw new CruxGeneratorException(message);
			}
			if (EvtProcessor.getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass) == null)
			{
				if (JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{}) == null)
				{
	        		throw new CruxGeneratorException("Screen ["+screen.getId()+"] tries to invoke the method ["+event.getMethod()+"] on controller ["+controller+"]. That method does not exist.");
				}
				hasEventParameter = false;
			}

			printlnPostProcessing(getControllerAccessHandler().getControllerExpression(event.getController())+"."+event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");

			if (hasEventParameter)
			{
				printlnPostProcessing("new ScreenLoadEvent()");
			}
			printlnPostProcessing(");");
    	}
    }

	/**
	 * Creates the resized event.
	 * 
	 * @param printer 
	 * @return
	 */
	private Event createResizedEvt(SourcePrinter printer)
    {
	    Event onResized = screen.getEvent("onResized");
		if (onResized != null)
		{
			printer.println("screen.addWindowResizeHandler(new "+ResizeHandler.class.getCanonicalName()+"(){");
			printer.println("public void onResize("+ResizeEvent.class.getCanonicalName()+" event){"); 

			EvtProcessor.printEvtCall(printer, onResized.getController()+"."+onResized.getMethod(), "onResized", 
					ResizeEvent.class, "event", context, screen.getId(), getControllerAccessHandler());
			
			printer.println("}");
			printer.println("});");
		}
	    return onResized;
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
					"On page ["+screen.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+screen.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}

		if (!isAttachToDOM(widgetType))
		{
			widget = newWidget(printer, metaElem, widgetId, widgetType);
		}
		else if (isHtmlContainer(widgetType))
		{
			widget = createHtmlContainerAndAttach(printer, metaElem, widgetId, widgetType);
		}
		else
		{
			widget = createWidgetAndAttach(printer, metaElem, widgetId, widgetType);
		}
		return widget;
	}
	
	/**
	 * Generate the code for an widget creation and attach it to the page DOM.
	 * 
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 */
	private String createWidgetAndAttach(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType)
	{
		String panelElement = createVariableName("panelElement");
		String panel = createVariableName("panel");
		
		printer.println("Element "+panelElement+" = ViewFactoryUtils.getEnclosingPanelElement("+EscapeUtils.quote(widgetId)+");");

		Class<?> widgetClassType = getWidgetCreatorHelper(widgetType).getWidgetType();
		String widget = newWidget(printer, metaElem, widgetId, widgetType);
		WidgetCreator<?> widgetFactory = getWidgetCreator(widgetType);
		boolean hasPartialSupport = widgetFactory.hasPartialSupport();
		if (hasPartialSupport)
		{
			printer.println("if ("+widgetFactory.getWidgetClassName()+".isSupported()){");
		}
		
		printer.println("Panel "+panel+";");
		if (RequiresResize.class.isAssignableFrom(widgetClassType))
		{
			boolean hasSize = (WidgetCreator.hasWidth(metaElem) && WidgetCreator.hasHeight(metaElem));
			if (!hasSize)
			{
				printer.println("if (RootPanel.getBodyElement().equals("+panelElement+".getParentElement())){");
				printer.println(panel+" = RootLayoutPanel.get();");
				printer.println("}");
				printer.println("else{");
				printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
				printer.println("}");
				printer.println(loggerVariable+".warning(Crux.getMessages().screenFactoryLayoutPanelWithoutSize("+EscapeUtils.quote(widgetId)+"));");
			}
			else
			{
				printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
			}
		}
		else
		{
			printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
		}
		printer.println(panel+".add("+widget+");");
		
		if (hasPartialSupport)
		{
			printer.println("}");
		}
		return widget;
	}	

	/**
	 * @param printer
	 */
	private void generateCreateMethod(SourcePrinter printer)
    {
	    createPostProcessingScope();
    	printer.println("public void create() throws InterfaceConfigException{");
		printer.println("createRegisteredControllers();");
		printer.println("createRegisteredDataSources();");
    	
		if (this.screen.isNormalizeDeviceAspectRatio())
		{
			printer.println(DeviceDisplayHandler.class.getCanonicalName()+".configureDisplayForDevice();");
		}
		
		JSONArray metaData = this.screen.getMetaData();
		createScreen(printer);
		for (int i = 0; i < metaData.length(); i++)
		{
			JSONObject metaElement = metaData.optJSONObject(i);

			if (!metaElement.has("_type"))
			{
				throw new CruxGeneratorException("Crux Meta Data contains an invalid meta element (without type attribute).");
			}
			String type = getMetaElementType(metaElement);
			if (!StringUtils.unsafeEquals("screen",type))
			{
				try 
				{
					createWidget(printer, metaElement, type);
				}
				catch (Throwable e) 
				{
					throw new CruxGeneratorException("Error Creating widget. See Log for more detail.", e);
				}
			}
		}

		commitPostProcessing(printer);
		
		printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".info(Crux.getMessages().screenFactoryViewCreated("+screenVariable+".getIdentifier()));");
		printer.println("}");
		
		printer.println("}");
    }	
	
	/**
	 * @param printer
	 */
	private void generateCreateRegisteredControllersMethod(SourcePrinter printer)
    {
    	printer.println("public void createRegisteredControllers() throws InterfaceConfigException{");
    	String regsiteredControllersClass = new RegisteredControllersProxyCreator(logger, context, screen).create();
		printer.println(ScreenFactory.class.getCanonicalName()+".getInstance().setRegisteredControllers(new "+regsiteredControllersClass+"());");
    	printer.println("}");
    }

	/**
	 * @param printer
	 */
	private void generateCreateRegisteredDataSourcesMethod(SourcePrinter printer)
    {
    	printer.println("public void createRegisteredDataSources() throws InterfaceConfigException{");
    	String regsiteredDataSourcesClass = new RegisteredDataSourcesProxyCreator(logger, context, screen).create();
		printer.println(ScreenFactory.class.getCanonicalName()+".getInstance().setRegisteredDataSources(new "+regsiteredDataSourcesClass+"());");
    	printer.println("}");
    }
	
	/**
	 * Split the i18n message and separate the messageClass alias from the message method
	 * 
	 * @param text
	 * @return
	 */
	private String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}

	/**
	 * Return the qualified name of the ViewFactory class created for the associated screen
	 * @return
	 */
	String getQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getSimpleName();
    }
	
	/**
	 * Return the simple name of the ViewFactory class created for the associated screen
	 * @return
	 */
	String getSimpleName()
    {
		String className = screen.getModule()+"_"+screen.getRelativeId()+"_"+this.device; 
		className = className.replaceAll("[\\W]", "_");
		return className;
    }
	
    /**
	 * Creates and returns a new {@link SourceWriter}
     * @return a new {@link SourceWriter}
     */
    private SourceWriter getSourceWriter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getSimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getSimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		return composerFactory.createSourceWriter(context, printWriter);    
	}

    /**
     * Returns <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 * @param widgetType
	 * @return <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 */
	private boolean isAttachToDOM(String widgetType)
	{
		if (!attachToDOMfactories.containsKey(widgetType))
		{
			DeclarativeFactory declarativeFactory = getWidgetCreator(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			attachToDOMfactories.put(widgetType, declarativeFactory.attachToDOM());
		}
		return attachToDOMfactories.get(widgetType);
	}

    /**
     * Returns <code>true</code> if the given widget type is an HTML container.
	 * @param widgetType
	 * @return <code>true</code> if the given widget type is an HTML container.
	 */
	private boolean isHtmlContainer(String widgetType)
	{
		if (!htmlContainersfactories.containsKey(widgetType))
		{
			DeclarativeFactory declarativeFactory = getWidgetCreator(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			htmlContainersfactories.put(widgetType, declarativeFactory.htmlContainer());
		}
		return htmlContainersfactories.get(widgetType);
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
		Class<?> widgetClass = getWidgetCreatorHelper(widgetType).getWidgetType();
		if ((Panel.class.isAssignableFrom(widgetClass)) && (!isHtmlContainer(widgetType)))
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
     * Printer for screen creation codes.
     * 
     * @author Thiago da Rosa de Bustamante
     */
    public static class SourcePrinter
    {
    	private final TreeLogger logger;
		private final SourceWriter srcWriter;

    	/**
    	 * Constructor
    	 * @param srcWriter
    	 * @param logger
    	 */
    	public SourcePrinter(SourceWriter srcWriter, TreeLogger logger)
        {
			this.srcWriter = srcWriter;
			this.logger = logger;
        }
    	
    	
    	/**
    	 * Flushes the printed code into a real output (file).
    	 */
    	public void commit()
    	{
    		srcWriter.commit(logger);
    	}
    	
    	/**
    	 * Indents the next line to be printed
    	 */
    	public void indent()
    	{
    		srcWriter.indent();
    	}
    	
    	/**
    	 * Outdents the next line to be printed
    	 */
    	public void outdent()
    	{
    		srcWriter.outdent();
    	}
    	
    	/**
    	 * Prints an in-line code.
    	 * @param s
    	 */
    	public void print(String s)
    	{
    		srcWriter.print(s);
    	}
    	
    	/**
    	 * Prints a line of code into the output. 
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	public void println(String s)
    	{
    		String line = s.trim();
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};"))
    		{
    			outdent();
    		}
			
    		srcWriter.println(s);
    		
    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}
    }

    /**
     * Printer for code that should be executed after the screen creation.
     * 
     * @author Thiago da Rosa de Bustamante
     */
    private static class PostProcessingPrinter
    {
		private StringBuilder builder = new StringBuilder();
		private String indentation = "";
		
    	/**
    	 * @see java.lang.Object#toString()
    	 */
    	public String toString()
    	{
    		return builder.toString();
    	}
    	
    	/**
    	 * Indents the next line to be printed
    	 */
    	void indent()
    	{
    		indentation+="\t";
    	}
    	
    	/**
    	 * Outdents the next line to be printed
    	 */
    	void outdent()
    	{
    		if (indentation.length() > 0)
    		{
    			indentation = indentation.substring(1);
    		}
    	}
    	
    	/**
    	 * Prints a line of code into the output. 
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	void println(String s)
    	{
    		String line = s.trim();
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};"))
    		{
    			outdent();
    		}
			
    		builder.append(indentation+s+"\n");
    		
    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}
    }
    
    private static class DefaultControllerAccessor implements ControllerAccessHandler
    {

		public String getControllerExpression(String controller)
        {
			
	        return "(("+getControllerImplClassName(controller)+")ScreenFactory.getInstance().getRegisteredControllers().getController("
			+EscapeUtils.quote(controller)+"))";
        }

		public String getControllerImplClassName(String controller)
        {
			String controllerClass = ClientControllers.getController(controller);
	        return controllerClass + ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX;
        }
    }
    
	/**
	 * @return the screenWidgetConsumer
	 */
	WidgetConsumer getScreenWidgetConsumer() 
	{
		return screenWidgetConsumer;
	}    
}
