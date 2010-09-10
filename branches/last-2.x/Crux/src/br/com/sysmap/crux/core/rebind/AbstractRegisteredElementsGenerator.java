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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.ScreenConfigException;
import br.com.sysmap.crux.core.rebind.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractRegisteredElementsGenerator extends AbstractGenerator
{
	
	/**
	 * 
	 */
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException 
	{
		try 
		{
			TypeOracle typeOracle = context.getTypeOracle(); 
			JClassType classType = typeOracle.getType(typeName);
			
			List<Screen> screens = getScreens(logger);
			
			String packageName = classType.getPackage().getName();
			String className = classType.getSimpleSourceName() + "Impl";
			generateClass(logger, context, classType, screens);
			return packageName + "." + className;
		} 
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElement(e.getLocalizedMessage()), e);
			throw new UnableToCompleteException();
		}
	}

	/**
	 * 
	 * @param logger
	 * @return
	 * @throws UnableToCompleteException
	 * @throws ScreenConfigException
	 */
	protected List<Screen> getScreens(TreeLogger logger) throws UnableToCompleteException, ScreenConfigException
	{
		List<Screen> screens = new ArrayList<Screen>();

		Screen requestedScreen = getRequestedScreen(logger);
		
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
	
	/**
	 * 
	 * @param logger
	 * @return
	 * @throws UnableToCompleteException
	 * @throws ScreenConfigException
	 */
	protected Screen getRequestedScreen(TreeLogger logger) throws UnableToCompleteException, ScreenConfigException
	{
		String screenID; 
		try
		{
			screenID = CruxScreenBridge.getInstance().getLastPageRequested();
		}
		catch (Throwable e) 
		{
			logger.log(TreeLogger.ERROR, messages.errorGeneratingRegisteredElementInvalidScreenID(),e);
			throw new UnableToCompleteException();
		}
		Screen screen = ScreenFactory.getInstance().getScreen(screenID);
		return screen;
	}
	
	/**
	 * 
	 * @param logger
	 * @param context
	 * @param classType
	 * @param screens
	 */
	protected abstract void generateClass(TreeLogger logger, GeneratorContext context, JClassType classType, List<Screen> screens) ;
}
