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
package org.cruxframework.crux.core.rebind.crossdevice;

import java.util.Map;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveViewFactoryCreator extends ViewFactoryCreator
{
	private String controllerClass;

	/**
	 * 
	 * @param context
	 * @param logger
	 * @param view
	 * @param device
	 * @param controllerClass 
	 */
	public DeviceAdaptiveViewFactoryCreator(GeneratorContext context, TreeLogger logger, View view, String device, final String controllerName, String module)
    {
	    super(context, logger, view, device, module);
	    controllerClass = ClientControllers.getController(controllerName, Device.valueOf(device));
		this.controllerAccessHandler = new ControllerAccessHandler(){

			@Override
            public String getControllerExpression(String controller, Device device)
            {
	            assert(controllerName.equals(controller)):"Controller ["+controller+" not found into this view.]";
		        return "(("+controllerClass+ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+")"+getViewVariable()+".getController("+EscapeUtils.quote(controller)+"))";
            }

			@Override
            public String getControllerImplClassName(String controller, Device device)
            {
	            return controllerClass + ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX;
            }
		};

    }

	@Override
	protected void generateProxyFields(SourcePrinter printer)
	{
	    super.generateProxyFields(printer);
		printer.println("public "+controllerClass+ ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+" _controller;");
	}
	
	/**
	 * Generate the View Constructor
	 */
	@Override
	protected void generateProxyContructor(SourcePrinter printer) throws CruxGeneratorException
	{
		printer.println("public "+getProxySimpleName()+"(String id){");
		printer.println("super(id);");
		printer.println("this.iocContainer = new "+iocContainerClassName+"(this);");
		generateResources(printer);
		printer.println("}");
	}

	@Override
	protected void generateProxyMethods(SourcePrinter printer)
	{
	    super.generateProxyMethods(printer);
	    
		printer.println("@Override");
	    printer.println("public <T> T getController(String controller){");
		printer.println("return (T)this._controller;");
		printer.println("}");
	    
		printer.println("public void setController("+controllerClass+ ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+" controller){");
		printer.println("this._controller = controller;");
		printer.println("}");
		// Device adaptive views can not be registered as loaded views. It is only for device widget internal usage.
		printer.println("public void registerLoadedView(){");
		printer.println("}");
		printer.println("public void unregisterLoadedView(){");
		printer.println("}");
	}
	
	
	
	@Override
	protected Map<String, String> getDeclaredMessages()
	{
	    return super.getDeclaredMessages();
	}
	
	@Override
	protected String getLoggerVariable()
	{
	    return super.getLoggerVariable();
	}
}
