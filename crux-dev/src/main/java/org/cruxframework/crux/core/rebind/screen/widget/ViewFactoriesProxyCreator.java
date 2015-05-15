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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.DisplayHandler;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.context.RebindContext;
import org.cruxframework.crux.core.rebind.screen.Screen;
import org.cruxframework.crux.core.rebind.screen.ScreenConfigException;
import org.cruxframework.crux.core.rebind.screen.ScreenFactory;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.server.Environment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.CachedGeneratorResult;
import com.google.gwt.core.ext.TreeLogger;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoriesProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Set<String> changedViews;
	private Map<String, Set<View>> fragmentedViews = new HashMap<String, Set<View>>();
	private long lastCompilationTime;
	private CachedGeneratorResult lastResult;
	private ScreenFactory screenFactory;
	private List<View> views;
	private static Map<String, ViewFactoryCreator> viewFactoryCache = new HashMap<String, ViewFactoryCreator>();

	/**
	 * @param logger
	 * @param context
	 */
	public ViewFactoriesProxyCreator(RebindContext context)
    {
	    super(context, context.getGeneratorContext().getTypeOracle().findType(ViewFactory.class.getCanonicalName()), false);
		this.screenFactory = new ScreenFactory(context);
    }
	
	@Override
	public String create() throws CruxGeneratorException
	{
		initializeViews();
		String className = getProxyQualifiedName();
	    if (!hasChangedView())
	    {
	    	if (findCacheableImplementationAndMarkForReuseIfAvailable())
	    	{
	    		return className;
	    	}
	    }
		if (isAlreadyGenerated(className))
		{
			return className;
		}
		
		SourcePrinter printer = getSourcePrinter();
		if (printer == null)
		{
			return className;
		}

		generateSubTypes(printer);
		generateProxyContructor(printer);
		generateProxyMethods(printer);
		generateProxyFields(printer);
		generateProxyResources();

		printer.commit();
		return className;
	}
	
	@Override
	public String getProxySimpleName()
	{
	    return super.getProxySimpleName()+"_"+this.getDeviceFeatures();
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
	    
		generateViewCreation(sourceWriter, views);

	    sourceWriter.println("}");

	    generateFragmentedViewFactoryCreation(sourceWriter);
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
	 * 
	 * @param sourceWriter
	 */
	protected void generateGetCurrentDeviceMethod(SourcePrinter sourceWriter)
    {
	    sourceWriter.println("public "+Device.class.getCanonicalName()+" getCurrentDevice(){ ");
		sourceWriter.println("return "+Device.class.getCanonicalName()+"."+getDeviceFeatures()+";");
		sourceWriter.println("}");
    }

	@Override
    protected void generateProxyMethods(SourcePrinter sourceWriter) throws CruxGeneratorException
    {
		generateCreateViewMethod(sourceWriter);
		generateGetCurrentDeviceMethod(sourceWriter);
    }

	@Override
	protected void generateProxyResources()
	{
		try
        {
	        screenFactory.generateHostPages(getDeviceFeatures());
        }
        catch (ScreenConfigException e)
        {
	        throw new CruxGeneratorException("Error generating host pages for crux screens.", e);
        }
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
					sourceWriter.println(org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName()+".setRefreshEnabled(false);");
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

	        Set<String> screenIDs = screenFactory.getScreens();

	        for (String screenID : screenIDs)
	        {
	        	Screen screen = screenFactory.getScreen(screenID, getDeviceFeatures());
	        	if(screen != null)
	        	{
	        		screens.add(screen);
	        	}
	        }
	        
	        return screens;
        }
        catch (ScreenConfigException e)
        {
			context.getLogger().log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve module's list of screens.",e);
			throw new CruxGeneratorException();
        }
	}

	/**
	 * 
	 * @return
	 */
	protected void initializeViews()
	{
		initializeLastCompilationVariables();
		views = new ArrayList<View>();
		changedViews = new HashSet<String>();
		if (!Environment.isProduction())
		{
			try
			{
				List<String> viewList = screenFactory.getViewFactory().getViews("*");
				for (String viewName : viewList)
				{
					View innerView = screenFactory.getViewFactory().getView(viewName, getDeviceFeatures());
					if (innerView != null)
					{
						views.add(innerView);
						if (innerView.getLastModified() >= lastCompilationTime)
						{
							changedViews.add(innerView.getId());
						}
					}
				}
			}
			catch (ScreenConfigException e)
			{
				context.getLogger().log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve list of views.",e);
				throw new CruxGeneratorException();
			}
			
		}
		else
		{
			List<Screen> screens = getScreens();
			HashSet<String> added = new HashSet<String>();
			for (Screen screen : screens)
			{
				findViews(screen, views, added);
			}
		}
	}
	
	/**
	 * 
	 * @param screen
	 * @param views
	 * @param added
	 */
	private void findViews(Screen screen, List<View> views, Set<String> added) 
	{
		View rootView = screen.getRootView();
		if (!added.contains(rootView.getId()))
		{
			added.add(rootView.getId());
			views.add(rootView);
			if (screen.getLastModified() >= lastCompilationTime)
			{
				changedViews.add(rootView.getId());
			}
			
			findViews(rootView, views, added);
		}
	}
	
	/**
	 * 
	 * @param view
	 * @param views
	 * @param added
	 */
	private void findViews(View view, List<View> views, Set<String> added) 
	{
		try
		{
			Iterator<String> iterator = view.iterateViews();
			while (iterator.hasNext())
			{
				String viewLocator = iterator.next();
				if (!added.contains(viewLocator))
				{
					added.add(viewLocator);
					
					List<String> viewList = screenFactory.getViewFactory().getViews(viewLocator);
					for (String viewName : viewList)
                    {
						View innerView = screenFactory.getViewFactory().getView(viewName, getDeviceFeatures());
						if (innerView != null)
						{
							views.add(innerView);
							if (innerView.getLastModified() >= lastCompilationTime)
							{
								changedViews.add(innerView.getId());
							}
							
							findViews(innerView, views, added);
						}
                    }
				}
			}
		}
		catch (ScreenConfigException e)
		{
			context.getLogger().log(TreeLogger.ERROR, "Error Generating registered element. Can not retrieve screen's list of views.",e);
			throw new CruxGeneratorException();
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
			factoryCreator.prepare(null, true, null, null);
		}
    }
	
	/**
	 * @param view
	 * @return
	 */
	private ViewFactoryCreator getViewFactoryCreator(View view)
	{
		ViewFactoryCreator factory = viewFactoryCache.get(view.getId());
		if (factory == null)
		{
			factory = new ViewFactoryCreator(context, view, isChanged(view), getDeviceFeatures());
			viewFactoryCache.put(view.getId(), factory);
		}
		else
		{
			factory.prepare(context, isChanged(view), view, getDeviceFeatures());
		}
		return factory;
	}

	private boolean hasChangedView()
	{
		return changedViews.size() > 0;
	}
	
	private void initializeLastCompilationVariables()
	{
		lastResult = context.getGeneratorContext().getCachedGeneratorResult();
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
		return changedViews.contains(view.getId());
	}
}
