/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.screen.resources;

import java.io.PrintWriter;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.resources.Resources;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourcesHandlerProxyCreator extends AbstractProxyCreator
{

	private final String resourceId;
	private final View view;
	private String loggerVariable;
	private final Device device;

	public ResourcesHandlerProxyCreator(TreeLogger logger, GeneratorContext context, String resourceId, View view, String devive)
    {
	    super(logger, context, false);
		this.resourceId = resourceId;
		this.view = view;
		this.device = Device.valueOf(devive);
		this.loggerVariable = ViewFactoryCreator.createVariableName("logger");
		
    }
	
	@Override
	protected void generateProxyFields(SourcePrinter printer) throws CruxGeneratorException
	{
		printer.println("private static "+Logger.class.getCanonicalName()+" "+loggerVariable+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getProxySimpleName()+".class.getName());");
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter printer) throws CruxGeneratorException
	{
    	String resourceClass = Resources.getResource(resourceId, device);
    	if (resourceClass == null)
    	{
    		throw new CruxGeneratorException("Resource ["+resourceId+"], declared on View ["+view.getId()+"] could not be found for device ["+device.name()+"].");
    	}
    	String resourceVariable = ViewFactoryCreator.createVariableName("resource");

    	printer.println("public static void init(){");
    	printer.println("if (!View.containsResource("+EscapeUtils.quote(resourceId)+")){");
    	printer.println(resourceClass + " " + resourceVariable + "= GWT.create("+resourceClass+".class);");
    	generateCssInjectionResources(printer, resourceVariable, resourceId, resourceClass);
    	printer.println("View.addResource("+EscapeUtils.quote(resourceId)+", "+resourceVariable+");");

    	printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".info(Crux.getMessages().resourcesInitialized("+EscapeUtils.quote(resourceId)+"));");
		printer.println("}");
    	
		printer.println("}");
    	printer.println("}");
    }

	/**
	 * 
	 * @param printer
	 * @param resourceVariable
	 * @param resourceKey
	 * @param resourceClass
	 */
	private void generateCssInjectionResources(SourcePrinter printer, String resourceVariable, String resourceKey, String resourceClass)
    {
		JClassType resourceType = context.getTypeOracle().findType(resourceClass);
		
    	if (resourceType == null)
    	{
    		throw new CruxGeneratorException("Resource ["+resourceKey+"], declared on View ["+view.getId()+"] could not be found.");
    	}
    	
    	JMethod[] methods = resourceType.getOverridableMethods();
    	if (methods != null)
    	{
    		JClassType cssResourceType = context.getTypeOracle().findType(CssResource.class.getCanonicalName());		
    		for (JMethod method : methods)
            {
	            if (method.getReturnType().isClassOrInterface().isAssignableTo(cssResourceType))
	            {
	    	    	printer.println(resourceVariable+"."+method.getName()+"().ensureInjected();");
	    	    	printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
	    			printer.println(loggerVariable+".info(Crux.getMessages().resourceCsssInjected("+EscapeUtils.quote(method.getReturnType().getSimpleSourceName())+"));");
	    			printer.println("}");
	            }
            }
    	}
    }

	@Override
    public String getProxyQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getProxySimpleName();
    }

	@Override
    public String getProxySimpleName()
    {
		String className = this.resourceId+"_"+device.toString();
		className = className.replaceAll("[\\W]", "_");
		return className;
    }

	@Override
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
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

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
    }
	
    /**
	 * Gets the list of classes used by the ResourcesHandler.
	 *
     * @return
     */
    String[] getImports()
    {
        String[] imports = new String[] {
        	Crux.class.getCanonicalName(),	
    		GWT.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName()
		};
	    return imports;
	}
}
