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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.ScreenFactory;
import org.cruxframework.crux.core.client.screen.ViewFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoriesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, Set<Screen>> fragmentedScreens = new HashMap<String, Set<Screen>>();

	/**
	 * @param logger
	 * @param context
	 */
	public ViewFactoriesProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context, context.getTypeOracle().findType(ViewFactory.class.getCanonicalName()), false);
    }
	
	@Override
    protected void generateProxyMethods(SourceWriter sourceWriter) throws CruxGeneratorException
    {
		generateCreateViewMethod(sourceWriter);
		generateGetCurrentDeviceMethod(sourceWriter);
    }

	/**
	 * 
	 * @param sourceWriter
	 */
	protected void generateCreateViewMethod(SourceWriter sourceWriter)
    {
	    sourceWriter.println("public void createView(String screenId) throws InterfaceConfigException{ ");
		sourceWriter.indent();

		if (Environment.isProduction())
		{
			generateViewCreationForAllScreens(sourceWriter);
		}
		else
		{
			generateViewCreationForCurrentScreen(sourceWriter);
		}
		
		sourceWriter.outdent();
		sourceWriter.println("}");

		if (Environment.isProduction())
		{
			generateFragmentedViewFactoryCreation(sourceWriter);
		}
    }

	/**
	 * 
	 * @param sourceWriter
	 */
	protected void generateGetCurrentDeviceMethod(SourceWriter sourceWriter)
    {
	    sourceWriter.println("public "+Device.class.getCanonicalName()+" getCurrentDevice(){ ");
		sourceWriter.indent();

		sourceWriter.println("return "+Device.class.getCanonicalName()+"."+getDeviceFeatures()+";");

		sourceWriter.outdent();
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 */
	private void generateViewCreationForCurrentScreen(SourceWriter sourceWriter) 
	{
			Screen screen = getCurrentScreen();
			generateViewCreator(sourceWriter, screen);
	}

	/**
	 * @param sourceWriter
	 * @param screens
	 */
	protected void generateViewCreationForAllScreens(SourceWriter sourceWriter) 
	{
		List<Screen> screens = getScreens();
		
		boolean first = true;
		for (Screen screen : screens)
        {
			if (!first)
			{
				sourceWriter.print("else ");
			}
			first = false;
			
			sourceWriter.println("if (StringUtils.unsafeEquals(screenId, "+EscapeUtils.quote(screen.getModule()+"/"+screen.getRelativeId())+")){");
			sourceWriter.indent();
			
			if (!StringUtils.isEmpty(screen.getFragment()))
			{
				Set<Screen> fragment = fragmentedScreens.get(screen.getFragment());
				if (fragment == null)
				{
					fragment = new HashSet<Screen>();
					fragmentedScreens.put(screen.getFragment(), fragment);
				}
				fragment.add(screen);
				String fragmentName = screen.getFragment().replaceAll("\\W", "");
				sourceWriter.println("__load"+fragmentName+"(screenId);");
			}
			else
			{
				generateViewCreator(sourceWriter, screen);
			}

			sourceWriter.outdent();
			sourceWriter.println("}");
        }
	}	
	
	/**
	 * @param sourceWriter
	 * @param controllerClassNames
	 * @param controller
	 * @param controllerAnnot
	 */
	protected void generateFragmentedViewFactoryCreation(SourceWriter sourceWriter)
    {
		for (String screenFragment : fragmentedScreens.keySet())
        {
			String fragment = screenFragment.replaceAll("\\W", "");
			sourceWriter.println("public void __load"+fragment+"(final String screenId){");
			sourceWriter.indent();
			sourceWriter.println("GWT.runAsync(new "+RunAsyncCallback.class.getCanonicalName()+"(){");
			sourceWriter.indent();
			sourceWriter.println("public void onFailure(Throwable reason){");
			sourceWriter.indent();
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().viewFactoryCanNotBeLoaded(\""+fragment+"\"));");
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.println("public void onSuccess(){");
			sourceWriter.indent();
			
			Set<Screen> screens = fragmentedScreens.get(screenFragment);
			
			boolean first = true;
			for (Screen screen : screens)
            {
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				
				sourceWriter.println("if (StringUtils.unsafeEquals(screenId, "+EscapeUtils.quote(screen.getModule()+"/"+screen.getRelativeId())+")){");
				sourceWriter.indent();
				
				generateViewCreator(sourceWriter, screen);

				sourceWriter.outdent();
				sourceWriter.println("}");
            }
			
	        
			sourceWriter.outdent();
			sourceWriter.println("}");
			sourceWriter.outdent();
			sourceWriter.println("});");
			sourceWriter.outdent();
			sourceWriter.println("}");
        } 
    }
	
	
	/**
	 * @param sourceWriter
	 * @param screen
	 */
	private void generateViewCreator(SourceWriter sourceWriter, Screen screen)
    {
		ViewFactoryCreator factoryCreator = getViewFactoryCreator(screen);
		try
		{
			sourceWriter.println("new "+ factoryCreator.create()+"().create();");
			sourceWriter.println(org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName()+".createCrossDocumentAccessor("+ScreenFactory.class.getCanonicalName()+".getInstance().getScreen());");
		}
		finally
		{
			factoryCreator.prepare(null, null, null);
		}
    }

	/**
	 * @param screen
	 * @return
	 */
	private ViewFactoryCreator getViewFactoryCreator(Screen screen)
	{
		if (Environment.isProduction())
		{
			return new ViewFactoryCreator(context, logger, screen, getDeviceFeatures());
		}
		else
		{
			ViewFactoryCreator factory = screen.getFactory();
			if (factory == null)
			{
				factory = new ViewFactoryCreator(context, logger, screen, getDeviceFeatures());
				screen.setFactory(factory);
			}
			else
			{
				factory.prepare(context, logger, getDeviceFeatures());
			}
			return factory;
		}
	}
	
	@Override
	protected String[] getImports()
	{
		String[] imports = new String[] {
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				FastMap.class.getCanonicalName(),
				ViewFactory.class.getCanonicalName(),
				StringUtils.class.getCanonicalName(),
				com.google.gwt.user.client.ui.Widget.class.getCanonicalName(), 
				WidgetCreatorContext.class.getCanonicalName(), 
				InterfaceConfigException.class.getCanonicalName()
		};
		return imports;
	}
	
	@Override
	public String getProxySimpleName()
	{
	    return super.getProxySimpleName()+"_"+this.getDeviceFeatures();
	}
}
