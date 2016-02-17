/*
 * Copyright 2016 cruxframework.org.
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
import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.View.NativeControllerCall;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * A Helper class to handle calls to crux controllers on native HTML elements
 * @author Thiago da Rosa de Bustamante
 *
 */
public class NativeControllersProxyCreator extends AbstractProxyCreator
{
	private ViewFactoryCreator viewFactoryCreator;

	public NativeControllersProxyCreator(ViewFactoryCreator viewFactoryCreator)
    {
		super(viewFactoryCreator.getContext(), false);
		this.viewFactoryCreator = viewFactoryCreator;
    }

	@Override
    public String getProxyQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getProxySimpleName();
    }
	
	@Override
    public String getProxySimpleName()
    {
		String className = ViewFactoryCreator.getViewNativeControllersType(viewFactoryCreator.getView().getId(), viewFactoryCreator.getDevice());
		return className;
    };
	
	@Override
	protected void generateProxyContructor(SourcePrinter printer) throws CruxGeneratorException
	{
		printer.println("public " + getProxySimpleName() + "("+org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName()+" view){");
		printer.println("this."+ViewFactoryCreator.getViewVariable() + " = view;");
		printer.println("}");
	}
	
	protected void generateProxyFields(SourcePrinter printer) throws CruxGeneratorException 
	{
		printer.println("private " + org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName() + 
			" " + ViewFactoryCreator.getViewVariable() + ";");
	}
	
	
	@Override
	protected void generateProxyMethods(SourcePrinter printer) throws CruxGeneratorException
	{
		String proxyName = getProxyQualifiedName();
		StringBuilder initMethods = new StringBuilder();
		
		View view = viewFactoryCreator.getView();
		String device = viewFactoryCreator.getDevice();
		
		Iterator<NativeControllerCall> nativeControllerCalls = view.iterateNativeControllerCalls();
		
		while(nativeControllerCalls.hasNext())
		{
			NativeControllerCall nativeControllerCall = nativeControllerCalls.next();
			String methodName = nativeControllerCall.getMethod();
			Event event = EventFactory.getEvent(methodName, nativeControllerCall.getControllerCall());
			
			JClassType eventType = getControllerMethodParameter(nativeControllerCall);
			printer.print("public final void bridge_" + methodName + "(");
			if (eventType != null)
			{
				printer.print(eventType.getParameterizedQualifiedSourceName() + " e");
			}
			printer.println("){");
			
			printer.print(viewFactoryCreator.getControllerAccessHandler().getControllerExpression(event.getController(), Device.valueOf(device)) + 
						  "." + event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"(");
			if (eventType != null)
			{
				printer.print("e");
			}
			printer.println(");");
				
			printer.println("}");

			printer.println("public native void register_" + methodName + "("+proxyName+"  proxy)/*-{");
			printer.println("$wnd."+methodName+" = function(e){");
			if (eventType != null)
			{
				printer.println("proxy.@"+proxyName+"::bridge_" + methodName+"(L"+
									eventType.getParameterizedQualifiedSourceName().replace('.', '/')+";)(e);");
			}
			else
			{
				printer.println("proxy.@"+proxyName+"::bridge_" + methodName+"()();");
			}
			printer.println("}");
			printer.println("}-*/;");
			
			initMethods.append("register_" + methodName + "(this);\n");
		}

		printer.println("public void init(){");
		printer.println(initMethods.toString());
		printer.println("}");
	}

	protected JClassType getControllerMethodParameter(NativeControllerCall nativeControllerCall) 
	{
		View view = viewFactoryCreator.getView();
		String device = viewFactoryCreator.getDevice();

		JMethod[] possibleEventHandlers = EvtProcessor.getControllerDomEventHandlers(context, "nativeEvent", 
			nativeControllerCall.getControllerCall(), view, Device.valueOf(device));
		
		if (possibleEventHandlers.length == 0)
		{
			throw new CruxGeneratorException("Error creating native HTML controller call for event["+nativeControllerCall.getControllerCall()+"]."
				+ "There is no possible handler for this call on target controller. The event handler method must be annotated with @Expose, be public "
				+ "and have no parameter or a single parameter of type NativeEvent.");
		}
		try
		{
			JMethod method = possibleEventHandlers[0];
			JType[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length > 0)
			{
				JClassType parameterType = parameterTypes[0].isClassOrInterface();
				return parameterType;
			}
			else 
			{
				return null;
			}
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException("Error creating native HTML controller call for event["+nativeControllerCall.getControllerCall()+"].", e);
		}
	}

	@Override
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.getGeneratorContext().tryCreate(context.getLogger(), packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		return new SourceCodePrinter(composerFactory.createSourceWriter(context.getGeneratorContext(), printWriter), context.getLogger());
    }

}
