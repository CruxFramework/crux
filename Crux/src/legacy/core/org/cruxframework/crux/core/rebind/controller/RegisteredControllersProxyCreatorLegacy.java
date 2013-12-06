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
package org.cruxframework.crux.core.rebind.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.RegisteredControllers;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;
import org.cruxframework.crux.core.client.event.CrossDocumentInvoker;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * Generates a RegisteredControllers class. 
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Legacy(value=RegisteredControllersProxyCreator.class)
public class RegisteredControllersProxyCreatorLegacy extends AbstractInterfaceWrapperProxyCreator
{
	@Legacy
	@Deprecated
	private Map<String, String> crossDocsClassNames = new HashMap<String, String>();
	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyMethods(SourcePrinter sourceWriter) throws CruxGeneratorException
    {
		generateControllerInvokeMethod(sourceWriter);
		generateCrossDocInvokeMethod(sourceWriter);
		generateGetControllertMethod(sourceWriter);
    }

	
	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<ControllerInvoker> controllers = new FastMap<ControllerInvoker>();");
		srcWriter.println("private "+org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName()+" view;");
		srcWriter.println("private "+iocContainerClassName+" iocContainer;");
    }	

	
	/**
	 * Generate the block to include controller object.
	 * @param controller
	 * @param module
	 */
	private void generateControllerBlock(String controller, String module)
	{
		try
		{
			JClassType controllerClass = getControllerClass(controller);
			if (!controllerClassNames.containsKey(controller) && controllerClass!= null)
			{
				String controllerClassName = controllerClass.getQualifiedSourceName();
				if (Modules.getInstance().isClassOnModulePath(controllerClassName, module))
				{
					String genClass = new ControllerProxyCreator(logger, context, controllerClass).create();
					controllerClassNames.put(controller, genClass);
					JClassType crossDocumentType = controllerClass.getOracle().getType(CrossDocument.class.getCanonicalName());
					if (crossDocumentType.isAssignableFrom(controllerClass))
					{
						crossDocsClassNames.put(controller, genClass);
					}
				}
			}
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException("Error for register client event handler. Controller: ["+controller+"].", e);
		}
	}	
	
	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.EventProcessor.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		FastMap.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.ControllerInvoker.class.getCanonicalName(),
    		CrossDocumentInvoker.class.getCanonicalName(), 
    		StringUtils.class.getCanonicalName()
		};
	    return imports;
    }	
	
	/**
	 * 
	 * @param sourceWriter
	 * @param controllerClassNames
	 * @throws CruxGeneratorException 
	 */
	@Legacy
	@Deprecated
	private void generateControllerInvokeMethod(SourcePrinter sourceWriter) throws CruxGeneratorException
	{
		sourceWriter.println("public void invokeController(final String controllerName, final String method, final boolean fromOutOfModule, final Object sourceEvent, final EventProcessor eventProcessor){");

		if (isCrux2OldInterfacesCompatibilityEnabled())
		{
		    sourceWriter.println("ControllerInvoker controller = getController(controllerName);");
			sourceWriter.println("if (controller != null){");
			sourceWriter.println("try{");
			sourceWriter.println("controller.invoke(method, sourceEvent, fromOutOfModule, eventProcessor);");
			sourceWriter.println("}");
			sourceWriter.println("catch (Exception e)"); 
			sourceWriter.println("{");
			sourceWriter.println("eventProcessor.setException(e);");
			sourceWriter.println("}");
			sourceWriter.println("return;");
			sourceWriter.println("}");
			sourceWriter.println("else {");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote("To use this feature you need to enable crux2 old interfaces compatibility.")+");");
		}
		sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	@Legacy
	@Deprecated
	private void generateCrossDocInvokeMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public String invokeCrossDocument(String serializedData){");

		if (!this.crossDocsClassNames.isEmpty())
		{
			sourceWriter.println("if (serializedData != null){");

			sourceWriter.println("int idx = serializedData.indexOf('|');");
			sourceWriter.println("if (idx > 0){");

			sourceWriter.println("String controllerName = null;");
			sourceWriter.println("try{");

			sourceWriter.println("controllerName = serializedData.substring(0,idx);");
			sourceWriter.println("serializedData = serializedData.substring(idx+1);");
			sourceWriter.println("CrossDocumentInvoker crossDoc = (CrossDocumentInvoker)getController(controllerName);");
			sourceWriter.println("if (crossDoc==null){");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.println("} else {");

			sourceWriter.println("return crossDoc.invoke(serializedData);");

			sourceWriter.println("}");

			sourceWriter.println("} catch(ClassCastException ex){");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().crossDocumentInvalidCrossDocumentController(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.println("}");

			sourceWriter.println("}");

			sourceWriter.println("}");
		}
		sourceWriter.println("return null;");
		sourceWriter.println("}");
	}
}
