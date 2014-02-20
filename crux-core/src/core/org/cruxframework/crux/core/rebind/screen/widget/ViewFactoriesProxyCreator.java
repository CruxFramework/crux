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
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.DisplayHandler;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoriesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, Set<View>> fragmentedViews = new HashMap<String, Set<View>>();

	/**
	 * @param logger
	 * @param context
	 */
	public ViewFactoriesProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(ViewFactory.class.getCanonicalName()), false);
    }
	
	@Override
    protected void generateProxyMethods(SourcePrinter sourceWriter) throws CruxGeneratorException
    {
		generateCreateViewMethod(sourceWriter);
		generateGetCurrentDeviceMethod(sourceWriter);
    }

	/**
	 * 
	 * @param sourceWriter
	 */
	protected void generateCreateViewMethod(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("public void createView(String name, CreateCallback callback) throws InterfaceConfigException{ ");
	    sourceWriter.println("createView(name, name, callback);");
	    sourceWriter.println("}");
	    sourceWriter.println();

	    sourceWriter.println("public void createView(String name, String id, CreateCallback callback) throws InterfaceConfigException{ ");

	    sourceWriter.println("if (callback == null){");
	    sourceWriter.println("callback = CreateCallback.EMPTY_CALLBACK;");
	    sourceWriter.println("}");
	    
	    generateViewCreation(sourceWriter, getViews());

	    sourceWriter.println("}");

	    generateFragmentedViewFactoryCreation(sourceWriter);
    }

	/**
	 * 
	 * @param sourceWriter
	 */
	protected void generateGetCurrentDeviceMethod(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("public "+Device.class.getCanonicalName()+" getCurrentDevice(){ ");
		sourceWriter.println("return "+Device.class.getCanonicalName()+"."+getDeviceFeatures()+";");
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 * @param views
	 */
	protected void generateViewCreation(SourcePrinter sourceWriter, List<View> views) 
	{
		boolean first = true;
		for (View view : views)
        {
			if (!first)
			{
				sourceWriter.print("else ");
			}
			first = false;
			
			sourceWriter.println("if (StringUtils.unsafeEquals(name, "+EscapeUtils.quote(view.getId())+")){");
			
			if (!StringUtils.isEmpty(view.getFragment()))
			{
				Set<View> fragment = fragmentedViews.get(view.getFragment());
				if (fragment == null)
				{
					fragment = new HashSet<View>();
					fragmentedViews.put(view.getFragment(), fragment);
				}
				fragment.add(view);
				String fragmentName = view.getFragment().replaceAll("\\W", "");
				sourceWriter.println("__load"+fragmentName+"(name, id, callback);");
			}
			else
			{
				generateViewCreator(sourceWriter, view);
			}

			if (view.isRootView())
			{
				Device currentDevice = Device.valueOf(getDeviceFeatures());
				if (currentDevice.getSize().equals(Size.small))
				{
					String smallViewport = view.getSmallViewport();
					if (smallViewport != null && smallViewport.length() > 0)
					{
						sourceWriter.println(DisplayHandler.class.getCanonicalName()+".configureViewport("+EscapeUtils.quote(smallViewport, false)+");");
					}
				}
				else
				{
					String largeViewport = view.getLargeViewport();
					if (largeViewport != null && largeViewport.length() > 0)
					{
						sourceWriter.println(DisplayHandler.class.getCanonicalName()+".configureViewport("+EscapeUtils.quote(largeViewport, false)+");");
					}
				}
				if (view.isDisableRefresh())
				{
					sourceWriter.println(Screen.class.getCanonicalName()+".setRefreshEnabled(false);");
				}
			}
			
			sourceWriter.println("}");
        }
		if (!first)
		{
			sourceWriter.println("else ");
		}
		sourceWriter.println("throw new InterfaceConfigException(\"View [\"+name+\"] was not found. Check if you import it using useView attribute.\");");
	}	
	
	/**
	 * @param sourceWriter
	 * @param controllerClassNames
	 * @param controller
	 * @param controllerAnnot
	 */
	protected void generateFragmentedViewFactoryCreation(SourcePrinter sourceWriter)
    {
		for (String viewFragment : fragmentedViews.keySet())
        {
			String fragment = viewFragment.replaceAll("\\W", "");
			sourceWriter.println("public void __load"+fragment+"(final String name, final String id, final CreateCallback callback){");
			sourceWriter.println("GWT.runAsync(new "+RunAsyncCallback.class.getCanonicalName()+"(){");
			sourceWriter.println("public void onFailure(Throwable reason){");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().viewFactoryCanNotBeLoaded(\""+fragment+"\"));");
			sourceWriter.println("}");
			sourceWriter.println("public void onSuccess(){");
			
			Set<View> views = fragmentedViews.get(viewFragment);
			
			boolean first = true;
			for (View view : views)
            {
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				
				sourceWriter.println("if (StringUtils.unsafeEquals(name, "+EscapeUtils.quote(view.getId())+")){");
				generateViewCreator(sourceWriter, view);
				sourceWriter.println("}");
            }
			
			sourceWriter.println("}");
			sourceWriter.println("});");
			sourceWriter.println("}");
        } 
    }
	
	
	/**
	 * @param sourceWriter
	 * @param view
	 */
	private void generateViewCreator(SourcePrinter sourceWriter, View view)
    {
		ViewFactoryCreator factoryCreator = getViewFactoryCreator(view);
		try
		{
			sourceWriter.println("callback.onViewCreated(new "+ factoryCreator.create()+"(id));");
		}
		finally
		{
			factoryCreator.prepare(null, null, null);
		}
    }

	/**
	 * @param view
	 * @return
	 */
	private ViewFactoryCreator getViewFactoryCreator(View view)
	{
		if (Environment.isProduction())
		{
			return new ViewFactoryCreator(context, logger, view, getDeviceFeatures(), getModule());
		}
		else
		{
			ViewFactoryCreator factory = view.getFactory();
			if (factory == null)
			{
				factory = new ViewFactoryCreator(context, logger, view, getDeviceFeatures(), getModule());
				view.setFactory(factory);
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
