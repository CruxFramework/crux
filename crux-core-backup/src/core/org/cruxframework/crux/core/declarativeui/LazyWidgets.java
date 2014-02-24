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
package org.cruxframework.crux.core.declarativeui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.rebind.screen.ViewFactory;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetConfig;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyCondition;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildLazyConditions;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.utils.HTMLUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LazyWidgets
{
	/**
	 * Checkers for widgets that lazily create its children
	 */
	private static Map<String, WidgetLazyChecker> lazyWidgetCheckers = new HashMap<String, WidgetLazyChecker>();
	
	/**
	 * Lazy checker for invisible panels
	 */
	private WidgetLazyChecker defaultLazyChecker = new WidgetLazyChecker() 
	{
		public boolean isLazy(JSONObject widget) 
		{
			return widget.has("visible") && !widget.optBoolean("visible");
		}
	};

	private final boolean escapeXML;
	
	public LazyWidgets(boolean escapeXML)
    {
		this.escapeXML = escapeXML;
    }
	
	/**
	 * @param childrenMethod
	 * @param factoryHelper
	 * @param declaredCheckers
	 * @throws NotFoundException
	 */
	private static void initializeLazyChecker(Class<?> factoryType, List<WidgetLazyChecker> declaredCheckers, Set<String> added) 
    {
		String className = factoryType.getCanonicalName();
		if (!added.contains(className))
		{
			added.add(className);
			TagChildren tagChildren = factoryType.getAnnotation(TagChildren.class);
			if (tagChildren != null)
			{
				for (TagChild child : tagChildren.value())
				{
					Class<?> childProcessor = child.value();
					TagChildLazyConditions lazyConditions = childProcessor.getAnnotation(TagChildLazyConditions.class);
					if (lazyConditions != null)
					{
						WidgetLazyChecker lazyChecker = initializeLazyChecker(lazyConditions);
						if (lazyChecker != null)
						{
							declaredCheckers.add(lazyChecker);
						}
					}
					initializeLazyChecker(childProcessor, declaredCheckers, added);
				}
			}
		}
    }
	
	/**
	 * @param type
	 * @throws ClassNotFoundException 
	 * @throws NotFoundException 
	 */
	private static void initializeLazyChecker(String type) throws ClassNotFoundException
	{
		String widgetFactoryClass = WidgetConfig.getClientClass(type);
		Class<?> factoryType = Class.forName(widgetFactoryClass);
		
		final List<WidgetLazyChecker> declaredCheckers = new ArrayList<WidgetLazyChecker>();
		initializeLazyChecker(factoryType, declaredCheckers, new HashSet<String>());
		if (declaredCheckers.size() == 0)
		{
			lazyWidgetCheckers.put(type, null);
		}
		else if (declaredCheckers.size() == 1)
		{
			lazyWidgetCheckers.put(type, declaredCheckers.get(0));
		}
		else
		{
			lazyWidgetCheckers.put(type, new WidgetLazyChecker()
			{
				public boolean isLazy(JSONObject widget)
				{
					boolean ret = false;
					for (WidgetLazyChecker widgetLazyChecker : declaredCheckers)
                    {
	                    ret = ret || widgetLazyChecker.isLazy(widget);
                    }
					return ret;
				}
			});
		}
		
	}

	/**
	 * @param lazyConditions
	 * @return
	 */
	public static WidgetLazyChecker initializeLazyChecker(final TagChildLazyConditions lazyConditions)
    {
	    if (lazyConditions.all().length > 0)
	    {
	    	return new WidgetLazyChecker()
			{
				public boolean isLazy(JSONObject widget)
				{
					boolean lazy = true;
					for (TagChildLazyCondition lazyCondition : lazyConditions.all())
                    {
						String property = lazyCondition.property();
						if (lazyCondition.equals().length() > 0)
						{
							lazy = lazy && (widget.has(property) && widget.optString(property).equals(lazyCondition.equals()));
						}
						else if (lazyCondition.notEquals().length() > 0)
						{
							lazy = lazy && (!widget.has(property) || !widget.optString(property).equals(lazyCondition.notEquals()));
						}
						if (!lazy)
						{
							break;
						}
                    }
					
					return lazy;
				}
			}; 
	    }
	    else if (lazyConditions.any().length > 0)
	    {
	    	return new WidgetLazyChecker()
			{
				public boolean isLazy(JSONObject widget)
				{
					boolean lazy = false;
					for (TagChildLazyCondition lazyCondition : lazyConditions.any())
                    {
						String property = lazyCondition.property();
						if (lazyCondition.equals().length() > 0)
						{
							lazy = lazy || (widget.has(property) && widget.optString(property).equals(lazyCondition.equals()));
						}
						else if (lazyCondition.notEquals().length() > 0)
						{
							lazy = lazy || (!widget.has(property) || !widget.optString(property).equals(lazyCondition.notEquals()));
						}
						if (lazy)
						{
							break;
						}
                    }
					
					return lazy;
				}
			}; 
	    }
	    return null;
    }

	public String generateScreenLazyDeps(String cruxMetadataArray) throws LazyExeption 
	{
    	try
        {
	        JSONArray meta = new JSONArray(cruxMetadataArray);
	        JSONObject dependencies = new JSONObject();
	        
	        int length = meta.length();
	        for (int i = 0; i < length; i++) 
	        {
	        	JSONObject compCandidate = meta.getJSONObject(i);
	        	
	        	generateScreenLazyDeps(dependencies, compCandidate);
	        }
	        
	        return dependencies.toString();
        }
        catch (Exception e)
        {
        	throw new LazyExeption(e.getMessage(), e);
        }
	}

	/**
	 * Return true if the parent widget informed, must render its children lazily.
	 * 
	 * @param parent
	 * @return
	 * @throws JSONException 
	 * @throws ClassNotFoundException 
	 */
	private boolean checkChildrenLazy(JSONObject parent) throws JSONException, ClassNotFoundException 
	{
		String parentType = parent.getString("_type");
		if (!lazyWidgetCheckers.containsKey(parentType))
		{
			initializeLazyChecker(parentType);
		}
		WidgetLazyChecker checker = lazyWidgetCheckers.get(parentType);
		return checker != null && checker.isLazy(parent);
	}

	/**
	 * @param dependencies
	 * @param widget
	 * @throws JSONException
	 * @throws ClassNotFoundException 
	 */
	private void checkChildrenLazyDeps(JSONObject dependencies, JSONObject widget) throws JSONException, ClassNotFoundException
    {
	    if (widget.has("_children"))
	    {
	    	JSONArray children = widget.getJSONArray("_children");
	    	int length = children.length();
	    	for (int i = 0; i < length; i++) 
	    	{
	    		JSONObject child = children.getJSONObject(i);
	    		generateScreenLazyDeps(dependencies, child);
	    	}
	    }
    }
	
	/**
	 * Return true if the widget informed, must be rendered lazily.
	 * 
	 * @param widget
	 * @return
	 * @throws JSONException 
	 */
	private boolean checkLazy(JSONObject widget) throws JSONException
	{
		return defaultLazyChecker.isLazy(widget);
	}
	
	/**
	 * Search the widget's children adding lazy dependencies between all children and the parent informed.
	 * 
	 * @param widget
	 * @param parentId
	 * @param dependencies
	 * @throws JSONException 
	 * @throws ClassNotFoundException 
	 */
	private void generateLazyDepsForChildren(JSONObject widget, String parentId, JSONObject dependencies) throws JSONException, ClassNotFoundException
	{
		if (widget.has("_children"))
		{
			ViewFactory factory = ViewFactory.getInstance();
			JSONArray children = widget.getJSONArray("_children");
			int size = children.length();
			for (int i=0; i<size; i++)
			{
				JSONObject child = children.getJSONObject(i);
				if (child != null)
				{
					String lazyId = parentId;
					if (factory.isValidWidget(child))
					{
						String childId = child.getString("id");
					    addDependency(dependencies, childId, parentId);
						if (checkLazy(child))
						{
							lazyId = ViewFactoryUtils.getLazyPanelId(childId, LazyPanelWrappingType.wrapWholeWidget);
						}
						else if (checkChildrenLazy(child))
						{
							lazyId = ViewFactoryUtils.getLazyPanelId(childId, LazyPanelWrappingType.wrapChildren);
						}
					}
					generateLazyDepsForChildren(child, lazyId, dependencies);
				}
			}
		}		
	}

	/**
	 * @param dependencies
	 * @param widget
	 * @throws JSONException
	 * @throws ClassNotFoundException 
	 */
	private void generateScreenLazyDeps(JSONObject dependencies, JSONObject widget) throws JSONException, ClassNotFoundException
    {
		if (ViewFactory.getInstance().isValidWidget(widget))
		{
			boolean wholeWidgetLazy = checkLazy(widget);
			boolean widgetChildrenLazy = checkChildrenLazy(widget);

			if (wholeWidgetLazy || widgetChildrenLazy)
			{
				String widgetId = widget.getString("id");
				String wrapperId = null;
				if (wholeWidgetLazy)
				{
					wrapperId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
					addDependency(dependencies, widgetId, wrapperId);

				}
				/*
				 * if both 'wholeWidgetLazy' and 'widgetChildrenLazy' are true, all widgets children must be dependent
				 * of a wrapper lazyPanel created for the children and not be dependent of the a wrapper created over its parent.
				 */
				if (widgetChildrenLazy)
				{
					wrapperId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapChildren);
				}
				generateLazyDepsForChildren(widget, wrapperId, dependencies);
			}
			else
			{
				checkChildrenLazyDeps(dependencies, widget);
			}
		}
		else
		{
			checkChildrenLazyDeps(dependencies, widget);
		}
    }

	/**
	 * @param dependencies
	 * @param widgetId
	 * @param wrapperId
	 * @return
	 * @throws JSONException
	 */
	private JSONObject addDependency(JSONObject dependencies, String widgetId, String wrapperId) throws JSONException
    {
	    return dependencies.put(HTMLUtils.escapeJavascriptString(widgetId, escapeXML), HTMLUtils.escapeJavascriptString(wrapperId, escapeXML));
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface WidgetLazyChecker
	{
		boolean isLazy(JSONObject widget);
	}	
}
