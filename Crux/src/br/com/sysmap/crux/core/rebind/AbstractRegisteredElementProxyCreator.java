/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * Base class for all RegisteredXXX classes (RegisteredControllers, RegisteredDataSources, ...)
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractRegisteredElementProxyCreator extends AbstractProxyCreator
{
	private static final String REGISTERD_ELEMENT_PROXY_SUFFIX = "_Impl";
	protected JClassType registeredIntf;

	public AbstractRegisteredElementProxyCreator(TreeLogger logger, GeneratorContext context, JClassType registeredIntf)
    {
	    super(logger, context);
		this.registeredIntf = registeredIntf;
    }

	/**
	 * @return the list of imports required by proxy
	 */
	protected abstract String[] getImports();
	
	/**
	 * @return the full qualified name of the proxy object.
	 */
	@Override
	protected String getProxyQualifiedName()
	{
		return registeredIntf.getPackage().getName() + "." + getProxySimpleName();
	}
	
	/**
	 * @return the simple name of the proxy object.
	 */
	@Override
	protected String getProxySimpleName()
	{
		return ClassUtils.getSourceName(registeredIntf) + REGISTERD_ELEMENT_PROXY_SUFFIX;
	}
	
	/**
	 * 
	 * @param logger
	 * @return
	 * @throws CruxGeneratorException
	 * @throws ScreenConfigException
	 */
	protected Screen getRequestedScreen() throws CruxGeneratorException, ScreenConfigException
	{
		String screenID; 
		try
		{
			screenID = CruxScreenBridge.getInstance().getLastPageRequested();
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementInvalidScreenID(),e);
			throw new CruxGeneratorException();
		}
		Screen screen = ScreenFactory.getInstance().getScreen(screenID);
		return screen;
	}

	/**
	 * 
	 * @param logger
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected List<Screen> getScreens() throws CruxGeneratorException
	{
		try
        {
	        List<Screen> screens = new ArrayList<Screen>();

	        Screen requestedScreen = getRequestedScreen();
	        
	        if(requestedScreen != null)
	        {
	        	Set<String> screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(requestedScreen.getModule());
	        	
	        	if (screenIDs == null)
	        	{
	        		throw new ScreenConfigException(messages.errorGeneratingRegisteredElementModuleNotFound(requestedScreen.getModule()));
	        	}
	        	for (String screenID : screenIDs)
	        	{
	        		Screen screen = ScreenFactory.getInstance().getScreen(screenID);
	        		if(screen != null)
	        		{
	        			screens.add(screen);
	        		}
	        	}
	        }
	        
	        return screens;
        }
        catch (ScreenConfigException e)
        {
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementCanNotFoundScreens(),e);
			throw new CruxGeneratorException();
        }
	}
	
	/**
	 * @return a sourceWriter for the proxy class
	 */
	@Override
	protected SourceWriter getSourceWriter()
	{
		JPackage pkg = registeredIntf.getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
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

		composerFactory.addImplementedInterface(registeredIntf.getQualifiedSourceName());

		return composerFactory.createSourceWriter(context, printWriter);
	}	
}
