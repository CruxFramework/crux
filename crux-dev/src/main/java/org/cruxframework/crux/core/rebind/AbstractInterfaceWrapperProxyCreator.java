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
package org.cruxframework.crux.core.rebind;

import java.io.PrintWriter;

import org.cruxframework.crux.core.rebind.context.RebindContext;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.SelectionProperty;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * 
 * Base class for all generators that create a smart stub for a base interface
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractInterfaceWrapperProxyCreator extends AbstractProxyCreator
{
	private static final String PROXY_SUFFIX = "_Impl";
	protected JClassType baseIntf;

	public AbstractInterfaceWrapperProxyCreator(RebindContext context, JClassType baseIntf, boolean cacheable)
    {
	    super(context, cacheable);
		this.baseIntf = baseIntf;
    }

	/**
	 * @return the full qualified name of the proxy object.
	 */
	@Override
	public String getProxyQualifiedName()
	{
		return baseIntf.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	@Override
	public String getProxySimpleName()
	{
		JClassType enclosingType = baseIntf.getEnclosingType();
		String enclosingTypeName = (enclosingType==null?"":enclosingType.getSimpleSourceName()+"_");
		return enclosingTypeName+baseIntf.getSimpleSourceName() + PROXY_SUFFIX;
	}

	/**
	 * 
	 * @return
	 */
	protected String getUserAgent()
	{
		try
		{
			SelectionProperty userAgent = context.getGeneratorContext().getPropertyOracle().getSelectionProperty(context.getLogger(), "user.agent");
			return userAgent==null?null:userAgent.getCurrentValue();
		}
		catch (BadPropertyValueException e)
		{
			throw new CruxGeneratorException("Can not read user.agent property.",e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getDeviceFeatures()
	{
		return GeneratorProperties.readSelectionPropertyValue(context, GeneratorProperties.DEVICE_FEATURES);
	}
	
		
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourcePrinter getSourcePrinter()
	{
		JPackage pkg = baseIntf.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
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

		return new SourceCodePrinter(composerFactory.createSourceWriter(context.getGeneratorContext(), printWriter), context.getLogger());
	}
	
	/**
	 * @return
	 */
	protected boolean findCacheableImplementationAndMarkForReuseIfAvailable()
	{
		return findCacheableImplementationAndMarkForReuseIfAvailable(baseIntf);
	}
	
	/**
	 * @return the list of imports required by proxy
	 */
	protected abstract String[] getImports();
}
