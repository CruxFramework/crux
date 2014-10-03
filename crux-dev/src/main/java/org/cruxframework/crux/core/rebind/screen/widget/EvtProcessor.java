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

import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Factory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.utils.JClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JGenericType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class EvtProcessor extends AbstractProcessor
{
	/**
	 * @param widgetCreator
	 */
	public EvtProcessor(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	/**
	 * @param out
	 * @param context
	 * @param eventValue
	 */
    public void processEvent(SourcePrinter out, WidgetCreatorContext context, String eventValue)
    {
		processEvent(out, eventValue, context.getWidget(), context.getWidgetId());
    }


    /**
     * @param out
     * @param eventValue
     * @param cruxEvent
     */
    public void printEvtCall(SourcePrinter out, String eventValue, String cruxEvent)
    {
    	printEvtCall(out, eventValue, getEventName(), getEventClass(), cruxEvent, getWidgetCreator());
    }

    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param creator
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass, String cruxEvent, WidgetCreator<?> creator)
    {
    	printEvtCall(out, eventValue, eventName,  eventClass!= null? eventClass.getCanonicalName():null, cruxEvent, creator);
    }

    /**
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClassName
     * @param cruxEvent
     * @param creator
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, String eventClassName,
    								String cruxEvent, WidgetCreator<?> creator)
    {
    	printEvtCall(out, eventValue, eventName, eventClassName, cruxEvent, creator.getContext(), creator.getView(), creator.getControllerAccessorHandler(), 
    			creator.getDevice(), true);
    }

    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, String eventClassName,
			String cruxEvent, WidgetCreator<?> creator, boolean allowNoParameterCall)
	{
		printEvtCall(out, eventValue, eventName, eventClassName, cruxEvent, creator.getContext(), creator.getView(), creator.getControllerAccessorHandler(), 
		creator.getDevice(), allowNoParameterCall);
	}
    
    /**
     * 
     * @param out
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param context
     * @param view
     * @param controllerAccessHandler
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName, Class<?> eventClass,
    		                        String cruxEvent, GeneratorContext context, View view, ControllerAccessHandler controllerAccessHandler, Device device)
    {
    	printEvtCall(out, eventValue, eventName, eventClass!= null? eventClass.getCanonicalName():null, cruxEvent, context, view, 
    			controllerAccessHandler, device, true);
    }

    /**
     * 
     * @param out
     * @param eventValue
     * @param eventName
     * @param parameterClassName
     * @param cruxEvent
     * @param context
     * @param view
     * @param controllerAccessHandler
     */
    public static void printEvtCall(SourcePrinter out, String eventValue, String eventName,String parameterClassName,
    		                        String cruxEvent, GeneratorContext context, View view, ControllerAccessHandler controllerAccessHandler, 
    		                        Device device, boolean allowNoParameterCall)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);
    	boolean hasEventParameter = checkEvtCall(eventValue, eventName, parameterClassName, context, view, device, allowNoParameterCall);
    	out.print(controllerAccessHandler.getControllerExpression(event.getController(), device)+"."+event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");
    	
    	if (hasEventParameter)
    	{
    		out.print(cruxEvent);
    	}
    	out.println(");");
    }

    /**
     * 
     * @param eventValue
     * @param eventName
     * @param parameterClassName
     * @param context
     * @param view
     * @param device
     * @param allowNoParameterCall
     */
    public static boolean checkEvtCall(String eventValue, String eventName,String parameterClassName,
            GeneratorContext context, View view, Device device, boolean allowNoParameterCall)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);

    	if (event == null)
    	{
    		throw new CruxGeneratorException("Error parsing controller method declaration on view ["+view.getId()+"]. ["+eventValue+"] is not a valid method declaration.");
    	}
    	
    	JClassType eventClassType = parameterClassName==null?null:context.getTypeOracle().findType(parameterClassName);

    	if (!view.useController(event.getController()))
    	{
    		throw new CruxGeneratorException("Controller ["+event.getController()+"] , used on view ["+view.getId()+"], was not declared on this view. Use the useController attribute to import the controller into this view.");
    	}
    	
    	String controller = ClientControllers.getController(event.getController(), device);
    	if (controller == null)
    	{
    		throw new CruxGeneratorException("Controller ["+event.getController()+"] , declared on view ["+view.getId()+"], not found.");
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = context.getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
    		String message = "Controller class ["+controller+"] , declared on view ["+view.getId()+"], could not be loaded. "
						   + "\n Possible causes:"
						   + "\n\t 1. Check if any type or subtype used by controller refers to another module and if this module is inherited in the .gwt.xml file."
						   + "\n\t 2. Check if your controller or its members belongs to a client package."
						   + "\n\t 3. Check the versions of all your modules."
						   ;
    		throw new CruxGeneratorException(message);
    	}

    	JMethod exposedMethod = getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass);
    	if (exposedMethod == null)
    	{
    		if (allowNoParameterCall)
    		{
    			exposedMethod = JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{});
    			if (exposedMethod == null)
    			{
    				throw new CruxGeneratorException("View ["+view.getId()+"] tries to invoke the method ["+event.getMethod()+"] on controller ["+controller+"]. That method does not exist.");
    			}
    			hasEventParameter = false;
    		}
    		else
			{
				throw new CruxGeneratorException("View ["+view.getId()+"] tries to invoke the method ["+event.getMethod()+"] on controller ["+controller+"]. That method does not exist.");
			}
    	}

    	checkExposedMethod(event, controller, exposedMethod, context);
    	return hasEventParameter;
    }

    
    /**
	 * @param event
	 * @param controller
	 * @param exposedMethod
	 * @param context
	 */
	private static void checkExposedMethod(Event event, String controller, JMethod exposedMethod, GeneratorContext context)
	{
		if (exposedMethod.getAnnotation(Expose.class) == null && exposedMethod.getAnnotation(Factory.class) == null)
    	{
    		throw new CruxGeneratorException(" Method ["+event.getMethod()+"] of Controller ["+controller+"] is not exposed, so it can not be called from crux.xml pages.");
    	}

		JClassType runtimeExceptionType = context.getTypeOracle().findType(RuntimeException.class.getCanonicalName());

    	JClassType[] methodThrows = exposedMethod.getThrows();
    	if (methodThrows != null)
    	{
    		for (JClassType exception : methodThrows)
    		{
    			if (!exception.isAssignableTo(runtimeExceptionType))
    			{
    				throw new CruxGeneratorException("Method ["+event.getMethod()+"] of Controller ["+controller+"] can not be exposed. It can throw a checked exception.");
    			}
			}
    	}
	}

	/**
	 * @param methodName
	 * @param eventClassType
	 * @param controllerClass
	 * @return
	 */
	static JMethod getControllerMethodWithEvent(String methodName, JClassType eventClassType, JClassType controllerClass)
    {
		if (eventClassType == null)
		{
			return null;
		}
		JGenericType genericType = eventClassType.isGenericType();
		if (genericType == null)
		{
			return JClassUtils.getMethod(controllerClass, methodName, new JType[]{eventClassType});
		}
		else
		{
			eventClassType = genericType.getRawType();
			JClassType superClass = controllerClass;
			while (superClass.getSuperclass() != null)
			{
				JMethod[] methods = superClass.getMethods();
				if (methods != null)
				{
					for (JMethod method : methods)
					{
						JParameter[] parameters = method.getParameters();
						if (method.getName().equals(methodName) && parameters != null && parameters.length==1 &&
								parameters[0].getType().isClass() != null && parameters[0].getType().isClass().isAssignableTo(eventClassType))
						{
							return method;
						}
					}
				}
				superClass = superClass.getSuperclass();
			}
			return null;
		}
    }

    /**
     * @param eventValue
     * @param cruxEvent
     */
    public void printPostProcessingEvtCall(String eventValue, String cruxEvent, Device device)
    {
    	printPostProcessingEvtCall(eventValue, getEventName(), getEventClass(), cruxEvent, getWidgetCreator());
    }

    /**
     * @param eventValue
     * @param eventName
     * @param eventClass
     * @param cruxEvent
     * @param creator
     */
    public static void printPostProcessingEvtCall(String eventValue, String eventName, Class<?> eventClass, String cruxEvent, WidgetCreator<?> creator)
    {
    	Event event = EventFactory.getEvent(eventName, eventValue);

    	JClassType eventClassType = creator.getContext().getTypeOracle().findType(eventClass.getCanonicalName());

    	if (!creator.getView().useController(event.getController()))
    	{
    		throw new CruxGeneratorException("Controller ["+event.getController()+"] , used on view ["+creator.getView().getId()+"], was not declared on this view. Use the useController attribute to import the controller into this view.");
    	}
    	String controller = ClientControllers.getController(event.getController(), creator.getDevice());
    	if (controller == null)
    	{
    		throw new CruxGeneratorException("Controller ["+event.getController()+"] , declared on view ["+creator.getView().getId()+"], not found.");
    	}

    	boolean hasEventParameter = true;
    	JClassType controllerClass = creator.getContext().getTypeOracle().findType(controller);
    	if (controllerClass == null)
    	{
			String message = "Controller class ["+controller+"] , declared on view ["+creator.getView().getId()+"], could not be loaded. "
						   + "\n Possible causes:"
						   + "\n\t 1. Check if any type or subtype used by controller refers to another module and if this module is inherited in the .gwt.xml file."
						   + "\n\t 2. Check if your controller or its members belongs to a client package."
						   + "\n\t 3. Check the versions of all your modules."
						   ;
			throw new CruxGeneratorException(message);
    	}

    	JMethod exposedMethod = getControllerMethodWithEvent(event.getMethod(), eventClassType, controllerClass);
		if (exposedMethod == null)
    	{
			exposedMethod = JClassUtils.getMethod(controllerClass, event.getMethod(), new JType[]{});
    		if (exposedMethod == null)
    		{
        		throw new CruxGeneratorException("View ["+creator.getView().getId()+"] tries to invoke the method ["+event.getMethod()+"] on controller ["+controller+"]. That method does not exist.");
    		}
    		hasEventParameter = false;
    	}

    	checkExposedMethod(event, controller, exposedMethod, creator.getContext());

        creator.printlnPostProcessing(creator.getControllerAccessorHandler().getControllerExpression(event.getController(), creator.getDevice())+"."+event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");

    	if (hasEventParameter)
    	{
    		creator.printlnPostProcessing(cruxEvent);
    	}
    	creator.printlnPostProcessing(");");
    }

    /**
     * @param out
     * @param eventValue
     * @param parentVariable
     * @param widgetId
     */
    public void processEvent(SourcePrinter out, String eventValue, String parentVariable, String widgetId)
    {
		out.println(parentVariable+".add"+getEventHandlerClass().getSimpleName()+"(new "+getEventHandlerClass().getCanonicalName()+"(){");
		out.println("public void "+getEventName()+"("+getEventClass().getCanonicalName()+" event){");
		printEvtCall(out, eventValue, "event");
		out.println("}");
		out.println("});");
    }

	/**
	 * @return
	 */
	public abstract Class<?> getEventClass();

	/**
	 * @return
	 */
	public abstract Class<?> getEventHandlerClass();

	/**
	 * @return
	 */
	public abstract String getEventName();
}
