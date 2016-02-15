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

import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
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
	};
	
	@Override
	protected void generateProxyMethods(SourcePrinter printer) throws CruxGeneratorException
	{
		String proxyName = getProxyQualifiedName();
		StringBuilder initMethods = new StringBuilder();
		
		View view = viewFactoryCreator.getView();
		String device = viewFactoryCreator.getDevice();
		
		Iterator<String> nativeControllerCalls = view.iterateNativeControllerCalls();
		
		while(nativeControllerCalls.hasNext())
		{
			String methodName = nativeControllerCalls.next();
			Event event = EventFactory.getEvent(methodName, view.getNativeControllerCall(methodName));
			
			printer.println("public final void bridge_" + methodName + "(){");

			printer.println(viewFactoryCreator.getControllerAccessHandler().getControllerExpression(event.getController(), Device.valueOf(device)) + 
						  "." + event.getMethod()+ControllerProxyCreator.EXPOSED_METHOD_SUFFIX+"();");
				
			printer.println("}");

			printer.println("public native void register_" + methodName + "("+proxyName+"  proxy)/*-{");
			printer.println("if (!$wnd."+methodName+") {");
			printer.println("$wnd."+methodName+" = function(e){");
			printer.println("proxy.@"+proxyName+"::bridge_" + methodName+"()();");
			printer.println("}");
			printer.println("}");
			printer.println("}-*/;");
			
			initMethods.append("register_" + methodName + "(this);\n");
		}

		printer.println("public void init(){");
		printer.println(initMethods.toString());
		printer.println("}");
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
