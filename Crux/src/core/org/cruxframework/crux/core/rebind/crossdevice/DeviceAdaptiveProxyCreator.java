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

import java.io.PrintWriter;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenFactory;
import org.cruxframework.crux.core.declarativeui.crossdevice.CrossDevices;
import org.cruxframework.crux.core.declarativeui.crossdevice.CrossDevicesTemplateParser;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.json.JSONObject;
import org.w3c.dom.Document;

import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveProxyCreator extends AbstractWrapperProxyCreator
{
	private JClassType controllerClass;
	private Document template;
	private Device device;
	private CrossDevicesTemplateParser templateParser;
	private DeviceAdaptiveViewFactoryCreator viewFactoryCreator;
	private JClassType deviceAdaptiveControllerClass;
	private JClassType deviceAdaptiveClass;
	private JClassType hasHandlersClass;
	private String controllerName;

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public DeviceAdaptiveProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf, false);

	    deviceAdaptiveControllerClass = context.getTypeOracle().findType(DeviceAdaptiveController.class.getCanonicalName());
	    deviceAdaptiveClass = context.getTypeOracle().findType(DeviceAdaptive.class.getCanonicalName());
	    hasHandlersClass = context.getTypeOracle().findType(HasHandlers.class.getCanonicalName());
	    
	    
	    initializeTemplateParser();
	    initializeController();
		viewFactoryCreator = new DeviceAdaptiveViewFactoryCreator(context, logger, getCurrentScreen(), getDeviceFeatures(), controllerName);
    }

	/**
	 * 
	 */
	protected void initializeTemplateParser()
    {
	    Device[] devices = Devices.getDevicesForDevice(getDeviceFeatures());
	    for (Device device : devices)
        {
	    	template = CrossDevices.getDeviceAdaptiveTemplate(baseIntf.getQualifiedSourceName(), device, true);
	    	if (template != null)
	    	{
	    		this.device = device;
	    		this.templateParser = CrossDevicesTemplateParser.getInstance();
	    		return;
	    	}
        }
	    throw new CruxGeneratorException("DeviceAdaptive widget does not declare any valid template for device ["+getDeviceFeatures()+"].");
    }
	
	/**
	 * 
	 * @return
	 */
	protected void initializeController()
	{
		controllerName = templateParser.getTemplateController(template, baseIntf.getQualifiedSourceName(), device);
		String controllerClassName = ClientControllers.getController(controllerName);
		if (controllerClassName == null)
		{
			throw new CruxGeneratorException("Error generating invoker. Controller ["+controllerName+"] not found.");
		}
		
		controllerClass = context.getTypeOracle().findType(controllerClassName);
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
		if (controllerAnnot == null)
		{
			throw new CruxGeneratorException("DeviceAdaptive implementation class ["+controllerClass.getQualifiedSourceName()+"] is not a valid Controller. It must be annotated with @Controller annotation.");
		}
		
		if (!controllerClass.isAssignableTo(deviceAdaptiveControllerClass))
		{
			throw new CruxGeneratorException("DeviceAdaptive implementation class ["+controllerClass.getQualifiedSourceName()+"] must externds the base class DeviceAdaptiveController.");
		}
	}
	
	/**
	 * 
	 */
	@Override
    protected void generateWrapperMethod(JMethod method, SourceWriter srcWriter)
    {
		if (mustDelegateToController(method))
		{
			JType returnType = method.getReturnType().getErasedType();

			srcWriter.println(method.getReadableDeclaration(false, false, false, false, true)+"{");
			srcWriter.indent();
			if (returnType != JPrimitiveType.VOID)
			{
				srcWriter.print("return ");
			}
			srcWriter.print("this._controller."+method.getName()+"(");
			boolean needsComma = false;
			for (JParameter parameter : method.getParameters())
			{
				if (needsComma)
				{
					srcWriter.print(", ");
				}
				needsComma = true;
				srcWriter.print(parameter.getName());
			}
			srcWriter.println(");");

			srcWriter.outdent();
			srcWriter.println("}");
		}
    }

	protected boolean mustDelegateToController(JMethod method)
    {
	    JClassType enclosingType = method.getEnclosingType();
		return (!enclosingType.equals(deviceAdaptiveClass) && !enclosingType.equals(hasHandlersClass));
    }

	@Override
    protected String[] getImports()
    {
	    return new String[]{
	    		ScreenFactory.class.getCanonicalName(),
	    		Screen.class.getCanonicalName()
	    };
    }

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public " + getProxySimpleName() + "(){");
		srcWriter.indent();

		createController(srcWriter);
		
		JSONObject metaData = templateParser.getTemplateMetadata(template,  baseIntf.getQualifiedSourceName(), device);
		String widget = viewFactoryCreator.generateWidgetsCreation(srcWriter, metaData);
		
		srcWriter.println("initWidget("+widget+");");
		srcWriter.println("(("+DeviceAdaptiveController.class.getCanonicalName()+")this._controller).init();");
		srcWriter.println("(("+DeviceAdaptiveController.class.getCanonicalName()+")this._controller).initWidgetDefaultStyleName();");
		srcWriter.outdent();
		srcWriter.println("}");
    }

    /**
	 * 
	 * @param srcWriter
	 */
	protected void createController(SourceWriter srcWriter)
	{
		String genClass = new ControllerProxyCreator(logger, context, controllerClass).create();
		srcWriter.println("this._controller = new "+genClass+"();");
		srcWriter.println("(("+DeviceAdaptiveController.class.getCanonicalName()+")this._controller).setBoundWidget(this);");
	}
	
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private "+controllerClass.getQualifiedSourceName()+ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+" _controller;");
	    for (String messageClass: viewFactoryCreator.getDeclaredMessages().keySet())
	    {
	    	srcWriter.println("private "+messageClass+" "+viewFactoryCreator.getDeclaredMessages().get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
	    srcWriter.println("private static "+Logger.class.getCanonicalName()+" "+viewFactoryCreator.getLoggerVariable()+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getProxySimpleName()+".class.getName());");
    }

	@Override
	public String getProxySimpleName()
	{
		String name = super.getProxySimpleName();
		return name+"_"+getDeviceFeatures();
	}
	
	@Override
	public String getProxyQualifiedName()
	{
		return DeviceAdaptiveController.class.getPackage().getName() + "." + getProxySimpleName();
	}

	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourceWriter getSourceWriter()
	{
		String packageName = DeviceAdaptiveController.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		composerFactory.addImplementedInterface(baseIntf.getQualifiedSourceName());
		composerFactory.setSuperclass(Composite.class.getCanonicalName());

		return composerFactory.createSourceWriter(context, printWriter);
	}	
}
