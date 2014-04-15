/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.core.rebind.ioc;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;

import org.cruxframework.crux.core.client.ioc.RuntimeIoCContainer;
import org.cruxframework.crux.core.client.ioc.Inject.Scope;
import org.cruxframework.crux.core.client.ioc.IoCContainerException;
import org.cruxframework.crux.core.client.ioc.IocContainer;
import org.cruxframework.crux.core.client.ioc.IocProvider;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.ioc.IocConfig;
import org.cruxframework.crux.core.ioc.IocConfigImpl;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RuntimeContainerProxyCreator extends IocContainerRebind
{

	public RuntimeContainerProxyCreator(TreeLogger logger, GeneratorContext ctx, JClassType baseIntf)
    {
		super(logger, ctx, null, getDeviceFeatures(logger, ctx));
    }

	@Override
	protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyFields(srcWriter);
	    srcWriter.println("private IocContainer baseContainer;");
	}
	
	@Override
	protected void generateProxyContructor(SourcePrinter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.println("}");
	}
	
	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyMethods(srcWriter);
	    generateGetMethod(srcWriter);
	    generateSetContainerMethod(srcWriter);
	    generateGetScopeMethod(srcWriter);
	}

	private void generateSetContainerMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("public void setIoCContainer(IocContainer iocContainer) {");
	    srcWriter.println("baseContainer = iocContainer;");
	    srcWriter.println("}");
    }

	private void generateGetScopeMethod(SourcePrinter srcWriter)
    {
	    srcWriter.println("protected IocScope _getScope(Scope scope){");
		srcWriter.println("return (baseContainer==null?null:baseContainer._getScope(scope));");
		srcWriter.println("}");
    }
	
	protected void generateGetMethod(SourcePrinter srcWriter)
    {
		srcWriter.println("public <T> T get(Class<T> clazz, Scope scope, String subscope){");

		srcWriter.println("String className = clazz.getName();");
		Iterator<Entry<String, IocConfig<?>>> classes = configurations.entrySet().iterator();
		boolean needsElse = false;
		while (classes.hasNext())
		{
			Entry<String, IocConfig<?>> entry = classes.next();
			IocConfigImpl<?> iocConfig = (IocConfigImpl<?>)entry.getValue();
			String className = entry.getKey();
			if (iocConfig.isRuntimeAccessible())
			{
				if (needsElse)
				{
					srcWriter.print("else ");
				}
				needsElse = true;
				srcWriter.println("if (StringUtils.unsafeEquals(className, "+EscapeUtils.quote(className)+")){");
				srcWriter.println("return (T) get"+className.replace('.', '_')+"(scope, subscope);");
				srcWriter.println("}");
			}
		}
		
		srcWriter.println("throw new IoCContainerException(\"Class not bound to IoCContainer [\"+className+\"]\");");
		srcWriter.println("}");
    }

	@Override
	protected String[] getImports()
	{
	    String[] imports = new String[] {
		    	org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName(),	
	    		GWT.class.getCanonicalName(),
	    		IoCContainerException.class.getCanonicalName(), 
	    		Scope.class.getCanonicalName(), 
	    		StringUtils.class.getCanonicalName(),
	    		IocContainer.class.getCanonicalName()
			};
		    return imports;
	}
	
	
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		String packageName = IocProvider.class.getPackage().getName();
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
		
		composerFactory.addImplementedInterface(RuntimeIoCContainer.class.getCanonicalName());
		
		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getDeviceFeatures(TreeLogger logger, GeneratorContext context)
	{
		try
		{
			SelectionProperty device = context.getPropertyOracle().getSelectionProperty(logger, "device.features");
			return device==null?null:device.getCurrentValue();
		}
		catch (BadPropertyValueException e)
		{
			throw new CruxGeneratorException("Can not read device.features property.", e);
		}
	}
}
