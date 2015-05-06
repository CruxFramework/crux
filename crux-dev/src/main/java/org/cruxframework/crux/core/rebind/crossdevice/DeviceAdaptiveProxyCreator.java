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
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveViewContainer;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.ScreenFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.context.scanner.ResourceNotFoundException;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.w3c.dom.Document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.CachedGeneratorResult;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DeviceAdaptiveProxyCreator extends AbstractWrapperProxyCreator
{
	private JClassType controllerClass;
	private String controllerName;
	private Device device;
	private JClassType deviceAdaptiveClass;
	private JClassType deviceAdaptiveControllerClass;
	private JClassType hasHandlersClass;
	private IocContainerRebind iocContainerRebind;
	private long lastCompilationTime;
	private Document template;
	private CrossDevicesTemplateParser templateParser;
	private View view;
	private String viewClassName;
	private DeviceAdaptiveViewFactoryCreator viewFactoryCreator;
	private Resource templateResource;

	/**
	 * 
	 * @param logger
	 * @param context
	 * @param baseIntf
	 */
	public DeviceAdaptiveProxyCreator(RebindContext context, JClassType baseIntf)
    {
	    super(context, baseIntf, true);

	    initializeLastCompilationVariables();
	    initializeTemplateParser();
    	if ((templateResource.getLastModified() >= lastCompilationTime) || 
    		!findCacheableImplementationAndMarkForReuseIfAvailable())
    	{
    		template = templateParser.getDeviceAdaptiveTemplate(templateResource);

    		deviceAdaptiveControllerClass = context.getGeneratorContext().getTypeOracle().findType(DeviceAdaptiveController.class.getCanonicalName());
    		deviceAdaptiveClass = context.getGeneratorContext().getTypeOracle().findType(DeviceAdaptive.class.getCanonicalName());
    		hasHandlersClass = context.getGeneratorContext().getTypeOracle().findType(HasHandlers.class.getCanonicalName());

    		view = templateParser.getTemplateView(template,  baseIntf.getQualifiedSourceName(), templateResource.getLastModified(), device);
    		initializeController(view);
    		viewFactoryCreator = new DeviceAdaptiveViewFactoryCreator(context, view, isChanged(view), getDeviceFeatures(), controllerName);
    		viewClassName = viewFactoryCreator.create();
    		iocContainerRebind = new IocContainerRebind(context, view, device.toString());
    	}
    }

	@Override
	public String getProxyQualifiedName()
	{
		return DeviceAdaptiveController.class.getPackage().getName() + "." + getProxySimpleName();
	}

	@Override
	public String getProxySimpleName()
	{
		String name = super.getProxySimpleName();
		return name+"_"+getDeviceFeatures();
	}	
	
	
	/**
	 * 
	 * @param srcWriter
	 */
	protected void createController(SourcePrinter srcWriter, String viewVariable)
	{
		String genClass = new ControllerProxyCreator(context, controllerClass).create();
		srcWriter.println("this._controller = new "+genClass+"("+viewVariable+");");
		srcWriter.println("(("+DeviceAdaptiveController.class.getCanonicalName()+")this._controller).setBoundWidget(this);");
		iocContainerRebind.injectFieldsAndMethods(srcWriter, controllerClass, "this._controller", viewVariable+".getTypedIocContainer()", view, device);
	}
	
	@Override
    protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public " + getProxySimpleName() + "(){");

		String viewVariable = ViewFactoryCreator.createVariableName("view");
		srcWriter.println(viewClassName + " " + 
				viewVariable + " = new "+viewClassName+"("+EscapeUtils.quote(baseIntf.getSimpleSourceName())+"+(_idGen++));");
		createController(srcWriter, viewVariable);
		srcWriter.println(viewVariable+".setController(this._controller);");
		
		srcWriter.println("initWidget(viewContainer.asWidget());");
		srcWriter.println("viewContainer.add("+viewVariable+", true, null);");
		srcWriter.println("(("+DeviceAdaptiveController.class.getCanonicalName()+")this._controller).init();");
		srcWriter.println("}");
    }
	
	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private "+controllerClass.getQualifiedSourceName()+ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX+" _controller;");
		srcWriter.println("private "+DeviceAdaptiveViewContainer.class.getCanonicalName()+ " viewContainer = new "+DeviceAdaptiveViewContainer.class.getCanonicalName()+"();");
	    for (String messageClass: viewFactoryCreator.getDeclaredMessages().keySet())
	    {
	    	srcWriter.println("private "+messageClass+" "+viewFactoryCreator.getDeclaredMessages().get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
	    srcWriter.println("private static "+Logger.class.getCanonicalName()+" "+viewFactoryCreator.getLoggerVariable()+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getProxySimpleName()+".class.getName());");
		srcWriter.println("private static int _idGen = 0;");
    }

	/**
	 * 
	 */
	@Override
    protected void generateWrapperMethod(JMethod method, SourcePrinter srcWriter)
    {
		if (mustDelegateToController(method))
		{
			JType returnType = method.getReturnType().getErasedType();

			srcWriter.println(method.getReadableDeclaration(false, false, false, false, true)+"{");
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

			srcWriter.println("}");
		}
    }

	@Override
    protected String[] getImports()
    {
	    return new String[]{
	    		ScreenFactory.class.getCanonicalName(),
	    		Screen.class.getCanonicalName(), 
	    		GWT.class.getCanonicalName()
	    };
    }

	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = DeviceAdaptiveController.class.getPackage().getName();
		PrintWriter printWriter = context.getGeneratorContext().tryCreate(context.getLogger(), packageName, getProxySimpleName());

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

		return new SourcePrinter(composerFactory.createSourceWriter(context.getGeneratorContext(), printWriter), context.getLogger());
	}

    /**
	 * 
	 * @return
	 */
	protected void initializeController(View view)
	{
		controllerName = templateParser.getTemplateController(view, baseIntf.getQualifiedSourceName(), device);
		String controllerClassName;
        try
        {
	        controllerClassName = context.getControllers().getController(controllerName, device);
        }
        catch (ResourceNotFoundException e)
        {
			throw new CruxGeneratorException("Error generating invoker. Controller ["+controllerName+"] not found.");
		}
		
		controllerClass = context.getGeneratorContext().getTypeOracle().findType(controllerClassName);
		if (controllerClass == null)
		{
    		String message = "Controller class ["+controllerName+"] , declared on view ["+view.getId()+"], could not be loaded. "
					   + "\n Possible causes:"
					   + "\n\t 1. Check if any type or subtype used by controller refers to another module and if this module is inherited in the .gwt.xml file."
					   + "\n\t 2. Check if your controller or its members belongs to a client package."
					   + "\n\t 3. Check the versions of all your modules.";
			
			throw new CruxGeneratorException(message);
		}
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
	protected void initializeTemplateParser()
    {
	    Device[] devices = Devices.getDevicesForDevice(getDeviceFeatures());
	    for (Device device : devices)
        {
	    	this.device = device;
	    	templateParser = new CrossDevicesTemplateParser(context, baseIntf, device);
	    	templateResource = templateParser.getDeviceAdaptiveTemplateResource();
	    	if (templateResource != null)
	    	{
	    		break;
	    	}
        }
	    if (templateResource == null)
	    {
	    	throw new CruxGeneratorException("DeviceAdaptive widget does not declare any valid template for device ["+getDeviceFeatures()+"].");
	    }
    }

	protected boolean mustDelegateToController(JMethod method)
    {
	    JClassType enclosingType = method.getEnclosingType();
		return (!enclosingType.equals(deviceAdaptiveClass) && !enclosingType.equals(hasHandlersClass));
    }
	
	private void initializeLastCompilationVariables()
	{
		CachedGeneratorResult lastResult = context.getGeneratorContext().getCachedGeneratorResult();
		if (lastResult == null || !context.getGeneratorContext().isGeneratorResultCachingEnabled())
		{
			lastCompilationTime =  -1;
		}
		else
		{
			try
			{
				lastCompilationTime = lastResult.getTimeGenerated();
			}
			catch(RuntimeException e)
			{
				lastCompilationTime = -1;
			}
		}
	}

	private boolean isChanged(View view)
	{
		return (view.getLastModified() >= lastCompilationTime);
	}	
}
